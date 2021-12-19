package cn.liumouren.integrationtest.microservice.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 *
 *
 * @author freeman
 * @date 2021/12/5 17:13
 */
@Data
@Accessors(chain = true)
public class UserVo {
    private String name;
    private Date birthday;
}
