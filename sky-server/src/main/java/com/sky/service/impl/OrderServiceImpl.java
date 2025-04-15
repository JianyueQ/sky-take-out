package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.WebSocket.WebSocketServer;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sky.entity.Orders.*;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private WebSocketServer webSocketServer;
    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    @Transactional
    @Override
    public OrderSubmitVO order(OrdersSubmitDTO ordersSubmitDTO) {
        //处理业务异常(地址簿为空,购物车为空)
        AddressBook addressBooks = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBooks == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(BaseContext.getCurrentId())
                .build();
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if (list == null || list.size() == 0) {
            throw new AddressBookBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        Long userId = BaseContext.getCurrentId();
        //向订单表插入一条数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        //计算总金额  amount * number
//        orders.setAmount();
        orders.setNumber(String.valueOf(System.currentTimeMillis()));// 订单号
        orders.setStatus(PENDING_PAYMENT);//订单状态
        orders.setUserId(userId);
        orders.setPhone(addressBooks.getPhone()); // 用户手机号
        orders.setOrderTime(LocalDateTime.now());// 下单时间
        orders.setPayStatus(UN_PAID);// 支付状态
        orders.setConsignee(addressBooks.getConsignee());// 收货人

        orderMapper.insert(orders);
        //向订单表明细表插入n条数据

        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (ShoppingCart cart : list) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        }
        orderDetailMapper.insertOrderDetail(orderDetailList);
        //清空用户的购物车
        shoppingCartMapper.deleteAll(userId);
        //返回VO数据
        return OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();
    }

    /**
     * 历史订单查询
     *
     * @param
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    @Override
    public PageResult getPageResult(int page, int pageSize, Integer status) {
        PageHelper.startPage(page, pageSize);
        Long userId = BaseContext.getCurrentId();
        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setUserId(userId);
        ordersPageQueryDTO.setStatus(status);

        Page<Orders> pages = orderMapper.getAllOrders(ordersPageQueryDTO);

        List<OrderVO> OrderVOList = new ArrayList<>();

        if (pages != null && pages.size() > 0) {
            for (Orders orders : pages) {
                Long ordersId = orders.getId();
                List<OrderDetail> orderDetail = orderDetailMapper.getByOrderId(ordersId);
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                orderVO.setOrderDetailList(orderDetail);

                OrderVOList.add(orderVO);
            }
        }
        return new PageResult(pages.getTotal(), OrderVOList);
    }

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    @Override
    public OrderVO getById(Long id) {
        Orders orders = new Orders();
//        orders.setUserId(BaseContext.getCurrentId());
        orders.setId(id);
        orders = orderMapper.getAllById(orders);
        AddressBook addressBook = addressBookMapper.getAllById(orders.getAddressBookId());
        orders.setAddress(addressBook.getProvinceName() + addressBook.getCityName() + addressBook.getDistrictName() + addressBook.getDetail());
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        List<OrderDetail> list = orderDetailMapper.getByOrderId(id);

        orderVO.setOrderDetailList(list);
        return orderVO;
    }

    /**
     * 取消订单
     * @param id
     */
    @Override
    public void updateById(Long id) {
        Orders orders = new Orders();
        orders.setId(id);
        orders.setUserId(BaseContext.getCurrentId());
        orders.setStatus(CANCELLED);
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    /**
     * 再来一单
     * @param id
     */
    @Override
    public void againOrder(Long id) {
        //查询原订单数据
        Long userId = BaseContext.getCurrentId();
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);
        //将查询到的订单详细数据转换成购物车对象
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map(x -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            // 复制属性并排除id
            BeanUtils.copyProperties(x, shoppingCart, "id");
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());
            return shoppingCart;
        }).toList();
        // 将购物车对象批量添加到数据库
        shoppingCartMapper.insertBatch(shoppingCartList);


    }

    /**
     * 各个状态的订单数量统计
     * @return
     */
    @Override
    public List<OrderStatisticsVO> getOrderStatistics() {
        List<OrderStatisticsVO> orderStatisticsVOList = new ArrayList<>();
        List<Orders> ordersList = orderMapper.getAll();
        for (Orders orders : ordersList) {
            OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
            if (orders.getStatus() == TO_BE_CONFIRMED) {
                //待接单数量
                orderStatisticsVO.setToBeConfirmed(TO_BE_CONFIRMED);
            }
            if (orders.getStatus() == CONFIRMED) {
                //待派送数量
                orderStatisticsVO.setConfirmed(CONFIRMED);
            }
            if (orders.getStatus() == DELIVERY_IN_PROGRESS) {
                //派送中数量
                orderStatisticsVO.setDeliveryInProgress(DELIVERY_IN_PROGRESS);
            }
            orderStatisticsVOList.add(orderStatisticsVO);
        }
        return orderStatisticsVOList;
    }

    /**
     * 订单搜索
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult Page(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<Orders> ordersPage = orderMapper.getPage(ordersPageQueryDTO);

        List<OrderVO> OrderVOList = getOrderVOList(ordersPage);

        return new PageResult(ordersPage.getTotal(),OrderVOList);
    }
    private List<OrderVO> getOrderVOList(Page<Orders> page){
        List<Orders> OrderList = page.getResult();
        List<OrderVO> OrderVOList = new ArrayList<>();
        if (OrderList != null && !OrderList.isEmpty()) {
            for (Orders orders : OrderList) {
                OrderVO orderVO = new OrderVO();
                AddressBook addressBook = addressBookMapper.getAllById(orders.getAddressBookId());
                orders.setAddress(addressBook.getProvinceName() + addressBook.getCityName() +
                        addressBook.getDistrictName() + addressBook.getDetail());
                List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());
                for (OrderDetail orderDetail : orderDetailList) {
                    orderVO.setOrderDishes(orderDetail.getName());
                }
                BeanUtils.copyProperties(orders, orderVO);
                OrderVOList.add(orderVO);
            }

        }

        return OrderVOList;
    }

    /**
     * 取消订单
     * @param cancelDTO
     */
    @Override
    public void update(OrdersCancelDTO cancelDTO) {
        Orders orders = new Orders();
        BeanUtils.copyProperties(cancelDTO, orders);
        orders.setStatus(CANCELLED);
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    /**
     * 完成订单
     * @param id
     */
    @Override
    public void complete(Long id) {
        Orders orders = new Orders();
        orders.setId(id);
        orders.setStatus(COMPLETED); //完成订单
        orders.setDeliveryTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    /**
     * 拒单
     * @param rejectionDTO
     */
    @Override
    public void rejection(OrdersRejectionDTO rejectionDTO) {
        Orders orders = new Orders();
        BeanUtils.copyProperties(rejectionDTO, orders);
        orders.setStatus(CANCELLED);
        orderMapper.update(orders);
    }

    /**
     * 接单
     * @param ordersConfirmDTO
     */
    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersConfirmDTO, orders);
        orders.setStatus(CONFIRMED);
        orderMapper.update(orders);
    }

    /**
     * 派送订单
     * @param id
     */
    @Override
    public void delivery(Long id) {
        Orders orders = new Orders();
        orders.setId(id);
        orders.setStatus(DELIVERY_IN_PROGRESS);
        orders.setEstimatedDeliveryTime(LocalDateTime.now().plusHours(1));
        orderMapper.update(orders);
    }

    /**
     * 订单支付
     * @param ordersPaymentDTO
     */
    @Override
    public void payment(OrdersPaymentDTO ordersPaymentDTO) {
        Orders orders = new Orders();
        orders.setNumber(ordersPaymentDTO.getOrderNumber());
        Orders orderMapperAll = orderMapper.getAllById(orders);
        Orders ordersPaymentDTO1 = new Orders();
        ordersPaymentDTO1.setId(orderMapperAll.getId());
        ordersPaymentDTO1.setStatus(TO_BE_CONFIRMED);
        ordersPaymentDTO1.setCheckoutTime(LocalDateTime.now());
        orderMapper.update(ordersPaymentDTO1);

        Map map = new HashMap();
        map.put("type",1); //1 来单提醒 2 客户催单
        map.put("orderId",orderMapperAll.getId());
        map.put("content","订单号:"+orderMapperAll.getNumber());

        String jsonString = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(jsonString);
    }

    /**
     * 催单
     * @param id
     */
    @Override
    public void reminder(Long id) {
        Orders orders = new Orders();
        orders.setId(id);
        Orders orderMapperAllById = orderMapper.getAllById(orders);
        Map map = new HashMap();
        map.put("type",2); //1 来单提醒 2 客户催单
        map.put("orderId",orderMapperAllById.getId());
        map.put("content","订单号:"+orderMapperAllById.getNumber());

        String jsonString = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(jsonString);
    }


}
