package cn.liumouren.boot.db.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 *
 *
 * @author freeman
 * @date 2021/12/8 15:41
 */
public interface OrderDao extends MongoRepository<Order, String> {
}
