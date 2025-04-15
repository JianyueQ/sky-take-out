package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

import java.util.List;

public interface OrderService {
    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO order(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 历史订单查询
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    PageResult getPageResult(int page, int pageSize, Integer status);

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    OrderVO getById(Long id);

    /**
     * 取消订单
     * @param id
     */
    void updateById(Long id);

    /**
     * 在来一单
     * @param id
     */
    void againOrder(Long id);

    /**
     * 各个状态的订单数量统计
     * @return
     */
    List<OrderStatisticsVO> getOrderStatistics();

    /**
     * 订单搜索
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult Page(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 取消订单
     * @param cancelDTO
     */
    void update(OrdersCancelDTO cancelDTO);

    /**
     * 完成订单
     * @param id
     */
    void complete(Long id);

    /**
     * 拒单
     * @param rejectionDTO
     */
    void rejection(OrdersRejectionDTO rejectionDTO);

    /**
     * 接单
     * @param ordersConfirmDTO
     */
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 派送订单
     * @param id
     */
    void delivery(Long id);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     */
    void payment(OrdersPaymentDTO ordersPaymentDTO);
}
