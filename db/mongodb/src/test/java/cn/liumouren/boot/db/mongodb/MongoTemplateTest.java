package cn.liumouren.boot.db.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

/**
 *
 *
 * @author freeman
 * @date 2021/12/8 15:03
 */
@SpringBootTest(
        classes = MongoTemplateTest.Config.class
)
public class MongoTemplateTest {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UserDao userDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private DefaultListableBeanFactory beanFactory;

    //    @Test
    public void test_mongoTemplate() {
        System.out.println(Arrays.toString(beanFactory.getBeanNamesForType(MongoOperations.class)));
        System.out.println(mongoTemplate);
        User user = new User()
                .setId("111")
                .setName("freeman")
                .setBirthday(new Date())
                .setSex(User.Sex.MALE);
        mongoTemplate.save(user);

        User u = mongoTemplate.findById("111", User.class);
        System.out.println(u);

        Optional<User> byId = userDao.findById("111");
        System.out.println(byId.get());
    }

    //    @Test
    public void test_user() {
        User user = new User()
                .setId("111")
                .setName("freeman")
                .setBirthday(new Date())
                .setSex(User.Sex.MALE);
        mongoTemplate.save(user);
        Optional<User> byId = userDao.findById("111");
        System.out.println(byId.get());
    }

    //    @Test
    public void test_order() {
        Order order = new Order()
                .setId("111")
                .setTitle("freeman's order")
                .setTotalPrice(new BigDecimal("9.99"));
        orderDao.save(order);

        Optional<Order> byId = orderDao.findById("111");
        System.out.println(byId.get());
    }

    //    @Test
    public void test_findUserInOrderDb() {
        Optional<User> byId = userDao.findById("111");
        System.out.println(byId);
    }

    @Configuration
    @EnableAutoConfiguration
    static class Config {
    }

}

