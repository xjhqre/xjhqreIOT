package com.xjhqre.admin;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson2.JSON;
import com.xjhqre.common.domain.entity.User;
import com.xjhqre.system.service.UserService;

import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class SpringTest {

    @Resource
    UserService userService;

    @Test
    public void test2() {
        List<User> list = this.userService.list();
        List<User> after = new ArrayList<>();
        log.info("before: {}", JSON.toJSONString(list));
        for (User user : list) {
            this.updateUser(user);
            after.add(user);
        }
        log.info("after: {}", JSON.toJSONString(after));
    }

    private void updateUser(User user) {
        user.setRemark("测试");
    }

    @org.junit.Test
    public void test1() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        System.out.println(passwordEncoder.encode("123456"));
    }

}
