package cn.poile.blog.service.impl;

import cn.poile.blog.common.constant.ArticleStatusEnum;
import cn.poile.blog.common.constant.CommonConstant;
import cn.poile.blog.common.constant.ErrorEnum;
import cn.poile.blog.common.exception.ApiException;
import cn.poile.blog.common.security.ServeSecurityContext;
import cn.poile.blog.common.util.DateUtil;
import cn.poile.blog.controller.model.dto.PreArtAndNextArtDTO;
import cn.poile.blog.controller.model.request.ArticleRequest;
import cn.poile.blog.entity.Article;
import cn.poile.blog.entity.ArticleTag;
import cn.poile.blog.entity.Category;
import cn.poile.blog.entity.Tag;
import cn.poile.blog.mapper.ArticleMapper;
import cn.poile.blog.service.IArticleService;
import cn.poile.blog.service.IArticleTagService;
import cn.poile.blog.service.ICategoryService;
import cn.poile.blog.service.ITagService;
import cn.poile.blog.vo.*;
import cn.poile.blog.wrapper.ArticlePageQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * <p>
 * 文章表 服务实现类
 * </p>
 *
 * @author yaohw
 * @since 2019-11-15
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements IArticleService {

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private ITagService tagService;

    @Autowired
    private IArticleTagService articleTagService;

    private static final String SEPARATION = ",";


    /**
     * 新增或更新
     *
     * @param request
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(ArticleRequest request) {
        Integer status = request.getStatus();
        if (!status.equals(0) & !status.equals(1)) {
            throw new ApiException(ErrorEnum.INVALID_REQUEST.getErrorCode(), "无效状态码");
        }
        Article article = new Article();
        // 文章id处理
        article.setId(request.getId() == null ? null : request.getId() == 0 ? null : request.getId());
        // 判断是否原创，做不同处理
        setAuthor(article, request);
        // 获取分类信息
        Category category = categoryService.getById(request.getCategoryId());
        if (category == null) {
            throw new ApiException(ErrorEnum.INVALID_REQUEST.getErrorCode(), "分类不存在");
        }
        article.setCategoryName(category.getName());
        article.setCategoryId(category.getId());
        // 文章标题、摘要、内容、封面
        article.setTitle(request.getTitle());
        article.setSummary(request.getSummary());
        article.setContent(request.getContent());
        article.setCover(request.getCover());
        // 文章状态
        article.setStatus(status);
        // 时间
        article.setPublishTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        // 保存或更新文章
        saveOrUpdate(article);

        Integer articleId = article.getId();
        // 文章-标签 关联
        List<Integer> tagIds = request.getTagIds();
        if (CollectionUtils.isEmpty(tagIds)) {
            deleteArticleTagByArticleId(articleId);
            return;
        }
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(Tag::getId, tagIds);
        int count = tagService.count(queryWrapper);
        if (count == 0) {
            throw new ApiException(ErrorEnum.INVALID_REQUEST.getErrorCode(), "标签id不存在");
        }
        // 非逻辑删除-删除原来的
        deleteArticleTagByArticleId(articleId);
        // 批量新增
        List<ArticleTag> articleTagList = tagIds.stream().map((tagId) -> new ArticleTag(articleId, tagId)).collect(Collectors.toList());
        articleTagService.saveBatch(articleTagList);
    }

    /**
     * 根据文章id删除文章标签
     * @param articleId
     */
    private void deleteArticleTagByArticleId(Integer articleId) {
        QueryWrapper<ArticleTag> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.lambda().eq(ArticleTag::getArticleId, articleId);
        articleTagService.remove(deleteWrapper);
    }


    /**
     * 分页查询文章
     *
     * @param current
     * @param size
     * @param status
     * @param title
     * @param categoryId
     * @param tagId
     * @param yearMonth
     * @return
     */
    @Override
    public IPage<ArticleVo> selectArticleVoPage(long current, long size, Integer status, String title, Integer categoryId, Integer tagId, String yearMonth) {
        validStatus(status);
        String[] startAndEndOfMonth = getStartAndEndOfMonth(yearMonth);
        String start = startAndEndOfMonth[0];
        String end = startAndEndOfMonth[1];
        int count = selectPageCount(status, categoryId, tagId, start, end, title);
        if (count == 0) {
            return new Page<>(current, size);
        }
        ArticlePageQueryWrapper queryWrapper = new ArticlePageQueryWrapper();
        queryWrapper.setOffset((current - 1) * size);
        queryWrapper.setLimit(size);
        queryWrapper.setCategoryId(categoryId);
        queryWrapper.setTagId(tagId);
        queryWrapper.setTitle(title);
        queryWrapper.setOrderBy("publish_time");
        queryWrapper.setStart(start);
        queryWrapper.setEnd(end);
        queryWrapper.setStatus(status);
        List<ArticleVo> articleVoList = this.baseMapper.selectArticleVoPage(queryWrapper);
        Page<ArticleVo> page = new Page<>(current, size, count);
        page.setRecords(articleVoList);
        return page;
    }

    /**
     * 丢弃文章（放到回收站）
     *
     * @param id
     */
    @Override
    public void discard(int id) {
        Article article = new Article();
        article.setId(id);
        article.setStatus(ArticleStatusEnum.DISCARD.getStatus());
        updateById(article);
    }

    /**
     * 删除文章（逻辑删除）
     *
     * @param id
     */
    @Override
    public void delete(int id) {
        removeById(id);
    }

    /**
     * 分页查询已发布文章
     *
     * @param current
     * @param size
     * @param categoryId
     * @param tagId
     * @param yearMonth  归档年月，因为数据库函数date_format会使publish_time索引失效，索引转换下再查询
     * @param title
     * @param orderBy    排序
     * @return
     */
    @Override
    public IPage<ArticleVo> selectPublishedArticleVoPage(long current, long size, Integer categoryId, Integer tagId, String yearMonth, String title, String orderBy) {
        String[] startAndEndOfMonth = getStartAndEndOfMonth(yearMonth);
        String start = startAndEndOfMonth[0];
        String end = startAndEndOfMonth[1];
        int count = selectPageCount(ArticleStatusEnum.NORMAL.getStatus(), categoryId, tagId, start, end, title);
        if (count == 0) {
            return new Page<>(current, size);
        }
        ArticlePageQueryWrapper queryWrapper = new ArticlePageQueryWrapper();
        queryWrapper.setOffset((current - 1) * size);
        queryWrapper.setLimit(size);
        queryWrapper.setCategoryId(categoryId);
        queryWrapper.setTagId(tagId);
        queryWrapper.setTitle(title);
        queryWrapper.setOrderBy(orderBy);
        queryWrapper.setStart(start);
        queryWrapper.setEnd(end);
        queryWrapper.setStatus((ArticleStatusEnum.NORMAL.getStatus()));
        List<ArticleVo> articleVoList = this.baseMapper.selectArticleVoPage(queryWrapper);
        Page<ArticleVo> page = new Page<>(current, size, count);
        page.setRecords(articleVoList);
        return page;
    }

    /**
     * 文章详情
     *
     * @param id
     * @return
     */
    @Override
    public ArticleVo selectArticleVoById(int id) {
        ArticleVo articleVo = this.baseMapper.selectArticleVoById(id, null);
        if (articleVo == null) {
            throw new ApiException(ErrorEnum.INVALID_REQUEST.getErrorCode(), "文章不存在");
        }
        List<Category> categoryList = categoryService.parentList(articleVo.getCategoryId());
        articleVo.setCategoryList(categoryList);
        return articleVo;
    }

    /**
     * 获取文章详情并增长浏览次数
     *
     * @param id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArticleVo selectOneAndAddViewCount(int id) {
        ArticleVo articleVo = this.baseMapper.selectArticleVoById(id, ArticleStatusEnum.NORMAL.getStatus());
        if (articleVo == null) {
            throw new ApiException(ErrorEnum.INVALID_REQUEST.getErrorCode(), "文章不存在");
        }
        // 浏览次数自增
        Article article = new Article();
        Integer viewCount = articleVo.getViewCount();
        article.setViewCount(viewCount + 1);
        article.setId(articleVo.getId());
        updateById(article);
        // 上一篇和下一篇
        PreArtAndNextArtDTO preAndNext = selectPreAndNext(id);
        articleVo.setPrevious(preAndNext.getPre());
        articleVo.setNext(preAndNext.getNext());
        return articleVo;
    }


    /**
     * 分页年月归档查询
     *
     * @param current
     * @param size
     * @return
     */
    @Override
    public IPage<ArticleArchivesVo> selectArticleArchives(long current, long size) {
        Integer count = this.baseMapper.selectArticleArchivesCount();
        if (count == 0) {
            return new Page<>(current, size);
        }
        List<ArticleArchivesVo> articleArchivesVoList = this.baseMapper.selectArticleArchives((current - 1) * size, size);
        Page<ArticleArchivesVo> page = new Page<>(current, size, count);
        page.setRecords(articleArchivesVoList);
        return page;
    }


    /**
     * 文章计数
     *
     * @param status 状态
     * @param categoryId 分类id
     * @param tagId 标签id
     * @param start 开始日期
     * @param end 结束日期
     * @param title  标题关键字
     * @return
     */
    private int selectPageCount(Integer status, Integer categoryId, Integer tagId, String start, String end, String title) {
        return this.baseMapper.selectPageCount(status, categoryId, tagId, start, end, title);
    }


    /**
     * 查询上一篇和下一篇
     *
     * @param id
     * @return
     */
    private PreArtAndNextArtDTO selectPreAndNext(int id) {
        List<Article> articleList = this.baseMapper.selectPreAndNext(id);
        int two = 2;
        int size = articleList.size();
        PreArtAndNextArtDTO dto = new PreArtAndNextArtDTO();
        if (size == two) {
            dto.setPre(articleList.get(0));
            dto.setNext(articleList.get(1));
        } else if (size == 1) {
            oneHandle(dto, articleList.get(0), id);
        }
        return dto;
    }

    /**
     * 按分类计数文章数
     *
     * @return
     */
    @Override
    public List<ArticleCategoryStatisticsVo> selectCategoryStatistic() {
        return this.baseMapper.selectCategoryStatistic();
    }


    /**
     * 按标签计数文章数
     *
     * @return
     */
    @Override
    public List<ArticleTagStatisticsVo> selectTagStatistic() {
        return this.baseMapper.selectTagStatistic();
    }


    /**
     * 相关文章查询
     *
     * @param id
     * @return
     */
    @Override
    public List<ArticleVo> selectInterrelatedById(Integer id, Long limit) {
        Article article = getById(id);
        ArticlePageQueryWrapper articlePageQueryWrapper = new ArticlePageQueryWrapper();
        articlePageQueryWrapper.setOffset(0L);
        articlePageQueryWrapper.setLimit(limit);
        articlePageQueryWrapper.setOrderBy("view_count");
        articlePageQueryWrapper.setCategoryId(article.getCategoryId());
        articlePageQueryWrapper.setStatus((ArticleStatusEnum.NORMAL.getStatus()));
        List<ArticleVo> resultList = this.baseMapper
                .selectArticleVoPage(articlePageQueryWrapper).stream()
                .filter(a -> !id.equals(a.getId())).collect(Collectors.toList());
        // 分类下没有使用标签查询
        if (CollectionUtils.isEmpty(resultList)) {
            QueryWrapper<ArticleTag> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(ArticleTag::getArticleId, id);
            List<ArticleTag> articleTagList = articleTagService.list(queryWrapper);
            if (!CollectionUtils.isEmpty(articleTagList)) {
                resultList = this.baseMapper
                        .selectByTagList(articleTagList.stream().map(ArticleTag::getTagId).collect(Collectors.toList()), limit)
                        .stream().filter(a -> !id.equals(a.getId())).collect(Collectors.toList());
            }
        }
        return resultList;
    }

    /**
     * 更新文章状态
     *
     * @param articleId
     * @param status 0为正常，1为待发布，2为回收站
     */
    @Override
    public void updateStatus(Integer articleId, Integer status) {
        Integer[] array = {0, 1, 2};
        if (!ArrayUtils.contains(array, status)) {
            throw new ApiException(ErrorEnum.INVALID_REQUEST.getErrorCode(), "无效状态码");
        }
        Article article = new Article();
        article.setId(articleId);
        article.setStatus(status);
        updateById(article);
    }

    /**
     * 点赞数自增
     *
     * @param articleId
     */
    @Override
    public void likeCountIncrement(int articleId) {
        this.baseMapper.likeCountIncrement(articleId);
    }

    /**
     * 点赞数自减
     *
     * @param articleId
     */
    @Override
    public void likeCountDecrement(int articleId) {
        this.baseMapper.likeCountDecrement(articleId);
    }


    /**
     * 评论数自增
     *
     * @param articleId
     */
    @Override
    public void commentCountIncrement(int articleId) {
        this.baseMapper.commentCountIncrement(articleId);
    }

    /**
     * 评论数数自减
     *
     * @param articleId
     */
    @Override
    public void commentCountDecrement(int articleId) {
        this.baseMapper.commentCountDecrement(articleId);
    }


    /**
     * 收藏数自增
     *
     * @param articleId
     */
    @Override
    public void collectCountIncrement(int articleId) {
        this.baseMapper.collectCountIncrement(articleId);
    }

    /**
     * 收藏数自减
     *
     * @param articleId
     */
    @Override
    public void collectCountDecrement(int articleId) {
        this.baseMapper.collectCountDecrement(articleId);
    }

    /**
     * 分页查询用户收藏文章
     *
     * @param offset
     * @param limit
     * @param userId
     * @return
     */
    @Override
    public List<ArticleVo> selectCollectByUserId(long offset, long limit, Integer userId) {
        return this.baseMapper.selectCollectByUserId(offset, limit, userId);
    }

    /**
     * 更新分类名称（分类冗余字段）
     *
     * @param categoryId
     * @param newName
     */
    @Override
    public void updateCategoryName(int categoryId, String newName) {
        Article article = new Article();
        article.setCategoryName(newName);
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Article::getCategoryId, categoryId);
        update(article,queryWrapper);
    }

    /**
     * 只有一篇文章判断是上一篇还是下一篇
     *
     * @param dto
     * @param one
     * @param id
     */
    private void oneHandle(PreArtAndNextArtDTO dto, Article one, Integer id) {
        Integer oneId = one.getId();
        if (oneId > id) {
            dto.setNext(one);
        } else {
            dto.setPre(one);
        }
    }

    /**
     * 根据年月获取字符串获取对应月份开始时间和结束时间
     *
     * @param yearMonth
     * @return
     */
    private String[] getStartAndEndOfMonth(String yearMonth) {
        if (StringUtils.isBlank(yearMonth)) {
            return new String[]{null, null};
        }
        String separator = "-";
        String[] array = yearMonth.split(separator);
        String yearStr = array[0];
        String monthStr = array[1];
        int maxDayOfMonth = DateUtil.getMaxDayOfMonth(Integer.parseInt(yearStr), Integer.parseInt(monthStr));
        String start = yearStr + separator + monthStr + separator + "01";
        String end = yearStr + separator + monthStr + separator + maxDayOfMonth;
        return new String[]{start, end};
    }

    /**
     * 作者设置
     *
     * @param article
     * @param request
     * @return
     */
    private void setAuthor(Article article, ArticleRequest request) {
        // 是否原创
        Integer original = request.getOriginal();
        String reproduce = request.getReproduce();
        if (original.equals(0) && StringUtils.isBlank(reproduce)) {
            throw new ApiException(ErrorEnum.INVALID_REQUEST.getErrorCode(), "转载地址不能为空");
        }
        if (original.equals(0)) {
            article.setReproduce(reproduce);
        } else if (original.equals(1)) {
            article.setReproduce(null);
        } else {
            throw new ApiException(ErrorEnum.INVALID_REQUEST.getErrorCode(), "无效转载标识");
        }
        CustomUserDetails userDetail = ServeSecurityContext.getUserDetail(true);
        article.setUserId(userDetail.getId());
        article.setOriginal(original);
    }

    /**
     * 文章状态码校验
     *
     * @param status
     */
    private void validStatus(Integer status) {
        if (status == null) {
            return;
        }
        Integer[] array = {0, 1, 2};
        if (!ArrayUtils.contains(array, status)) {
            throw new ApiException(ErrorEnum.INVALID_REQUEST.getErrorCode(), "无效状态码");
        }
    }
}
