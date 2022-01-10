package cn.liumouren.boot.db.mysql.model;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 *
 * @author freeman
 * @date 2022/1/11 02:59
 */
@Data
@Accessors(chain = true)
@Entity(name = "tb_product")
public class Product {
    @Id
    private String id;
    private String name;
}
