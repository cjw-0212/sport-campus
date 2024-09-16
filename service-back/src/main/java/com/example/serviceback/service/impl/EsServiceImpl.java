package com.example.serviceback.service.impl;

import com.alibaba.fastjson.JSON;
import com.example.serviceback.esmodel.ArticleEsModel;
import com.example.serviceback.po.Article;
import com.example.serviceback.service.EsService;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author CJW
 * @since 2024/8/25
 */
@RequiredArgsConstructor
@Service
public class EsServiceImpl implements EsService {
    private final RestHighLevelClient restHighLevelClient;

    @Override
    public void saveArticle(Article article) throws IOException {
        ArticleEsModel articleEsModel = new ArticleEsModel();
        BeanUtils.copyProperties(article, articleEsModel);
        articleEsModel.setTitle(article.getTitle().replace("\n", "{##}"));
        articleEsModel.setContent(article.getContent().replace("\n", "{##}"));
        IndexRequest indexRequest = new IndexRequest("article");
        indexRequest.id(articleEsModel.getId().toString());
        String json = JSON.toJSONString(articleEsModel);
        indexRequest.source(json, XContentType.JSON);
        restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
    }

    @Override
    public List<ArticleEsModel> searchArticle(Long currentPage, Long pageSize, String keyword) throws IOException {
        SearchSourceBuilder searchSource = new SearchSourceBuilder();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.should(
                QueryBuilders.multiMatchQuery(keyword, "title", "content"));
        searchSource.query(boolQuery);
        //分页查询
        searchSource.from((int) ((currentPage - 1) * pageSize)).size(Math.toIntExact(pageSize));
        //排序条件
        searchSource.sort(SortBuilders.scoreSort())
                .sort(SortBuilders.fieldSort("agreeNumber").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC));
        //排除评分小于5的
        searchSource.minScore(0.5F);
        SearchRequest searchRequest = new SearchRequest(new String[]{"article"}, searchSource);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] hits = searchResponse.getHits().getHits();
        if (hits.length == 0) {
            return null;
        }
        return Arrays.stream(hits).map(hit -> {
            ArticleEsModel articleEsModel = new ArticleEsModel();
            Map<String, Object> sourceMap = hit.getSourceAsMap();
            articleEsModel.setId((Long) sourceMap.get("id"));
            articleEsModel.setTitle(sourceMap.get("title").toString().replace("{##}", "\n"));
            articleEsModel.setContent(sourceMap.get("content").toString().replace("{##}", "\n"));
            articleEsModel.setAgreeNumber((Integer) sourceMap.get("agreeNumber"));
            return articleEsModel;
        }).collect(Collectors.toList());
    }

    @Override
    public void updateAgreeNumber(Map<Object, Object> agreeNumMap) throws IOException {
        if (agreeNumMap.size() == 0) {
            return;
        }
        BulkRequest bulkRequest = new BulkRequest();
        agreeNumMap.forEach((articleId, agreeNumber) -> {
            UpdateRequest updateRequest = new UpdateRequest("article", articleId.toString());
            updateRequest.doc(XContentType.JSON, "agreeNumber", agreeNumber);
            bulkRequest.add(updateRequest);
        });
        restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);

    }
}
