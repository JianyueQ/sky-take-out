package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/admin/setmeal")
public class SetMealController {
    @Autowired
    private SetMealService setMealService;

    /**
     * 分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("分页查询：{}", setmealPageQueryDTO);
        PageResult page = setMealService.page(setmealPageQueryDTO);
        return Result.success(page);
    }

    /**
     * 新增套餐
     * @param setmealDTO
     * @return
     */
    @PostMapping()
    @CacheEvict(cacheNames = "SetmealCache",key = "#setmealDTO.categoryId")  //CacheEvict:清除缓存数据
    public Result<SetmealDTO> add(@RequestBody SetmealDTO setmealDTO) {
        log.info("套餐数据：{}", setmealDTO);
        setMealService.add(setmealDTO);
        return Result.success();
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<SetmealVO> getById(@PathVariable Long id) {
        log.info("根据id查询:{}",id);
        SetmealVO setmealVO = setMealService.getById(id);
        return Result.success(setmealVO);
    }

    /**
     * 修改套餐
     * @param setmealDTO
     * @return
     */
    @PutMapping()
    @CacheEvict(cacheNames = "SetmealCache",key = "#setmealDTO.categoryId")
    public Result<SetmealDTO> update(@RequestBody SetmealDTO setmealDTO) {
        log.info("修改套餐参数：{}", setmealDTO);
        setMealService.update(setmealDTO);
        return Result.success();
    }

    /**
     * 套餐起售、停售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @CacheEvict(cacheNames = "SetmealCache",allEntries = true)//allEntries:是否清空整个缓存
    public Result<String> updateStatus(@PathVariable Integer status,Long id) {
        log.info("套餐id:{},套餐的状态:{}",id,status);
        setMealService.setMealstatus(status,id);
        return Result.success();
    }

    /**
     * 批量删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping()
    @CacheEvict(cacheNames = "SetmealCache",allEntries = true)//allEntries:是否清空整个缓存
    public Result delete(@RequestParam List<Long> ids) {
        log.info("批量删除套餐的ids:{}",ids);
        setMealService.delete(ids);
        return Result.success();
    }

}
