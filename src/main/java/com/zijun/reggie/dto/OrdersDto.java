package com.zijun.reggie.dto;

import com.zijun.reggie.entity.OrderDetail;
import com.zijun.reggie.entity.Orders;
import lombok.Data;
import java.util.List;

@Data
public class OrdersDto extends Orders {

    private List<OrderDetail> orderDetails;
	
}
