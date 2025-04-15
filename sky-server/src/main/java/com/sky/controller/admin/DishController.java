package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;


/**
 * 菜品管理
 */
@Slf4j
@RestController("adminDishController")
@RequestMapping("/admin/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 清理缓存数据
     * @param pattern
     */
    private void cleanRedisData(String pattern){
        //先查询keys
        Set keys = redisTemplate.keys(pattern);
        //在删除
        redisTemplate.delete(keys);
    }


    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> Page(DishPageQueryDTO dishPageQueryDTO){
        log.info("分页{}",dishPageQueryDTO);
        PageResult pageResult = dishService.page(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    public Result<List<DishVO>> getByCategory(Long categoryId){
        log.info("分类id:{}",categoryId);
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        List<DishVO> dishVO = dishService.listByCategory(dish);
        return Result.success(dishVO);
    }

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id){
        DishVO dishVO = dishService.listByID(id);
        return Result.success(dishVO);
    }

    /**
     * 新增菜品和口味
     * @param dishDTO
     * @return
     */
    @PostMapping()
    public Result<DishDTO> save(@RequestBody DishDTO dishDTO){
        dishService.save(dishDTO);
        return Result.success();
    }

    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    @PutMapping()
    public Result<DishDTO> update(@RequestBody DishDTO dishDTO){
        log.info("DishDTO:{}",dishDTO);
        dishService.update(dishDTO);

        cleanRedisData("dish_*");

        return Result.success();
    }

    /**
     * 批量删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping()
    public Result<String> delete(@RequestParam List<Long> ids){
        log.info("id:{}",ids);
        dishService.delete(ids);

        cleanRedisData("dish_*");

        return Result.success();
    }

    /**
     * 菜品起售、停售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    public Result<String> updateStatus(@PathVariable Integer status,Long id){
        log.info("status:{},Id:{}",status,id);
        dishService.updateStatus(status,id);

        cleanRedisData("dish_*");

        return Result.success();
    }
}
