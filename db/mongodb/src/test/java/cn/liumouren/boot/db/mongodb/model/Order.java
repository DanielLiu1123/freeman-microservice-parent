package cn.liumouren.boot.db.mongodb.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 *
 *
 * @author freeman
 * @date 2021/12/8 21:23
 */
@Data
@Accessors(chain = true)
public class Order {
    private String id;
    private String title;
    private BigDecimal totalPrice;
}
