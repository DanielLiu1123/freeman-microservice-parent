package cn.liumouren.boot.openfeign;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 *
 * @author <a href="mailto:freemanliu.me@gmail.com">freeman</a>
 * @date 2021/11/28 15:46
 */
@Data
@Accessors(chain = true)
public class Admin {
    private String name;
    private List<String> roles;
    private Integer age;
    private Boolean deleted;
}
