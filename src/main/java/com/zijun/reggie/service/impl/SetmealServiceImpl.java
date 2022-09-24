package com.zijun.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zijun.reggie.common.CustomException;
import com.zijun.reggie.dto.SetmealDto;
import com.zijun.reggie.entity.Setmeal;
import com.zijun.reggie.entity.SetmealDish;
import com.zijun.reggie.mapper.SetmealMapper;
import com.zijun.reggie.service.SetmealDishService;
import com.zijun.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

  @Autowired
  private SetmealDishService setmealDishService ;

  @Override
  public void saveWithDish(SetmealDto setmealDto) {
    this.save(setmealDto);

    List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes().stream().map(item -> {
      item.setSetmealId(setmealDto.getId());
      return item;
    }).collect(Collectors.toList());

    setmealDishService.saveBatch(setmealDishes);

  }

  @Override
  @Transactional
  public void delete(List<Long> list) {

    // 根据ids查找所有处于起售状态的套餐
    LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
    wrapper.in(Setmeal::getId, list);
    wrapper.eq(Setmeal::getStatus, 1);

    // 如果查询结果数量大于0，则包含起售状态，不可删除，抛出异常
    int count = this.count(wrapper);
    if (count > 0) {
      throw new CustomException("选中套餐处于起售状态，不可删除");
    }

    LambdaQueryWrapper<SetmealDish> wrapper1 = new LambdaQueryWrapper<>();
    wrapper1.in(SetmealDish::getSetmealId, list);

    // 删除所有菜品套餐关联记录
    setmealDishService.remove(wrapper1);

    // 删除套餐
    this.removeByIds(list);
  }
}
