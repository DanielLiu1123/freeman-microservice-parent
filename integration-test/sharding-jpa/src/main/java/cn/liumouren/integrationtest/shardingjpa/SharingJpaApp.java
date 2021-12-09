package cn.liumouren.integrationtest.shardingjpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.sql.DataSource;
import java.util.Arrays;

/**
 *
 * @author freeman
 * @date 2021/12/7 2:17 PM
 */
@SpringBootApplication
public class SharingJpaApp implements ApplicationRunner {
    public static void main(String[] args) {
        SpringApplication.run(SharingJpaApp.class, args);
    }

    @Autowired
    private DefaultListableBeanFactory beanFactory;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String[] beanNamesForType = beanFactory.getBeanNamesForType(DataSource.class);
        System.out.println(Arrays.toString(beanNamesForType));
    }
}
