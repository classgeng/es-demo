package com.fydata;

import com.fydata.entity.FileInfo;
import com.fydata.es.EsClientHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EsFileInfoTests {

	private static final String INDEX_NAME = "doc_index_test";

	@Autowired
	private EsClientHandler esClientHandler;


	@Test
	public void saveRestTest() throws IOException {
		FileInfo fileInfo = new FileInfo();
		fileInfo.setId("111");
		fileInfo.setName("aaa");
		fileInfo.setContent("bbb");

		esClientHandler.save(INDEX_NAME, fileInfo);
	}

}
