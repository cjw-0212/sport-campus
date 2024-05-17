package com.example.serviceback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.serviceback.constant.CacheName;
import com.example.serviceback.controller.FileController;
import com.example.serviceback.mapper.ArticleMapper;
import com.example.serviceback.mapper.ArticleMediaMapper;
import com.example.serviceback.mapper.UserMapper;
import com.example.serviceback.po.Article;
import com.example.serviceback.po.ArticleMedia;
import com.example.serviceback.po.User;
import com.example.serviceback.service.ArticleService;
import com.example.serviceback.util.CacheUtils;
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
import java.util.List;
import java.util.Set;

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
    private CacheUtils cacheUtils;
    @Value("${file.requestPrefix}")
    private String mediaRequestPrefix;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long saveArticle(ArticleDTO articleDTO){
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
    }

    @Override
    public Page<ArticleVO> getPage(Long currentPage, Long pageSize, String title) {
        Page<Article> page = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(title != null && title.length() != 0, Article::getTitle, title);
        Page<Article> articlePage = articleMapper.selectPage(page, queryWrapper);
        Page<ArticleVO> articleVoPage = new Page<>();
        BeanUtils.copyProperties(articlePage, articleVoPage, "records");
        List<ArticleVO> articleVOList = articlePage.getRecords().stream().map(this::transformToUserVO).toList();
        articleVoPage.setRecords(articleVOList);
        return articleVoPage;
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
    private ArticleVO transformToUserVO(Article article) {
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
        cacheUtils.handleArticleNumberNoExist(articleId);
        //更新文章点赞数量缓存数据
        if (up) {
            redisUtils.hIncrBy(CacheName.getArticleNumber(articleId), CacheName.AGREE_NUMBER_FIELD, 1);
            redisUtils.sAdd(CacheName.getUserArticleAgree(userId), articleId.toString());
        } else {
            redisUtils.hIncrBy(CacheName.getArticleNumber(articleId), CacheName.AGREE_NUMBER_FIELD, -1);
            redisUtils.sRemove(CacheName.getUserArticleAgree(userId), articleId.toString());
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
        ArticleMedia articleMedia=new ArticleMedia();
        articleMedia.setArticleId(articleId);
        articleMedia.setUrl(fileName);
        articleMediaMapper.insert(articleMedia);
    }
}
