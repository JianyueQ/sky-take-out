package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    /**
     * wx用户添加购物车
     * @param shoppingCartDTO
     */
    void save(ShoppingCartDTO shoppingCartDTO);

    /**
     * wx用户查看购物车
     * @return
     */
    List<ShoppingCart> getShoppingCart();

    /**
     * 删除购物车中一个商品
     * @param shoppingCartDTO
     */
    void deleteWithOne(ShoppingCartDTO shoppingCartDTO);

    /**
     *清空购物车
     */
    void deleteAll();
}
