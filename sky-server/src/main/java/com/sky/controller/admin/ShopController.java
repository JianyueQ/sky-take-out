package com.sky.controller.admin;

import com.sky.constant.StatusConstant;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@Slf4j
@RequestMapping("/admin/shop")
public class ShopController {

    public static final String KEY = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 设置营业状态
     * @param status
     * @return
     */
    @PutMapping("/{status}")
    public Result updateStatus(@PathVariable Integer status) {
        log.info("设置营业状态:{}", status == 1 ? "营业中" : "打样中");
        redisTemplate.opsForValue().set(KEY, status);
        return Result.success();
    }

    /**
     * 获取营业状态
     * @return
     */
    @GetMapping("/status")
    public Result<Integer> getStatus() {
        Integer shopStatus = (Integer) redisTemplate.opsForValue().get(KEY);
        log.info("当前营业状态:{}",shopStatus == 1? "营业中" : "打样中");
        return Result.success(shopStatus);
    }


}
