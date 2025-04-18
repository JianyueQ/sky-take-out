package com.sky.service.impl;


import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.ReportMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.mapper.WorkspaceMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    @Autowired
    private WorkspaceMapper workspaceMapper;
    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetMealMapper setMealMapper;


    /**
     * 查询今日运营数据
     * @return
     */
    @Override
    public BusinessDataVO getBusinessData() {
        LocalDateTime begin = LocalDateTime.of(LocalDate.from(LocalDateTime.now()), LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        Map map = new HashMap();
        map.put("begin", begin);
        map.put("end", end);
        //获取今日新增用户数量

        Integer sumUser = reportMapper.sumByUserMap(map);

        //获取今日订单完成率
        Integer sumOrders = reportMapper.sumByOrdersMap(map); //今日订单总数
        map.put("status", Orders.COMPLETED);
        //今日有效订单数量
        Integer sumValidOrders = reportMapper.sumByOrdersMap(map);
        //今日订单完成率
        Double OrderFulfillmentRate = sumOrders > 0 ? sumValidOrders / sumOrders : 0.0;

        //计算今日营业额
        Double todayTurnover = reportMapper.sumByMap(map);

        //计算今日平均客单价
        Double todayAverageOrderValue = sumValidOrders > 0 ? todayTurnover / sumValidOrders : 0.0;

        return BusinessDataVO.builder()
                .turnover(todayTurnover)
                .validOrderCount(sumValidOrders)
                .orderCompletionRate(OrderFulfillmentRate)
                .unitPrice(todayAverageOrderValue)
                .newUsers(sumUser)
                .build();
    }

    /**
     * 查询套餐总览
     * @return
     */
    @Override
    public SetmealOverViewVO getOverviewSetmeals() {

        //获取已起售数量
        Integer sold = workspaceMapper.getNumberByStatus(StatusConstant.ENABLE);

        //获取已停售的数量
        Integer discontinued = workspaceMapper.getNumberByStatus(StatusConstant.DISABLE);

        return SetmealOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }

    /**
     * 查询菜品总览
     * @return
     */
    @Override
    public SetmealOverViewVO getOverviewDishes() {

        //获取已起售数量
        Integer sold = workspaceMapper.getNumberByStatusDish(StatusConstant.ENABLE);

        //获取已停售的数量
        Integer discontinued = workspaceMapper.getNumberByStatusDish(StatusConstant.DISABLE);

        return SetmealOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }

    /**
     * 查询订单管理数据
     * @return
     */
    @Override
    public OrderOverViewVO getOverviewOrders() {
        /**
         * 订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
         */
        //待接单数量
        Integer waitingOrders = workspaceMapper.getStatusByOrders(Orders.REFUND);
        //待派送数量
        Integer deliveredOrders = workspaceMapper.getStatusByOrders(Orders.CONFIRMED);
        //已完成数量
        Integer completedOrders = workspaceMapper.getStatusByOrders(Orders.COMPLETED);
        //已取消数量
        Integer cancelledOrders = workspaceMapper.getStatusByOrders(Orders.CANCELLED);
        //全部订单
        Integer allOrders = waitingOrders + deliveredOrders + completedOrders + cancelledOrders;

        return OrderOverViewVO.builder()
                .allOrders(allOrders)
                .cancelledOrders(cancelledOrders)
                .completedOrders(completedOrders)
                .deliveredOrders(deliveredOrders)
                .waitingOrders(waitingOrders)
                .build();
    }
}
