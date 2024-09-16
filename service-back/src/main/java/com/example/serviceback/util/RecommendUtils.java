package com.example.serviceback.util;

import java.util.*;

/**
 * @author CJW
 * @since 2024/8/27
 */
public class RecommendUtils {
    /**
     * 返回推荐的文章id集合
     *
     * @param targetUserId           被推荐的用户id
     * @param userInterMap           用户共同交互数量map
     * @param userToNumMap           用户交互过的总数量map
     * @param excludeArticle         被推荐用户交互过的文章
     * @param includeAboutArticleMap 相关用户的交互文章数据
     * @return 推荐文章id集合
     */
    public static Set<String> recommend(Object targetUserId, Map<Object, Object> userInterMap,
                                        Map<Object, Integer> userToNumMap, Set<String> excludeArticle,
                                        Map<Object, Set<String>> includeAboutArticleMap) {
        //计算相似度，交互文章交集数量/交互文章并集数量
        Map<Object, Double> similarityMap = new HashMap<>();
        for (Object includeId : userInterMap.keySet()) {
            int interNum = Integer.parseInt((String) userInterMap.get(includeId));
            int unionNum = userToNumMap.get(targetUserId) + userToNumMap.get(includeId) - interNum;
            if (unionNum == 0) {
                similarityMap.put(includeId, 0.0);
            } else {
                Double similarity = interNum * 1.0 / unionNum;
                similarityMap.put(includeId, similarity);
            }
        }
        //获取相似度前十
        ArrayList<Map.Entry<Object, Double>> entries = new ArrayList<>(similarityMap.entrySet());
        entries.sort((o1, o2) -> Double.compare(o2.getValue(), o1.getValue()));
        Set<String> allArticle = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            if (i >= entries.size()) {
                break;
            }
            allArticle.addAll(includeAboutArticleMap.get(entries.get(i).getKey()));
        }
        Set<String> recommendArticle = new HashSet<>();
        for (String article : allArticle) {
            if (!excludeArticle.contains(article)) {
                recommendArticle.add(article);
            }
        }
        return recommendArticle;
    }
}
