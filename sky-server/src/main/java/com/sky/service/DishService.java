package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;


public interface DishService {

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    PageResult page(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
     List<Dish> listByCategory(Long categoryId);

    /**
     * 根据id查询菜品
     *
     * @param id
     * @return
     */
    DishVO listByID(Long id);

    /**
     * 新增菜品和口味
     * @param dishDTO
     */
    void save(DishDTO dishDTO);

    /**
     * 修改菜品
     * @param dishDTO
     */
    void update(DishDTO dishDTO);

    /**
     *批量删除菜品
     * @param id
     */
    void delete(List<Long> id);

    /**
     *菜品起售、停售
     * @param status
     * @param id
     */
    void updateStatus(Integer status, Long id);
}
