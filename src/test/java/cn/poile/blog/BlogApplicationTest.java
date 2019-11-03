package cn.poile.blog;

import cn.poile.blog.service.IUserService;
import cn.poile.blog.vo.UserVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: yaohw
 * @create: 2019-10-23 18:47
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class BlogApplicationTest {

    @Autowired
    private IUserService userService;

    @Test
    public void userTest() {
        UserVo userVo = userService.selectUserVoByUsernameOrMobile("yaohw",null);
        System.out.println(userVo.toString());
    }

}
