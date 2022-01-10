package cn.liumouren.boot.db.mongodb;

import cn.liumouren.boot.db.mongodb.core.DynamicMongoTemplate;
import cn.liumouren.boot.db.mongodb.core.MongoTemplatePostProcessor;
import cn.liumouren.boot.db.mongodb.model.Order;
import cn.liumouren.boot.db.mongodb.model.OrderDao;
import cn.liumouren.boot.db.mongodb.model.User;
import cn.liumouren.boot.db.mongodb.model.UserDao;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 *
 * @author freeman
 * @date 2021/12/8 15:03
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DynamicMongoTemplateTest {

    @Autowired
    private DynamicMongoTemplate mongoTemplate;
    @Autowired
    private UserDao userDao;
    @Autowired
    private OrderDao orderDao;


    @Test
    @org.junit.jupiter.api.Order(0)
    public void test_ready() {
        assertThat(mongoTemplate).isInstanceOf(DynamicMongoTemplate.class);

        MongoTemplate defaultTemplate = mongoTemplate.getDefaultTemplate();
        assertThat(defaultTemplate).isNotNull();

        Map<String, MongoTemplate> mappings = mongoTemplate.getDsTemplateMappings();
        assertThat(mappings.size()).isEqualTo(1);

        List<User> users = mongoTemplate.choose("user").findAll(User.class);
        assertThat(users.size()).isEqualTo(1);

        List<Order> orders = mongoTemplate.choose("order").findAll(Order.class);
        assertThat(orders.size()).isEqualTo(1);
    }

    @Test
    public void test_jpaAndMongoTemplateBothWork() {
        User user = new User()
                .setId("111")
                .setName("freeman")
                .setBirthday(new Date())
                .setSex(User.Sex.MALE);
        mongoTemplate.save(user);

        // mongoTemplate
        User user1 = mongoTemplate.findById("111", User.class);
        assertThat(user1).isNotNull();
        assertThat(user1.getName()).isEqualTo("freeman");

        // jpa
        Optional<User> user2 = userDao.findById("111");
        assertThat(user2.get()).isNotNull();
        assertThat(user2.get().getName()).isEqualTo("freeman");
    }

    @Test
    public void test_useDifferentDatasource() {
        // user
        User user = new User()
                .setId("111")
                .setName("freeman")
                .setBirthday(new Date())
                .setSex(User.Sex.MALE);
        mongoTemplate.save(user);
        Optional<User> u = userDao.findById("111");

        // order
        Order order = new Order()
                .setId("111")
                .setTitle("freeman's order")
                .setTotalPrice(new BigDecimal("9.99"));
        orderDao.save(order);
        Optional<Order> o = orderDao.findById("111");

        assertThat(o.get()).isNotNull();
        assertThat(u.get()).isNotNull();
        assertThat(mongoTemplate.choose(User.class)).isNotEqualTo(mongoTemplate.choose(Order.class));
    }

    @Configuration
    @EnableMongoRepositories
    @EnableAutoConfiguration
    static class Config {

        @Autowired
        private DynamicMongoTemplate mongoTemplate;
        @Autowired
        private ApplicationContext context;
        @Autowired
        private MongoDynamicProperties properties;

        @PostConstruct
        public void init() {
            // create db, collection
            String uri = properties.getDatasources().values().iterator().next().getUri();
            uri = uri.substring(0, uri.lastIndexOf('/'));
            MongoClient client = MongoClients.create(uri);
            MongoDatabase userDb = client.getDatabase("freeman_user");
            MongoDatabase orderDb = client.getDatabase("freeman_order");
            userDb.createCollection("user");
            userDb.getCollection("user").insertOne(new Document("name", "freeman lau"));
            orderDb.createCollection("order");
            orderDb.getCollection("order").insertOne(new Document("title", "some goods!"));

            // reload DynamicMongoTemplate
            DynamicMongoTemplate mt = ReflectionTestUtils.invokeMethod(
                    new MongoTemplatePostProcessor(),
                    "dynamicMongoTemplate",
                    context.getBean(MongoDatabaseFactory.class),
                    context.getBean(MongoConverter.class),
                    properties);
            mongoTemplate.setDsTemplateMappings(mt.getDsTemplateMappings());
            mongoTemplate.setDefaultTemplate(mt.getDefaultTemplate());
            mongoTemplate.setCollectionNameDsMappings(mt.getCollectionNameDsMappings());
        }

    }

}

