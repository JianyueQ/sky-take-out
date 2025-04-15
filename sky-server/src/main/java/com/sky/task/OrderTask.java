package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务类
 */
@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    public static final String ORDER_CANCEL = "订单超时,自动取消";
    public static final String ERROR_Timeout_ORDER = "未查询到超时订单";
    public static final String ERROR_Delivering_ORDER = "未查询到处于派送中的订单";
    /**
     * 处理超时的订单
     */
    @Scheduled(cron = "0 * * * * ?")
    public void getTimeoutOrders(){
        log.info("定时处理超时订单:{}", LocalDateTime.now());

        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);

        log.info(time.toString());
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTime(Orders.PENDING_PAYMENT,time);
        log.info("查询到 {} 个超时订单", ordersList.size());
        if(ordersList.size()>0){
            for (Orders order : ordersList) {
                order.setCancelTime(LocalDateTime.now());
                order.setStatus(Orders.CANCELLED);
                order.setCancelReason(ORDER_CANCEL);
                orderMapper.update(order);
            }
        }
    }

    /**
     * 处理一直处于派送中的订单
     */
    @Scheduled(cron = "0 0 1 * * ?")
//    @Scheduled(cron = "0/5 * * * * ?")
    public void getDeliveringOrders(){

        log.info("定时处理派送中订单:{}", LocalDateTime.now());

        LocalDateTime time = LocalDateTime.now().minusMinutes(-60);

        List<Orders> ordersList = orderMapper.getByStatusAndOrderTime(Orders.DELIVERY_IN_PROGRESS,time);
        log.info("查询到 {} 个派送中订单", ordersList.size());
        if (ordersList != null && ordersList.size() > 0) {
            for (Orders order : ordersList) {
                order.setStatus(Orders.COMPLETED);
                order.setDeliveryTime(LocalDateTime.now());
                orderMapper.update(order);
            }
        }

    }
}
