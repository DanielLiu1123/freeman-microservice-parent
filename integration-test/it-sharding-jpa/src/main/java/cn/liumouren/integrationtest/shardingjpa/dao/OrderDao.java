package cn.liumouren.integrationtest.shardingjpa.dao;

import cn.liumouren.integrationtest.shardingjpa.entity.Order;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

/**
 *
 *
 * @author freeman
 * @date 2021/12/7 15:40
 */
public interface OrderDao extends JpaRepositoryImplementation<Order, String> {

}
