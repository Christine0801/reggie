package com.zijun.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zijun.reggie.common.R;
import com.zijun.reggie.dto.OrdersDto;
import com.zijun.reggie.entity.OrderDetail;
import com.zijun.reggie.entity.Orders;
import com.zijun.reggie.service.OrderDetailService;
import com.zijun.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {

  @Autowired
  private OrdersService ordersService ;

  @Autowired
  private RedisTemplate<String, Object> redisTemplate ;

  @Autowired
  private OrderDetailService orderDetailService ;

  @PostMapping("/submit")
  public R<String> submit(@RequestBody Orders orders) {

    ordersService.submit(orders);

    return R.success("下单成功");
  }

  @GetMapping("/page")
  public R<Page> page(int page, int pageSize, Long number, Date beginTime, Date endTime) {

    Page<Orders> ordersPage = new Page<>(page, pageSize);
    LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
    wrapper.like(number != null, Orders::getNumber, number);
    ordersService.page(ordersPage, wrapper);

    return R.success(ordersPage);
  }

  @GetMapping("/userPage")
  public R<Page> page(int page, int pageSize) {

    Page<Orders> ordersPage = new Page<>(page, pageSize);
    Page<OrdersDto> ordersDtoPage = new Page<>(page, pageSize);

    Long userId = (Long) redisTemplate.opsForValue().get("user");
    LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(Orders::getUserId, userId);
    ordersService.page(ordersPage, wrapper);

    BeanUtils.copyProperties(ordersPage, ordersDtoPage, "records");
    List<OrdersDto> ordersDtos = ordersPage.getRecords().stream().map(item -> {
      OrdersDto ordersDto = new OrdersDto();
      BeanUtils.copyProperties(item, ordersDto);
      LambdaQueryWrapper<OrderDetail> wrapper1 = new LambdaQueryWrapper<>();
      wrapper1.eq(OrderDetail::getOrderId, item.getId());
      List<OrderDetail> list = orderDetailService.list(wrapper1);
      ordersDto.setOrderDetails(list);
      return ordersDto;
    }).collect(Collectors.toList());

    ordersDtoPage.setRecords(ordersDtos);

    return R.success(ordersDtoPage);
  }
}


