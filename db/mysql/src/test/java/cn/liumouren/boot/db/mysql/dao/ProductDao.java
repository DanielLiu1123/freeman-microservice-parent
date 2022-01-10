package cn.liumouren.boot.db.mysql.dao;

import cn.liumouren.boot.db.mysql.model.Product;
import cn.liumouren.boot.db.mysql.model.User;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

/**
 *
 *
 * @author freeman
 * @date 2022/1/11 03:25
 */
public interface ProductDao extends JpaRepositoryImplementation<Product, String> {
}
