package cn.liumouren.integrationtest.openfeign;

import cn.liumouren.integrationtest.openfeign.api.SomeApi;
import feign.Logger;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author freeman
 * @date 2021/11/29 14:12
 */
@SpringBootApplication
@EnableFeignClients
public class TestOpenfeignApp implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(TestOpenfeignApp.class, args);
    }

    @Bean
    public Logger.Level feignLogger() {
        return Logger.Level.FULL;
    }

    @Autowired
    ApplicationContext context;
    @Autowired
    @Qualifier("cn.liumouren.integrationtest.openfeign.api.SomeApi")
    SomeApi someApi;
//    @Autowired
//    User user1;
//    @Autowired
//    User user2;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println(someApi.getClass());
        String freeman = someApi.some("freeman");
        System.out.println(freeman);
//        System.out.println(user1);
//        System.out.println(user2);
//        String[] names1 = context.getBeanNamesForAnnotation(A.class);
//        System.out.println(Arrays.toString(names1));
    }
}

class User {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{"
                + "name='" + name + '\''
                + '}';
    }
}

@Component
@A
class TestFactoryBean implements FactoryBean<User> {

    {
        System.out.println("shit.....");
    }

    @Override
    @SuppressWarnings("MagicNumber")
    public User getObject() throws Exception {
        Thread.sleep(1000L);
        User user = new User();
        user.setName("freeman" + System.currentTimeMillis());
        return user;
    }

    @Override
    public Class<?> getObjectType() {
        return User.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
