package com.fydata;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.fydata.dao.AbnormalOperationDao;
import com.fydata.entity.AbnormalOperation;
import com.fydata.util.FileUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;
import java.io.File;
import java.io.IOException;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AbnormalOperationTests {

	private static final String indexName = "abnormal_operation";

    @Autowired
    private AbnormalOperationDao abnormalOperationDao;

    @Test
    public void saveAllTest() throws IOException {
        long startTime = System.currentTimeMillis();
        File[] files = FileUtil.readFileList("E://data/abnormal_operation");
        for (File file:files) {
            String json = FileUtil.readFileByStr(file);
            List<AbnormalOperation> list = JSON.parseObject("["+json+"]", new TypeReference<List<AbnormalOperation>>() {});
            if(CollectionUtils.isEmpty(list)){
                continue;
            }
            abnormalOperationDao.saveAll(indexName, list);
            System.out.println(list.size());
        }
        long endTime = System.currentTimeMillis();
        System.out.println("导入完成时间：" + (endTime-startTime));
    }

}
