package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    /**
     * wx用户添加购物车
     * @param shoppingCart
     */
    void addShoppingCart(ShoppingCart shoppingCart);

    /**
     * 查询购物车
     *
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 更新数量
     * @param
     */
    void updateNumber(ShoppingCart cart);

    /**
     * 清空购物车
     */
    void deleteAll(Long userId);

    /**
     * 批量插入购物车
     * @param shoppingCartList
     */
    void insertBatch(List<ShoppingCart> shoppingCartList);
}
