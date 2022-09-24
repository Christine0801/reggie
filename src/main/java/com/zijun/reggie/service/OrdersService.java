package com.zijun.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zijun.reggie.entity.Orders;

public interface OrdersService extends IService<Orders> {

  void submit(Orders orders);
}
