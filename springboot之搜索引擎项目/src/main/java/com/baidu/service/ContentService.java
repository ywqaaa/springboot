package com.baidu.service;

import com.alibaba.fastjson.JSON;
import com.baidu.pojo.Product;
import com.baidu.utils.htmltmparse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ContentService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    //1.解析数据放入es索引中
    public Boolean parseContent(String keywords) throws Exception{
//        List<Content> contents =new HtmlParseUtil().parseJD(keywords);

        List<Product> products =new htmltmparse().parsebk(keywords);
        //把查询到的数据放入es中
        BulkRequest bulkRequest=new BulkRequest();
        bulkRequest.timeout("2m");
        for (int i=0;i<products.size();i++){
            bulkRequest.add(new IndexRequest("bk").source(JSON.toJSONString(products.get(i)), XContentType.JSON));
        }
        BulkResponse bulk=restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulk.hasFailures();
    }

    //2.获取这些数据实现搜索高亮功能
    public List<Map<String,Object>> searchPage(String keyword,int pageNo,int pageSize) throws IOException {
        if (pageNo<=1){
            pageNo=1;
        }

        //条件搜索
        SearchRequest searchRequest =new SearchRequest("bk");
        SearchSourceBuilder sourceBuilder =new SearchSourceBuilder();

        //分页
        sourceBuilder.from(pageNo);
        sourceBuilder.size(pageSize);



        //使用布尔查询BoolQuery,可以实现将多个查询联合起来，比如将精确查询，全文检索组合起来查询
        BoolQueryBuilder boolQueryBuilder =QueryBuilders.boolQuery();
        //使用全文检索，会对关键词进行分词后匹配词条
        boolQueryBuilder.should(                                //使用should参数表示应该匹配should所包含的查询条件其中一个或多个，相当于or
                    QueryBuilders.matchQuery("title",keyword)  //使用全文检索，最大力度匹配
        );
        boolQueryBuilder.should(
                QueryBuilders.matchQuery("content",keyword)
        );

        sourceBuilder.query(boolQueryBuilder); //将查询条件封装给查询对象
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        //给关键词高亮
        HighlightBuilder highlightBuilder =new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.field("content");
        highlightBuilder.requireFieldMatch(false);
        highlightBuilder.fragmentSize(800000); //最多高亮分片数
        highlightBuilder.numOfFragments(0);
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        sourceBuilder.highlighter(highlightBuilder);
        //执行搜索
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse=restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);

        //解析结果
        ArrayList<Map<String,Object>> list=new ArrayList<>();
        for (SearchHit hit:searchResponse.getHits().getHits()){
            //获取高亮的属性
            Map<String, HighlightField> highlightFields=hit.getHighlightFields();
            HighlightField title=highlightFields.get("title");
            HighlightField content=highlightFields.get("content");
            Map<String,Object> sourceAsMap=hit.getSourceAsMap();

            //解析高亮的字段，将原来的字段换为我们高亮的字段
            if (title!=null){
                Text[] fragments=title.fragments();
                String n_title="";
                for (Text text:fragments){
                    n_title+=text;
                }
                sourceAsMap.put("title",n_title); //高亮的字段替换
            }
            if (content!=null){
                Text[] fragments=content.fragments();
                String n_content="";
                for (Text text:fragments){
                    n_content+=text;
                }
                sourceAsMap.put("content",n_content); //高亮的字段替换
            }
            list.add(sourceAsMap);
        }
        return list;
    }
}
