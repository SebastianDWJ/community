package com.mybbs.community.service;

import com.mybbs.community.entity.DiscussPost;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ElasticsearchService {

    @Autowired
    DiscussPostService discussPostService;
    @Autowired
    ElasticsearchRepository elasticRepository;
    @Autowired
    ElasticsearchOperations operations;

    public void saveDiscussPost(DiscussPost discussPost){
        elasticRepository.save(discussPost);
    }

    public void deleteDiscussPost(int id){
        elasticRepository.deleteById(id);
    }

    public Map<String, Object> searchDiscussPost(String keyword, int current, int limit){
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword,"title","content"))
                .withSorts(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSorts(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSorts(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(current,limit))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                )
                .build();
        //查询
        SearchHits<DiscussPost> hits = operations.search(searchQuery, DiscussPost.class);
        List<DiscussPost> list = new ArrayList<>();
        for(SearchHit<DiscussPost> hit : hits){
            Map<String, List<String>> highlightFields = hit.getHighlightFields();
            //加上标签,没有检索到就不加
            hit.getContent().setTitle(highlightFields.get("title")==null?hit.getContent().getTitle():highlightFields.get("title").get(0));
            hit.getContent().setContent(highlightFields.get("content")==null?hit.getContent().getContent():highlightFields.get("content").get(0));

            list.add(hit.getContent());
        }
        int count = (int) hits.getTotalHits();
        Map<String, Object> map = new HashMap<>();
        map.put("count",count);
        map.put("list",list);
        return map;
    }
}
