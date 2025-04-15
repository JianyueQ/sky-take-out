package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFile;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetMealMapper {
    /**
     * 分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    Page<SetmealVO> PageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 新增套餐
     * @param setmeal
     */
    @AutoFile(OperationType.INSERT)
    void addSetmeal(Setmeal setmeal);

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    SetmealVO getById(Long id);

    /**
     * 修改套餐
     * @param setmeal
     */
    @AutoFile(OperationType.UPDATE)
    void update(Setmeal setmeal);

    /**
     * 删除套餐表
     * @param id
     */
    void delete(Long id);


    List<SetmealVO> getByCategoryId(Long categoryId);

    @Select("select * from setmeal where id = #{setmealId}")
    Setmeal getBySetmealId(Long setmealId);
}
