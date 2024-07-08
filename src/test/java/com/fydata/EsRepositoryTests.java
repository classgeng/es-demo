package com.fydata;

import com.fydata.dao.UserInfoEsDao;
import com.fydata.entity.UserInfo;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EsRepositoryTests {

	private static final String indexName = "aj_42000020200720_index_test_fydata";
    private static final String fieldName = "remark";

    public static final String PHONE_REGEXP = "(13[0-9]|14[5-9]|15[0-3,5-9]|16[2,5-7]|17[0-8]|18[0-9]|19[0-3,5-9])[0-9]{8}";

    public static final String CAR_CARD_REGEXP_PRE = ".*[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领].*";
    public static final String CAR_CARD_REGEXP_CENTER = "[a-zA-Z]([a-zA-Z0-9]{4}|[a-zA-Z0-9]{5}).*";
    public static final String CAR_CARD_REGEXP_END = ".*[挂学警港澳].*";
    public static final String CAR_CARD_REGEXP = ".*[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][a-zA-Z][a-zA-Z0-9]{4}[a-zA-Z0-9挂学警港澳].*";
    public static final String EXPRESS = "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}[A-Z0-9]{4}[A-Z0-9挂学警港澳]{1}$";

    @Autowired
    private UserInfoEsDao userInfoEsDao;

	@Test
	public void saveTest() {
		UserInfo userInfo = new UserInfo();
        userInfo.setId(UUID.randomUUID().toString());
        userInfo.setName("张三4");
        userInfo.setTitle("张三1车牌号云鄂A77N8R澳门，1213大多数");
        userInfo.setAge(21);
        userInfo.setBirthday(new Date());
        userInfo.setAddress("武汉市洪山区文化大道");
        userInfo.setRemark("张三1车牌号云贵a77n8澳门，1213大多数");
        userInfo.setSex(1);
        userInfoEsDao.save(indexName,userInfo);
	}

    @Test
    public void saveAllTest() {
        UserInfo user1 = new UserInfo();
        user1.setId(UUID.randomUUID().toString());
        user1.setName("张三1");
        user1.setTitle("test123");
        user1.setAge(18);
        user1.setBirthday(new Date());
        user1.setAddress("武汉市洪山区文化大道1");
        user1.setRemark("张三1车牌号鄂a77n8r，1213大多数");

        UserInfo user2 = new UserInfo();
        user2.setId(UUID.randomUUID().toString());
        user2.setName("张三2");
        user2.setAge(19);
        user2.setBirthday(new Date());
        user2.setAddress("武汉市洪山区文化大道2，手机号16602730038");
        user2.setRemark("张三2车牌号豫S77N9R，1213大多数");

        UserInfo user3 = new UserInfo();
        user3.setId(UUID.randomUUID().toString());
        user3.setName("张三3");
        user3.setAge(20);
        user3.setBirthday(new Date());
        user3.setAddress("武汉市洪山区文化大道3，手机号13802730038");

        List<UserInfo> list = new ArrayList<>();
        list.add(user1);
        list.add(user2);
        list.add(user3);
        userInfoEsDao.saveAll(indexName, list);
    }

	@Test
	public void deleteTest() {
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.filter(QueryBuilders.termsQuery("name", "张三2"));
        userInfoEsDao.deleteByQuery(indexName,boolQueryBuilder);
	}

	@Test
	public void searchTest() {
		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		//queryBuilder.must(QueryBuilders.regexpQuery("address.keyword", PHONE_REGEXP));
        queryBuilder.must(QueryBuilders.regexpQuery("remark", CAR_CARD_REGEXP));
        //queryBuilder.should(QueryBuilders.regexpQuery("remark", CAR_CARD_REGEXP1));
        Iterable<UserInfo> list = userInfoEsDao.search(indexName, queryBuilder);
        list.forEach(item -> {
			System.out.println(item.getId() + " -> " + item.getName() + " -> " + item.getAddress() + " -> " + item.getRemark());
		});
	}

    @Test
    public void searchHighlightTest() {

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.regexpQuery("remark.keyword", CAR_CARD_REGEXP));
        //queryBuilder.must(QueryBuilders.regexpQuery(fieldName, CAR_CARD_REGEXP_PRE));
        //queryBuilder.must(QueryBuilders.regexpQuery(fieldName, CAR_CARD_REGEXP_CENTER));
        //queryBuilder.must(QueryBuilders.regexpQuery("remark", CAR_CARD_REGEXP_END));

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(Integer.MAX_VALUE);
        searchSourceBuilder.query(queryBuilder);
        HighlightBuilder highlightBuilder = buildHighlightBuilder("remark.keyword", "<esmark>", "</esmark>");
        searchSourceBuilder.highlighter(highlightBuilder);

        List<Map<String, Object>> result = new ArrayList<>();
        SearchResponse response = userInfoEsDao.search(indexName, searchSourceBuilder);
        response.getHits().forEach(hit -> {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            sourceAsMap.forEach((key,value) -> System.out.println(key + " -> " + value));
            System.out.println("############################################");
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            highlightFields.forEach((key,value) -> {
                String fragment = value.fragments()[0].toString();
                System.out.println(key + " -> " + fragment);
            });
            //this.regexPattern(result,sourceAsMap);
        });
        result.forEach(item -> System.out.println(item));
    }

    private HighlightBuilder buildHighlightBuilder(String field,String preTag,String postTag){
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field(field);
        highlightBuilder.preTags(preTag).postTags(postTag);
        highlightBuilder.numOfFragments(250).fragmentSize(Integer.MAX_VALUE).highlighterType("plain");
        return highlightBuilder;
    }

    private void regexPattern(List<Map<String, Object>> result, Map<String, Object> sourceAsMap){
        String remark = sourceAsMap.get(fieldName).toString();
        Pattern phonePattern = Pattern.compile(CAR_CARD_REGEXP);
        Matcher matcher = phonePattern.matcher(remark);
        while (matcher.find()){
            String highlightRemark = remark.replaceAll(matcher.group(), "<esmark>" + matcher.group() + "</esmark>");
            sourceAsMap.put("highlightRemark",highlightRemark);
            result.add(sourceAsMap);
        }
    }

}
