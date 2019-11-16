package cn.poile.blog.service.impl;

import cn.poile.blog.entity.ArticleTag;
import cn.poile.blog.mapper.ArticleTagMapper;
import cn.poile.blog.service.IArticleTagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 文章-标签 关联表 服务实现类
 * </p>
 *
 * @author yaohw
 * @since 2019-11-15
 */
@Service
public class ArticleTagServiceImpl extends ServiceImpl<ArticleTagMapper, ArticleTag> implements IArticleTagService {

}
