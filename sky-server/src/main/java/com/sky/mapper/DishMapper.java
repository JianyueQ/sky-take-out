package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFile;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 菜品分页查询
     *
     * @return
     */
    Page<DishVO> page(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据分类id查询菜品
     * @param dish
     * @return
     */
    List<Dish> list(Dish dish);

    /**
     * 新增菜品和口味
     * @param dish
     */
    @AutoFile(OperationType.INSERT)
    void save(Dish dish);

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    Dish listByID(Long id);

    /**
     * 修改菜品
     * @param dish
     */
    @AutoFile(OperationType.UPDATE)
    void update(Dish dish);

    /**
     * 批量删除菜品
     * @param id
     */
    void delete(Long id);

    /**
     *
     * @param id
     * @return
     */
    @Select("select * from dish where id = #{id}")
    Dish select(Long id);
}
