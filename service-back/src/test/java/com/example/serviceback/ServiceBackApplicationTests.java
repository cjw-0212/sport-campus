package com.example.serviceback;

import com.example.serviceback.constant.CacheName;
import com.example.serviceback.mapper.ActivityMapper;
import com.example.serviceback.mapper.ArticleMapper;
import com.example.serviceback.mapper.CommentMapper;
import com.example.serviceback.mapper.UserMapper;
import com.example.serviceback.po.Article;
import com.example.serviceback.po.User;
import com.example.serviceback.service.ArticleService;
import com.example.serviceback.service.EsService;
import com.example.serviceback.util.CacheDataUtils;
import com.example.serviceback.util.RedisDistanceUtils;
import com.example.serviceback.util.RedisUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author CJW
 * @since 2024/3/21
 */
@SpringBootTest
class ServiceBackApplicationTests {
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ActivityMapper activityMapper;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private RedisDistanceUtils redisDistanceUtils;
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private EsService esService;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    @Autowired
    private CacheDataUtils cacheDataUtils;
    @Autowired
    private ArticleService articleService;

    @Test
    void contextLoads() throws IOException {
        Random random = new Random();
        List<Long> articleIds = articleMapper.selectList(null).stream().map(Article::getId).toList();
        List<Long> userIds = userMapper.selectList(null).stream().map(User::getId).toList();
        for (Long articleId : articleIds) {
            for (int j = 0; j < random.nextInt(5); j++) {
                redisUtils.sAdd(CacheName.getArticleConcernUser(articleId), String.valueOf(userIds.get(random.nextInt(userIds.size()))));
            }
        }
        for (int i = 0; i < 10; i++) {
            cacheDataUtils.setUserInterData(articleIds.get(random.nextInt(articleIds.size())),
                    userIds.get(random.nextInt(userIds.size())));
        }
    }

    @Test
    public void testForOrPipeline() {
        //使用for
       /* StopWatch stopWatch1 = new StopWatch();
        stopWatch1.start();
        for (int i = 0; i < 10000; i++) {
            String value = String.valueOf(i);
            String key = "test1:" + value;
            stringRedisTemplate.opsForValue().set(key, value);
            stringRedisTemplate.delete(key);
        }
        stopWatch1.stop();
        System.out.println("for所需时间：" + stopWatch1.getTotalTimeSeconds() + "s");*/
        //使用管道
        StopWatch stopWatch2 = new StopWatch();
        stopWatch2.start();
        List<Long> userIds = new ArrayList();
        userIds.add(21251104203L);
        userIds.add(21251104204L);
        List<Object> list = stringRedisTemplate.executePipelined(new SessionCallback<>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                for (Long userId : userIds) {
                    operations.opsForSet().members(CacheName.getUserArticleConcern(userId));
                }
                return null;
            }
        });
        stopWatch2.stop();
        System.out.println("管道所需时间：" + stopWatch2.getTotalTimeSeconds() + "s");
        System.out.println(list);
    }

    @Test
    public void insertArticleData() {
        Random random = new Random();
        Long[] userIds = new Long[]{21251104203L, 21251104204L, 21251104205L, 21251104206L, 21251104207L, 21251104208L};
        String title = "运动时间按“需”选择";
        String content = "一天中什么时间段运动最科学？国家体育总局科研所国民体质研究中心副研究员武东明表示，运动时间可以按“需”选择，每个人要从运动目的、工作、生活实际来选择适合自己的运动时间。\n" +
                "\n" +
                "　　武东明说：“首先，每个人要根据自己的体质状况、闲暇时间、生活环境等实际情况选择一个固定的时间段进行运动，这样身体会慢慢习惯这个时间段，逐步形成生物钟，提前为运动做好相应的生理储备。其次，在时间允许的情况下，可以从运动生理学角度根据运动目标去选择时间段。”\n" +
                "\n" +
                "　　若以增强体质、提高免疫力、改善精神状态为目的的运动，推荐在早上进行。身体经过一晚上的消耗，基本处于低血糖状态，有氧运动会加速体内糖原的消耗，同时适当的有氧运动可以增强人体生物钟的调节，减少焦虑。值得注意的是，早上的时间段并不是适合每个人，比如患有高血压和心血管疾病人群，以及低血糖和糖尿病人群，由于这类人群血液黏稠度较高，且血糖水平较低，就不适合在早上进行运动。\n" +
                "\n" +
                "　　若以增长力量、肌肉塑形、提升耐力和柔韧为目的的运动，建议在中午或午后进行。由于这一时间段身体体能储备和身体感知度比较高，此时进行力量类和耐力类运动可以更好地把握动作及速度的精准性。但是中午运动一定要控制好运动与午餐的间隔，餐后1小时开始运动为最佳，午后运动强度要适中，尽量不影响下午学习、工作。\n" +
                "\n" +
                "　　若以减肥、修身、增加柔韧为目的的运动，可以安排在晚饭后进行。这一时间段会加速体内糖原的消耗，身体会快速调整供能方式，相比平时而言，晚上身体会更快达到消耗脂肪供能的条件，并且随着运动的进行，脂肪消耗增加，体内血脂水平大幅度下降。\n" +
                "\n" +
                "　　武东明表示，人体新陈代谢一般在傍晚达到高峰，人体的柔韧性和肾上腺激素分泌水平也达到峰值，此时非常有利于进行柔韧项目中难度相对较高的动作练习。同时，傍晚运动也要注意运动时间与晚饭、睡眠之间的间隔，一般安排在饭后1小时到睡前2小时的时间段内进行运动，以避免因运动而引起交感神经过度兴奋妨碍入睡。";
        for (int i = 0; i < 10; i++) {
            Article article = new Article();
            article.setTitle(title);
            article.setContent(content);
            article.setPublishUserId(userIds[random.nextInt(6)]);
            articleMapper.insert(article);
        }
    }

    @Test
    public void insertUserArticleConcern() {
        Random random = new Random();
        List<Long> articleIds = articleMapper.selectList(null).stream().map(Article::getId).toList();
        List<Long> userIds = userMapper.selectList(null).stream().map(User::getId).toList();
        for (Long userId : userIds) {
            for (int i = 0; i < 5; i++) {
                redisUtils.sAdd(CacheName.getUserArticleConcern(userId), String.valueOf(articleIds.get(random.nextInt(articleIds.size()))));
            }
        }
    }

    @Test
    public void test() {
        List<Long> userIds = userMapper.getAllUserId();
        for (Long userId : userIds) {
            articleService.prepareRecommendData(userId);
        }
    }
}
