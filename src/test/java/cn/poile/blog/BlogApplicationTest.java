package cn.poile.blog;

import cn.poile.blog.entity.Category;
import cn.poile.blog.entity.Tag;
import cn.poile.blog.service.ICategoryService;
import cn.poile.blog.service.ITagService;
import cn.poile.blog.service.IUserService;
import cn.poile.blog.service.impl.CategoryServiceImpl;
import cn.poile.blog.vo.UserVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author: yaohw
 * @create: 2019-10-23 18:47
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
@Log4j2
public class BlogApplicationTest {

    @Autowired
    private CategoryServiceImpl categoryService;

    @Test
    public void test() {
        //Category category = categoryService.selectOneByParentId(0);
        //log.info("分页:{}",category);
    }

}
