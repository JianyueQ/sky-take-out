package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
    /**
     * 用户下单
     * @param orders
     */
    void insert(Orders orders);

    /**
     * 历史订单查询
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> getAllOrders(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 查询订单详情
     *
     * @param orders
     * @return
     */
    Orders getAllById(Orders orders);


    /**
     * 取消订单  ,  修改订单取消原因 ,完成订单
     * @param orders
     */
    void update(Orders orders);

    /**
     * 各个状态的订单数量统计
     *
     * @return
     */
    List<Orders> getAll();

    /**
     * 订单搜索
     * @return
     */
    Page<Orders> getPage(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 查询超时订单
     * @param status
     * @param orderTime
     * @return
     */
    List<Orders> getByStatusAndOrderTime(Integer status, LocalDateTime orderTime);
}
