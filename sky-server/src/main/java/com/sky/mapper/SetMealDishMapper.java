package com.sky.mapper;

import com.sky.annotation.AutoFile;
import com.sky.dto.SetmealDTO;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetMealDishMapper {

    /**
     * @param dishIds
     * @return
     */
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    /**
     * 添加菜品
     * @param setmealDishes
     */
    void addSetmealDish(List<SetmealDish> setmealDishes);

    /**
     * 根据id查询套餐菜品
     * @param id
     * @return
     */
    List<SetmealDish> getBySetmealid(Long id);

    /**
     * 修改菜品
     * @param setmealDTO
     */
    @AutoFile(OperationType.UPDATE)
    void update(SetmealDTO setmealDTO);

    /**
     * 删除菜品
     * @param setmealId
     */
    void delete(Long setmealId);
}
