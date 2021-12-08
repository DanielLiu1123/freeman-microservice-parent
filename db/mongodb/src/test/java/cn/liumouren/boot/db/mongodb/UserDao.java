package cn.liumouren.boot.db.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 *
 *
 * @author freeman
 * @date 2021/12/8 15:41
 */
public interface UserDao extends MongoRepository<User, String> {
}
