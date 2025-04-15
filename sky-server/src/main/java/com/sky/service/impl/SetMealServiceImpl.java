package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class SetMealServiceImpl implements SetMealService {

    @Autowired
    private SetMealMapper setMealMapper;
    @Autowired
    private SetMealDishMapper setMealDishMapper;

    /**
     * 分页查询
     * @return
     */
    @Override
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setMealMapper.PageQuery(setmealPageQueryDTO);

        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Override
    public void add(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setMealMapper.addSetmeal(setmeal);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            for (SetmealDish setmealDish : setmealDishes) {
                setmealDish.setSetmealId(setmeal.getId());
            }
            setMealDishMapper.addSetmealDish(setmealDishes);
        }
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Override
    public SetmealVO getById(Long id) {
        SetmealVO setmealVO = setMealMapper.getById(id);
        List<SetmealDish> setmealDishes = setMealDishMapper.getBySetmealid(id);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }



    /**
     * 修改套餐
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setMealMapper.update(setmeal);

        //先删除菜品
        setMealDishMapper.delete(setmeal.getId());
        //在添加菜品
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            for (SetmealDish setmealDish : setmealDishes) {
                setmealDish.setSetmealId(setmealDTO.getId());
            }
            setMealDishMapper.addSetmealDish(setmealDishes);
        }
    }

    /**
     * 套餐起售、停售
     * @param status
     * @param id
     */
    @Override
    public void setMealstatus(Integer status, Long id) {
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setMealMapper.update(setmeal);
    }

    /**
     * 批量删除套餐
     * @param ids
     */
    @Override
    @Transactional
    public void delete(List<Long> ids) {
        //起售中的套餐不能删除
        ids.forEach(id -> {
            SetmealVO setmeal = setMealMapper.getById(id);
            if(StatusConstant.ENABLE == setmeal.getStatus()){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        });
        ids.forEach(id -> {
            //删除套餐表中的数据
            setMealMapper.delete(id);
            //删除套餐菜品关系表中的数据
            setMealDishMapper.delete(id);
        });
    }

    @Override
    public List<SetmealVO> getByCategoryId(Long categoryId) {
        List<SetmealVO> setmealVO = setMealMapper.getByCategoryId(categoryId);
        return setmealVO;
    }
}
