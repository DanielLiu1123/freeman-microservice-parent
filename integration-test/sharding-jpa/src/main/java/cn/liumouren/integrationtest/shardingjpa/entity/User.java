package cn.liumouren.integrationtest.shardingjpa.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Date;

/**
 *
 *
 * @author freeman
 * @date 2021/12/7 15:36
 */
@Entity
@Data
@Accessors(chain = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String name;
    private Sex sex;
    private String phone;

}
