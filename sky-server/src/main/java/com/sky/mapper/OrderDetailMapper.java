package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderDetailMapper {

    /**
     * 订单明细表插入
     * @param orderDetailList
     */
    void insertOrderDetail(List<OrderDetail> orderDetailList);

    /**
     * 查询订单明细表
     * @param ordersId
     * @return
     */
    List<OrderDetail> getByOrderId(Long ordersId);
}
