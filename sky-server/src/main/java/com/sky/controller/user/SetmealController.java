package com.sky.controller.user;

import com.sky.dto.SetmealDTO;
import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user/setmeal")
public class SetmealController {

    @Autowired
    private SetMealService setMealService;

    @GetMapping("/list")
    @Cacheable(cacheNames = "SetmealCache",key = "#categoryId")  //key: SetmealCache::categoryId   Cacheable:若缓存存在（通过key匹配），直接返回缓存值,若不存在，执行方法并将结果存入缓存
    public Result<List<SetmealVO>> list(Long categoryId) {
        log.info("categoryId:{}", categoryId);
        List<SetmealVO> setmealVO = setMealService.getByCategoryId(categoryId);
        return Result.success(setmealVO);
    }

    @GetMapping("/dish/{id}")
    public Result<SetmealVO> dish(@PathVariable Long id) {
        SetmealVO setmealVO = setMealService.getById(id);
        return Result.success(setmealVO);
    }
}
