package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorsMapper {

    /**
     * 批量插入口味
     * @param flavors
     */
    void saveFlavors(List<DishFlavor> flavors);

    /**
     * 根据dishID查询口味
     * @param id
     * @return
     */
    List<DishFlavor> list(Long id);

    /**
     * 删除菜品口味
     * @param id
     */
    void delete(Long id);

    List<DishFlavor> getByDishId(Long id);
}
