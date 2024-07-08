package com.fydata;

import com.fydata.dao.AttachmentDao;
import com.fydata.entity.Document;
import com.fydata.es.EsQueryBuilder;
import com.fydata.es.PageBuilder;
import com.fydata.util.Base64Util;
import com.fydata.util.FileUtil;
import com.fydata.vo.PageVO;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
@RunWith(SpringRunner.class)
public class EsAttachmentTests {

	private static final String INDEX_NAME = "msdap_document_test";
	private static final String[] INCLUDES = {"id","orgName","type","size","createTime","createBy","attachment.title","attachment.content"};
	private static final String FIELD = "attachment.content";

	public static final String CAR_CARD_REGEXP = "([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][a-zA-Z][a-hA-Hj-nJ-Np-zP-Z0-9]{4}[a-hA-Hj-nJ-Np-zP-Z0-9挂学警港澳])";
	public static final String CAR_CARD_EXPRESS = ".*[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][a-zA-Z][a-zA-Z0-9]{4}[a-zA-Z0-9挂学警港澳].*";
	public static final String CAR_CARD_REGEXP_PRE = "[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领]";
	public static final String CAR_CARD_REGEXP_CENTER = "[a-zA-Z]([a-zA-Z0-9]{4}|[a-zA-Z0-9]{5})";

	public static final String PHONE_REGEXP_ES = "(13[0-9]|14[5-9]|15[0-3,5-9]|16[2,5-7]|17[0-8]|18[0-9]|19[0-3,5-9])[0-9]{8}";
	public static final String IP_EXPRESS_ES = "((1[0-9][0-9]\\.)|(2[0-4][0-9]\\.)|(25[0-5]\\.)|([1-9][0-9]\\.)|([0-9]\\.)){3}((1[0-9][0-9])|(2[0-4][0-9])|(25[0-5])|([1-9][0-9])|([0-9]))";
	public static final String DOMAIN_REGEXP_ES = "[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+\\.?";


	@Autowired
    private AttachmentDao attachmentDao;

	@Test
	public void saveTest() throws IOException {
		//保存数据
		String filePath = "D:\\dzqz\\4200002019001010011574500281\\源文件\\分词测试.txt";
		byte[] content = FileUtil.readFileByByte(filePath);
		Document document = new Document();
		document.setId("111");
		document.setOrgName(filePath.substring(filePath.lastIndexOf("\\") + 1));
		document.setContent(Base64Util.stringToEncode(content));
		attachmentDao.saveAttachment(INDEX_NAME, document);
	}

	@Test
	public void findByIdTest() {
		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		queryBuilder.filter(QueryBuilders.termQuery("id","8959dcceaa2c45898d425a9aaa6bf404"));

		Iterable<Document> attachments = attachmentDao.search(INDEX_NAME, queryBuilder);
		if(null == attachments){
			return;
		}
		Document document = attachments.iterator().next();
		System.out.println(document.getId());
		System.out.println(document.getOrgName());
		System.out.println(document.getTitle());
		System.out.println(document.getContent());

	}

	@Test
	public void searchByPageTest() {
		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		//queryBuilder.must(QueryBuilders.regexpQuery("attachment.content.keyword", CAR_CARD_REGEXP));
		queryBuilder.filter(QueryBuilders.termsQuery("attachment.serName","手机","数据","规范"));

		EsQueryBuilder esQueryBuilder = new EsQueryBuilder();
		esQueryBuilder.setFields(INCLUDES);
		esQueryBuilder.setQueryBuilder(queryBuilder);

		SortBuilder sortBuilder = SortBuilders.fieldSort("createTime").order(SortOrder.DESC);
		PageBuilder pageBuilder = new PageBuilder(1,10);
		PageVO<Document> page = attachmentDao.searchByPage(INDEX_NAME, esQueryBuilder, sortBuilder, pageBuilder);

		page.getPageList().forEach(item -> {
			System.out.println(item.getId() + " -> " + item.getOrgName() + " -> " + item.getType() + " -> " + item.getTitle());
		});
	}

