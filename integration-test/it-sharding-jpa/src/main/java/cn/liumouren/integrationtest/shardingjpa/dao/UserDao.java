package cn.liumouren.integrationtest.shardingjpa.dao;

import cn.liumouren.integrationtest.shardingjpa.entity.User;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

/**
 *
 *
 * @author freeman
 * @date 2021/12/7 15:40
 */
public interface UserDao extends JpaRepositoryImplementation<User, String> {

}
