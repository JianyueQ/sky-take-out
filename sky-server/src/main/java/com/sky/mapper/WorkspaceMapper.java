package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WorkspaceMapper {

    /**
     * 查询套餐停售,起售数量
     * @return
     */
    Integer getNumberByStatus(Integer status);

    /**
     * 查询菜品停售,起售数量
     * @param disable
     * @return
     */
    Integer getNumberByStatusDish(Integer disable);

    /**
     * 查询订单管理数据
     * @param refund
     * @return
     */
    Integer getStatusByOrders(Integer refund);
}
