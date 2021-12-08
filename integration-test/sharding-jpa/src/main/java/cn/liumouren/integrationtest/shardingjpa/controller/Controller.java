package cn.liumouren.integrationtest.shardingjpa.controller;

import cn.liumouren.integrationtest.shardingjpa.dao.OrderDao;
import cn.liumouren.integrationtest.shardingjpa.dao.UserDao;
import cn.liumouren.integrationtest.shardingjpa.entity.Order;
import cn.liumouren.integrationtest.shardingjpa.entity.Sex;
import cn.liumouren.integrationtest.shardingjpa.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 *
 *
 * @author freeman
 * @date 2021/12/7 15:42
 */
@RestController
public class Controller {
    @Autowired
    private UserDao userDao;
    @Autowired
    private OrderDao orderDao;

    @PostMapping("/user")
    public User saveUser(@RequestBody User user) {
        userDao.save(user);
        return user;
    }

    @PostMapping("/order")
    public Order saveOrder(@RequestBody Order order) {
        orderDao.save(order);
        return order;
    }

    @GetMapping("/user/{id}")
    public User getUser(@PathVariable String id) {
        return userDao.findById(id).orElse(null);
    }

    // 测试本地多数据源事务

    @Transactional(rollbackFor = Exception.class)
    @GetMapping("/user/testLocalTransaction/{ok}")
    public User testLocalTransaction(@PathVariable Integer ok) {
        User wlq = new User().setName("wlq").setSex(Sex.FEMALE);
        Order llw = new Order().setTitle("llw").setTotalPrice(new BigDecimal("9.99"));
        userDao.save(wlq);

        // 模拟异常
        if (ok < 0) {
            throw new RuntimeException("error");
        }

        orderDao.save(llw);
        return wlq;
    }

}
