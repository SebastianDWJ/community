package com.mybbs.community;

import com.mybbs.community.dao.DiscussPostMapper;
import com.mybbs.community.dao.elasticsearch.DiscussPostRepository;
import com.mybbs.community.entity.DiscussPost;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.BaseQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class ElasticsearchTests {
    @Autowired
    private DiscussPostMapper discussMapper;
    @Autowired
    private DiscussPostRepository discussRepository;
//    @Autowired
//    private ElasticsearchRestTemplate template;
    @Autowired
    private ElasticsearchOperations operations;

    @Test
    public void testInsert(){
        discussRepository.save(discussMapper.selectDiscussPostById(241));
        discussRepository.save(discussMapper.selectDiscussPostById(242));
        discussRepository.save(discussMapper.selectDiscussPostById(243));
    }

    @Test
    public void testInsertList(){
        discussRepository.saveAll(discussMapper.selectDiscussPosts(101,0,100));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(102,0,100));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(103,0,100));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(111,0,100));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(112,0,100));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(131,0,100));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(132,0,100));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(133,0,100));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(134,0,100));
    }

    @Test
    public void testUpdate(){
        DiscussPost discussPost = discussMapper.selectDiscussPostById(231);
        discussPost.setContent("我是新人，使劲灌水！");
        discussRepository.save(discussPost);
    }

    @Test
    public void testDelete(){
//        discussRepository.deleteById(231);
        discussRepository.deleteAll();
    }

    @Test
    public void testSearchByRepository(){
        BaseQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬","title","content"))
                .withSorts(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSorts(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSorts(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0,10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();
        //查询
        Iterable<DiscussPost> discussPosts = discussRepository.findAll();
//        SearchHits<DiscussPost> search = operations.search(searchQuery, DiscussPost.class);
//        List<SearchHit<DiscussPost>> searchHits = search.getSearchHits();
//        for (SearchHit<DiscussPost> searchHit : searchHits) {
//            Map<String, List<String>> highlightFields = searchHit.getHighlightFields();
//            System.out.println(highlightFields);
//        }
        for (DiscussPost discussPost : discussPosts) {
            System.out.println(discussPost);

        }

    }

    @Test
    public void testSearchByTemplate(){
        BaseQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬","title","content"))
                .withSorts(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSorts(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSorts(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0,10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();
        //查询，此处改为operations
        SearchHits<DiscussPost> search = operations.search(searchQuery, DiscussPost.class);
        long count = operations.count(searchQuery,DiscussPost.class);
        System.out.println(count);//匹配的数量 111
        System.out.println(search.getTotalHits());//111
        System.out.println(search.getTotalHitsRelation());//EQUAL_TO

        //得到查询返回的内容
        List<SearchHit<DiscussPost>> searchHits = search.getSearchHits();
        ArrayList<DiscussPost> discussPosts = new ArrayList<>();
        for (SearchHit<DiscussPost> searchHit : searchHits) {
            //获取高亮的内容
            Map<String, List<String>> highlightFields = searchHit.getHighlightFields();
            // 将高亮的内容添加到content中(匹配到的如果是多段，就将第一段高亮显示)
            // 没有匹配到关键字就显示原来的title和content
            searchHit.getContent().setTitle(highlightFields.get("title")==null?searchHit.getContent().getTitle() : highlightFields.get("title").get(0));
            searchHit.getContent().setContent(highlightFields.get("content")==null?searchHit.getContent().getContent() : highlightFields.get("content").get(0));

            discussPosts.add(searchHit.getContent());
        }

        for(DiscussPost discussPost: discussPosts){
            System.out.println(discussPost);
        }
    }

}
