package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetMealMapper setMealMapper;

    /**
     * wx用户添加购物车
     * @param shoppingCartDTO
     */
    @Override
    public void save(ShoppingCartDTO shoppingCartDTO) {
        //先查询购物车是否为空
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.list(shoppingCart);
        //如果购物车不为空
        if (shoppingCarts != null && shoppingCarts.size() > 0) {
            //数量+1
            ShoppingCart cart = shoppingCarts.get(0);
            cart.setNumber(cart.getNumber() + 1);
            //更新
            shoppingCartMapper.updateNumber(cart);
        }else {
            //如果菜品不存在,需要插入一条数据
            Long dishId = shoppingCartDTO.getDishId();
            //判断菜品
            if (dishId != null) {
                Dish dish = dishMapper.listByID(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            }else {
            //判断套餐
                Long setmealId = shoppingCartDTO.getSetmealId();
                Setmeal setmeal = setMealMapper.getBySetmealId(setmealId);
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.addShoppingCart(shoppingCart);
        }
    }

    /**
     * 查看购物车
     * @return
     */
    @Override
    public List<ShoppingCart> getShoppingCart() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        return shoppingCartMapper.list(shoppingCart);
    }

    /**
     * 删除购物车中一个商品
     * @param shoppingCartDTO
     */
    @Override
    public void deleteWithOne(ShoppingCartDTO shoppingCartDTO) {
        //先查询购物车是否为空
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.list(shoppingCart);
        //如果购物车不为空
        if (shoppingCarts != null && shoppingCarts.size() > 0) {
            //数量-1
            ShoppingCart cart = shoppingCarts.get(0);
//            判断是否为负数
            if (cart.getNumber() > 1) {
                cart.setNumber(cart.getNumber() - 1);
                //更新
                shoppingCartMapper.updateNumber(cart);
            }
//            else if (cart.getNumber() == 0) {
//                throw new ShoppingCartBusinessException();
//            }
        }
    }

    /**
     * 清空购物车
     */
    @Override
    public void deleteAll() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteAll(userId);
    }
}
