package cn.poile.blog.service.impl;

import cn.poile.blog.common.constant.ArticleStatusEnum;
import cn.poile.blog.common.constant.ErrorEnum;
import cn.poile.blog.common.exception.ApiException;
import cn.poile.blog.mapper.ArticleMapper;
import cn.poile.blog.service.ArticleRecommendService;
import cn.poile.blog.vo.ArticleVo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 文章推荐服务
 *
 * @author: yaohw
 * @create: 2019-11-29 11:57
 **/
@Log4j2
@Service
public class ArticleRecommendServiceImpl implements ArticleRecommendService {

    private static final String KEY = "art:recommend:";

    @Autowired
    private ZSetOperations<String, Object> zSetOperations;

    @Autowired
    private ArticleMapper articleMapper;

    /**
     * 新增推荐
     * @param articleId
     * @param score   文章分数
     */
    @Override
    public void add(Integer articleId, Double score) {
        if (articleId == null) {
            return;
        }
        ArticleVo articleVo = articleMapper.selectArticleVoById(articleId, ArticleStatusEnum.NORMAL.getStatus());
        if (articleVo == null) {
            throw new ApiException(ErrorEnum.INVALID_REQUEST.getErrorCode(), "文章不存在或未发布");
        }
        if (!articleVo.getStatus().equals(0)) {
            return;
        }
        // 先移除原有的
        remove(articleId);
        // 只存列表所需要字段
        articleVo.setContent(null);
        articleVo.setNext(null);
        articleVo.setPrevious(null);
        articleVo.setCategoryList(null);
        zSetOperations.add(KEY, articleVo, score);
    }

    /**
     * 获取推荐列表
     * @return
     */
    @Override
    public List<ArticleVo> list() {
        Set<ZSetOperations.TypedTuple<Object>> valueScoreSet = zSetOperations.rangeWithScores(KEY, 0, -1);
        List<ArticleVo> resultList = new ArrayList<>();
        if (valueScoreSet != null) {
            valueScoreSet.forEach(item -> {
                ArticleVo articleVo = (ArticleVo) item.getValue();
                if (articleVo != null) {
                    articleVo.setRecommendScore(item.getScore());
                    resultList.add(articleVo);
                }
            });
        }
        return resultList;
    }

    /**
     * 从推荐中移除
     * @param articleId
     */
    @Override
    public void remove(Integer articleId) {
        if (articleId == null) {
            return;
        }
        Set<Object> set = zSetOperations.range(KEY, 0, -1);
        if (set != null) {
            set.forEach(i -> {
                ArticleVo articleVo = (ArticleVo) i;
                if (articleId.equals(articleVo.getId())) {
                    zSetOperations.remove(KEY, articleVo);
                }
            });
        }
    }

    /**
     * 刷新
     * @param articleId
     */
    @Override
    public void refresh(Integer articleId) {
        asyncRefresh(articleId);
    }

    /**
     * 异步刷新(底层使用aop，所以同一个类调用异步不会生效)
     * @param articleId
     */
    @Async
    @Override
    public void asyncRefresh(Integer articleId) {
        if (articleId == null) {
            return;
        }
        ArticleVo articleVo;
        Set<ZSetOperations.TypedTuple<Object>> valueScoreSet = zSetOperations.reverseRangeWithScores(KEY, 0, -1);
        if (valueScoreSet != null) {
            for(ZSetOperations.TypedTuple<Object> item:valueScoreSet) {
                ArticleVo value =(ArticleVo) item.getValue();
                if (articleId.equals(value.getId())) {
                    articleVo = value;
                    double score = item.getScore();
                    zSetOperations.remove(KEY,articleVo);
                    add(articleId,score);
                }
            }
        }
    }
}