	@Test
	public void searchHighlightTest() {

		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		BoolQueryBuilder regexpQuery = QueryBuilders.boolQuery();
		regexpQuery.should(QueryBuilders.regexpQuery(FIELD, PHONE_REGEXP_ES));
		regexpQuery.should(QueryBuilders.regexpQuery(FIELD+".car_number", CAR_CARD_REGEXP));
		queryBuilder.must(regexpQuery);
		//queryBuilder.must(QueryBuilders.termQuery("id","be1863ff9b6a437c8ca7887ac70a4791"));
		//queryBuilder.must(QueryBuilders.matchQuery(FIELD+".car_number","湘M77v7n"));
		/*BoolQueryBuilder bb = QueryBuilders.boolQuery();
		bb.should(QueryBuilders.termsQuery("attachment.content","很"));
		bb.should(QueryBuilders.matchPhraseQuery("attachment.content","很"));
		queryBuilder.must(bb);*/

		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.fetchSource(INCLUDES,null);
		searchSourceBuilder.query(queryBuilder);
		searchSourceBuilder.highlighter(this.buildHighlightBuilder(FIELD));

		SearchResponse response = attachmentDao.search(INDEX_NAME, searchSourceBuilder);
		response.getHits().forEach(hit -> {
			Map<String, Object> sourceAsMap = hit.getSourceAsMap();
			sourceAsMap.forEach((key,value) -> System.out.println(key + " -> " + value));
			System.out.println("############################################");
			Map<String, HighlightField> highlightFields = hit.getHighlightFields();
			String content = buildContent(highlightFields);
			long count = appearNumber(content,"</esmark>");
			System.out.println(count + " -> " + content);
		});
	}

	private HighlightBuilder buildHighlightBuilder(String field){
		HighlightBuilder highlightBuilder = new HighlightBuilder();
		highlightBuilder.field(field);
		highlightBuilder.field(field+".car_number");
		highlightBuilder.requireFieldMatch(true);//多个高亮显示
		highlightBuilder.preTags("<esmark>").postTags("</esmark>");
		highlightBuilder.numOfFragments(250).fragmentSize(Integer.MAX_VALUE).highlighterType("plain");
		return highlightBuilder;
	}

	public String buildContent(Map<String, HighlightField> highlightFields) {
		HighlightField carContent = highlightFields.get("attachment.content.car_number");
		HighlightField attContent = highlightFields.get("attachment.content");
		if(null != attContent && null != carContent){
			return regexpMatchResolve(attContent.fragments()[0].toString());
		}
		if(null == carContent){
			return attContent.fragments()[0].toString();
		}
		if(null == attContent){
			return carContent.fragments()[0].toString();
		}
		return null;
	}

	/**
	 * 获取指定字符串出现的次数
	 *
	 * @param srcText 源字符串
	 * @param findText 要查找的字符串
	 * @return
	 */
	public long appearNumber(String srcText, String findText) {
		long count = 0;
		Pattern p = Pattern.compile(findText);
		Matcher m = p.matcher(srcText);
		while (m.find()) {
			count++;
		}
		return count;
	}

	public String regexpMatchResolve(String content) {
		Matcher matcher = Pattern.compile(CAR_CARD_REGEXP).matcher(content);
		while (matcher.find()) {
			String carCard = matcher.group();
			content = content.replace(carCard, "<esmark>" + carCard + "</esmark>");
		}
		return content;
	}

	@Test
	public void mainTest() {
		String content = "6.除用小图标显示发送方、接收方、www..aa.baidu.com主被叫、已接听/未接听等状态以外，同时需要保留文字描述10.15.11.1。";
		Matcher matcher = Pattern.compile(DOMAIN_REGEXP_ES).matcher(content);
		while (matcher.find()) {
			String phone = matcher.group();
			content = content.replace(phone, "<es>" + phone + "</es>");
		}
		System.out.println(content);
	}

}
