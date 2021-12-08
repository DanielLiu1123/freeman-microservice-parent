package cn.liumouren.boot.db.mongodb;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 *
 *
 * @author freeman
 * @date 2021/12/8 15:16
 */
@Data
@Accessors(chain = true)
public class User {
    private String id;
    private String name;
    private Sex sex;
    private Date birthday;

    enum Sex {
        MALE, FEMALE
    }
}

