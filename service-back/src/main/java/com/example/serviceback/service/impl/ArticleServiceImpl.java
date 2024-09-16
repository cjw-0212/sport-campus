package com.example.serviceback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.serviceback.constant.CacheName;
import com.example.serviceback.controller.FileController;
import com.example.serviceback.esmodel.ArticleEsModel;
import com.example.serviceback.mapper.ArticleMapper;
import com.example.serviceback.mapper.ArticleMediaMapper;
import com.example.serviceback.mapper.CommentMapper;
import com.example.serviceback.mapper.UserMapper;
import com.example.serviceback.po.Article;
import com.example.serviceback.po.ArticleMedia;
import com.example.serviceback.po.Comment;
import com.example.serviceback.po.User;
import com.example.serviceback.service.ArticleService;
import com.example.serviceback.service.EsService;
import com.example.serviceback.util.CacheDataUtils;
import com.example.serviceback.util.RecommendUtils;
import com.example.serviceback.util.RedisUtils;
import com.example.serviceback.validation.dto.ArticleDTO;
import com.example.serviceback.vo.ArticleVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 体育资料表 服务实现类
 * </p>
 *
 * @author CJW
 * @since 2024-03-29
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleMediaMapper articleMediaMapper;
    @Autowired
    private FileController fileController;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CacheDataUtils cacheDataUtils;
    @Value("${file.requestPrefix}")
    private String mediaRequestPrefix;
    @Autowired
    private EsService esService;
    @Autowired
    private CommentMapper commentMapper;

    @Override
    public Long saveArticle(ArticleDTO articleDTO) {
        Article article = new Article();
        BeanUtils.copyProperties(articleDTO, article);
        articleMapper.insert(article);
        return article.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteArticle(Long id) {
        //删除文本数据
        articleMapper.deleteById(id);
        //查出对应的所有资源名称
        LambdaQueryWrapper<ArticleMedia> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleMedia::getArticleId, id);
        List<ArticleMedia> articleMediaList = articleMediaMapper.selectList(queryWrapper);
        articleMediaList.forEach(articleMedia -> {
            //删除真实媒体资源
            fileController.deleteFile(articleMedia.getUrl());
            //删除此条对应关系
            articleMediaMapper.deleteById(articleMedia.getId());
        });
        //删除相关缓存数据
        redisUtils.delete(CacheName.getArticleConcernUser(id));
        //删除对应的评论数据
        LambdaQueryWrapper<Comment> commentQueryWrapper = new LambdaQueryWrapper<>();
        commentQueryWrapper.eq(Comment::getArticleId, id);
        commentMapper.delete(commentQueryWrapper);
    }

    @Override
    public Page<ArticleVO> getPage(Long currentPage, Long pageSize, Long userId) {
        Page<Article> page = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getCreateTime).orderByDesc(Article::getAgreeNumber)
                .orderByDesc(Article::getCommentNumber);
        Page<Article> articlePage = articleMapper.selectPage(page, queryWrapper);
        Page<ArticleVO> articleVoPage = new Page<>();
        BeanUtils.copyProperties(articlePage, articleVoPage, "records");
        List<ArticleVO> articleVOList = new ArrayList<>(articlePage.getRecords().stream()
                .map(this::transformToArticleVO).toList());
        boolean toRecommend = redisUtils.setIfAbsent(CacheName.getRecommendLock(userId), "1");
        if (toRecommend) {
            insertRecommend(articleVOList, articlePage, userId);
            //30分钟内只会触发一次推荐
            redisUtils.expire(CacheName.getRecommendLock(userId), 30, TimeUnit.MINUTES);
        }
        articleVoPage.setRecords(articleVOList);
        return articleVoPage;
    }

    private void insertRecommend(List<ArticleVO> articleVOList, Page<Article> articlePage, Long userId) {
        //插入推荐
        Set<String> recommendSet = redisUtils.sDistinctRandomMembers(CacheName.getRecommendInclude(userId), 5);
        if (recommendSet.size() != 0) {
            //标记已被推荐的并删除
            redisUtils.sRemove(CacheName.getRecommendInclude(userId), recommendSet.toArray());
            redisUtils.sAdd(CacheName.getRecommendExclude(userId), recommendSet.toArray(new String[0]));
            //推荐id文章去重混合
            List<String> collect = articlePage.getRecords().stream().map(article -> article.getId().toString())
                    .toList();
            for (String s : collect) {
                if (recommendSet.contains(s)) {
                    recommendSet.remove(s);
                }
            }
            LambdaQueryWrapper<Article> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.in(Article::getId, recommendSet);
            List<Article> articles = articleMapper.selectList(queryWrapper1);
            List<ArticleVO> articleVOS = articles.stream().map(this::transformToArticleVO).toList();
            articleVOS.forEach(articleVO -> {
                articleVO.setIsRecommend(1);
                articleVOList.add(0, articleVO);
            });
        }
    }

    /**
     * 获取文章的媒体资源请求路径
     */
    private List<String> getMediaUrls(Long articleId) {
        LambdaQueryWrapper<ArticleMedia> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleMedia::getArticleId, articleId);
        List<ArticleMedia> articleMediaList = articleMediaMapper.selectList(queryWrapper);
        return articleMediaList.stream().map(articleMedia -> mediaRequestPrefix + articleMedia.getUrl()).toList();
    }

    /**
     * 根据article数据库映射对象获取articleVO视图对象
     */
    private ArticleVO transformToArticleVO(Article article) {
        //复制数据并添加媒体资源
        ArticleVO articleVO = new ArticleVO();
        BeanUtils.copyProperties(article, articleVO, "createTime");
        articleVO.setMedias(getMediaUrls(articleVO.getId()));
        //将时间转化为秒级时间戳
        articleVO.setCreateTime(article.getCreateTime().atZone(ZoneId.systemDefault()).toEpochSecond());
        //添加发布用户信息
        User find = userMapper.getNameAndAvatarById(articleVO.getPublishUserId());
        articleVO.setPublishUserName(find.getName());
        articleVO.setAvatar(mediaRequestPrefix + find.getAvatar());
        return articleVO;
    }


    @Override
    public void doAgree(Long articleId, Long userId, Boolean up) {
        //判断是否有缓存数据没有查询数据库并加入
        cacheDataUtils.ensureArticleAgreeNumCache(articleId);
        //更新文章点赞数量缓存数据
        if (up) {
            //记录用户交互过的文章id
            redisUtils.sAdd(CacheName.getUserArticleConcern(userId), articleId.toString());
            //记录文章被哪些用户交互过
            redisUtils.sAdd(CacheName.getArticleConcernUser(articleId), userId.toString());
            //修改点赞数量
            redisUtils.hIncrBy(CacheName.getArticleAgreeNumber(), articleId.toString(), 1);
            //加入用户点赞文章列表
            redisUtils.sAdd(CacheName.getUserArticleAgree(userId), articleId.toString());
            cacheDataUtils.setUserInterData(articleId, userId);
        } else {
            //移出用户交互文章列表
            redisUtils.sRemove(CacheName.getUserArticleConcern(userId), articleId.toString());
            //移除文章被用户交互列表
            redisUtils.sRemove(CacheName.getArticleConcernUser(articleId), userId.toString());
            //修改点赞数量
            redisUtils.hIncrBy(CacheName.getArticleAgreeNumber(), articleId.toString(), -1);
            //移出用户点赞文章列表
            redisUtils.sRemove(CacheName.getUserArticleAgree(userId), articleId.toString());
            cacheDataUtils.removeUserInterData(articleId, userId);
        }
    }

    @Override
    public List<String> getAgreeDataByUserId(Long userId) {
        Set<String> set = redisUtils.setMembers(CacheName.getUserArticleAgree(userId));
        return set.stream().toList();
    }

    @Override
    public void saveMedia(Long articleId, MultipartFile file) throws IOException {
        String fileName = fileController.upload(file);
        ArticleMedia articleMedia = new ArticleMedia();
        articleMedia.setArticleId(articleId);
        articleMedia.setUrl(fileName);
        articleMediaMapper.insert(articleMedia);
    }

    @Override
    public Page<ArticleVO> getPageByUserId(Long userId, Integer category, Long currentPage, Long pageSize) {
        Page<Article> articlePage = new Page<>(currentPage, pageSize);
        if (category == 0) {
            getPublishData(userId, articlePage);
        } else {
            getAgreeData(userId, articlePage);
        }
        Page<ArticleVO> articleVOPage = new Page<>(currentPage, pageSize);
        List<ArticleVO> articleVOS = articlePage.getRecords().stream().map(this::transformToArticleVO).toList();
        articleVOPage.setRecords(articleVOS);
        return articleVOPage;
    }

    @Override
    public List<ArticleVO> searchByKeyword(Long currentPage, Long pageSize, String keyword) throws IOException {
        List<ArticleEsModel> esModels = esService.searchArticle(currentPage, pageSize, keyword);
        if (esModels == null) {
            return new ArrayList<>();
        }
        Map<Long, String> titleMap = esModels.stream().collect(Collectors.toMap(ArticleEsModel::getId, ArticleEsModel::getTitle));
        Map<Long, String> contentMap = esModels.stream().collect(Collectors.toMap(ArticleEsModel::getId, ArticleEsModel::getContent));
        List<Long> idList = esModels.stream().map(ArticleEsModel::getId).toList();
        List<Article> articles = this.listByIds(idList);
        Collections.reverse(articles);
        List<ArticleVO> articleVOS = articles.stream().map(this::transformToArticleVO).toList();
        articleVOS.forEach(articleVO -> {
            articleVO.setTitle(titleMap.get(articleVO.getId()));
            articleVO.setContent(contentMap.get(articleVO.getId()));
        });
        return articleVOS;
    }

    public void getPublishData(Long userId, Page<Article> articlePage) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getPublishUserId, userId).orderByDesc(Article::getCreateTime);
        this.page(articlePage, queryWrapper);
    }

    public void getAgreeData(Long userId, Page<Article> articlePage) {
        List<String> articleIds = getAgreeDataByUserId(userId);
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Article::getId, articleIds).orderByDesc(Article::getCreateTime);
        this.page(articlePage, queryWrapper);
    }

    @Override
    public void prepareRecommendData(Object targetUserId) {
        //目标用户与相关用户的共同数
        Map<Object, Object> userInterMap = redisUtils.hGetAll(CacheName.getUserInter(targetUserId));
        //涉及到的用户id
        Set<Object> allUserId = userInterMap.keySet();
        //用户与交互数量map
        Map<Object, Integer> userToNumMap = new HashMap<>();
        Map<Object, Set<String>> includeAboutArticleMap = new HashMap<>();
        for (Object userId : allUserId) {
            Set<String> articleIdSet = redisUtils.setMembers(CacheName.getUserArticleConcern(userId));
            userToNumMap.put(userId, articleIdSet.size());
            includeAboutArticleMap.put(userId, articleIdSet);
        }
        Set<String> excludeArticleSet = redisUtils.setMembers(CacheName.getUserArticleConcern(targetUserId));
        Set<String> recommendExcludeSet = redisUtils.setMembers(CacheName.getRecommendExclude(targetUserId.toString()));
        excludeArticleSet.addAll(recommendExcludeSet);
        userToNumMap.put(targetUserId, excludeArticleSet.size());
        Set<String> recommend = RecommendUtils.recommend(targetUserId, userInterMap, userToNumMap, excludeArticleSet, includeAboutArticleMap);
        if (recommend.size() != 0) {
            redisUtils.sAdd(CacheName.getRecommendInclude(targetUserId), recommend.toArray(new String[0]));
        }
    }
}
