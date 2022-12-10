package com.xjhqre.admin;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.xjhqre.common.domain.entity.Menu;
import com.xjhqre.system.service.MenuService;

/**
 * <p>
 * MysqlTest
 * </p>
 *
 * @author xjhqre
 * @since 12月 10, 2022
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AdminApplication.class)
public class MysqlTest {

    @Resource
    MenuService menuService;

    @Test
    public void test1() {
        List<Menu> list = this.menuService.list();
        list.forEach(System.out::println);
    }
}
