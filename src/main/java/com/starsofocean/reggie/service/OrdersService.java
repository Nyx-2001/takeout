package com.starsofocean.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.starsofocean.reggie.domain.Orders;

public interface OrdersService extends IService<Orders> {
    //用户下单
    public void submit(Orders orders);
}
