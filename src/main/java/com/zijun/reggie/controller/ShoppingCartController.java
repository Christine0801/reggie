package com.zijun.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.zijun.reggie.common.R;
import com.zijun.reggie.entity.ShoppingCart;
import com.zijun.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

  @Autowired
  private ShoppingCartService shoppingCartService ;

  @Autowired
  private RedisTemplate<String, Object> redisTemplate ;

  @GetMapping("/list")
  public R<List<ShoppingCart>> list() {

    Long userId = (Long) redisTemplate.opsForValue().get("user");
    LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(ShoppingCart::getUserId, userId);
    wrapper.orderByAsc(ShoppingCart::getCreateTime);
    List<ShoppingCart> shoppingCarts = shoppingCartService.list(wrapper);

    return R.success(shoppingCarts);
  }

  @PostMapping("/add")
  public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {

    Long userId = (Long) redisTemplate.opsForValue().get("user");

    LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(ShoppingCart::getUserId, userId);
    // 判断是菜品
    wrapper.eq(shoppingCart.getDishId() != null, ShoppingCart::getDishId, shoppingCart.getDishId());
    // 判断是套餐
    wrapper.eq(shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
    ShoppingCart cart = shoppingCartService.getOne(wrapper);

    if (cart == null) {
      // 记录为空 需要新增
      shoppingCart.setUserId(userId.longValue());
      shoppingCart.setNumber(1);
      shoppingCartService.save(shoppingCart);
      return R.success(shoppingCart);
    } else {
      // 记录不为空，更新number
      cart.setNumber((cart.getNumber()+1));
      shoppingCartService.updateById(cart);
      return R.success(cart);
    }
  }

  @PostMapping("/sub")
  public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {

    Long userId = (Long) redisTemplate.opsForValue().get("user");
    LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(ShoppingCart::getUserId, userId);
    wrapper.eq(shoppingCart.getDishId() != null, ShoppingCart::getDishId, shoppingCart.getDishId());
    wrapper.eq(shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
    ShoppingCart cart = shoppingCartService.getOne(wrapper);

    Integer number = cart.getNumber();
    if (number == 1) {
      // 只剩一份，就删除该记录
      shoppingCartService.removeById(cart);
      return null;
    } else {
      number--;
      cart.setNumber(number);
      shoppingCartService.updateById(cart);
      return R.success(cart);
    }
  }

  @DeleteMapping("/clean")
  public R<List<ShoppingCart>> clean() {

    Long userId = (Long) redisTemplate.opsForValue().get("user");

    LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(ShoppingCart::getUserId, userId);
    shoppingCartService.remove(wrapper);

    return R.success(new ArrayList<>());
  }
}
