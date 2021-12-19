package cn.liumouren.integrationtest.shardingjpa.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

/**
 * order 是 mysql 关键字, 注意下
 *
 * @author freeman
 * @date 2021/12/7 15:36
 */
@Data
@Entity(name = "tb_order")
@Accessors(chain = true)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String title;
    private BigDecimal totalPrice;

}
