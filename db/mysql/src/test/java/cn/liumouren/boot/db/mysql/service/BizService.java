package cn.liumouren.boot.db.mysql.service;

import cn.liumouren.boot.db.mysql.dao.ProductDao;
import cn.liumouren.boot.db.mysql.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 *
 * @author freeman
 * @date 2022/1/11 03:43
 */
@Service
public class BizService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private ProductDao productDao;

    @Transactional(rollbackFor = Exception.class)
    public void deleteErrFallback(String id) {
        userDao.deleteById(id);
        productDao.deleteById(id);
        throw new RuntimeException();
    }

    public void deleteErrNoFallback(String id) {
        userDao.deleteById(id);
        productDao.deleteById(id);
        throw new RuntimeException();
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteSuccess(String id) {
        userDao.deleteById(id);
        productDao.deleteById(id);
    }

}
