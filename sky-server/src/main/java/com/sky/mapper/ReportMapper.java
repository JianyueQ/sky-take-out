package com.sky.mapper;

import com.sky.dto.GoodsSalesDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReportMapper {

    /**
     * 查询营业额
     * @param map
     * @return
     */
    Double sumByMap(Map map);

    /**
     * 查询用户数量
     *
     * @param map
     * @return
     */
    Integer sumByUserMap(Map map);

    /**
     *查询订单完成数量
     * @param mapOrders
     * @return
     */
    Integer sumByOrdersMap(Map mapOrders);

    /**
     *
     * @param mapOrders
     */
    void listByOrdersMap(Map mapOrders);

    /**
     * 查询销量排名top10接口
     * @param mapTopTen
     * @return
     */
    List<GoodsSalesDTO> topTenByMap(Map mapTopTen);
}
