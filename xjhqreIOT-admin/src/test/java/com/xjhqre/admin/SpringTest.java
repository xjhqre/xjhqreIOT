package com.xjhqre.admin;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <p>
 * PasswordTest
 * </p>
 *
 * @author xjhqre
 * @since 12月 11, 2022
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AdminApplication.class)
public class SpringTest {

    @org.junit.Test
    public void test1() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        System.out.println(passwordEncoder.encode("123456"));
    }

}
