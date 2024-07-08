package com.fydata.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.fydata.entity.AbnormalOperation;
import java.io.*;
import java.util.List;

/**
 * @author xfgeng
 */
public class FileUtil {

    /**
     * 读取文件列表
     * @param fileDir
     * @return
     */
    public static File[] readFileList(String fileDir) {
        File file = new File(fileDir);
        if(file.isDirectory()){
            return file.listFiles();
        }
        return null;
    }

    /**
     * * 以字节为单位读取文件，常用于读二进制文件，如图片、声音、影像等文件
     * @param filePath
     * @return
     */
    public static byte[] readFileByByte(String filePath) throws IOException {
        FileInputStream in = null;
        try {
            File file = new File(filePath);
            in = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
            byte[] temp = new byte[1024];
            int size;
            while ((size = in.read(temp)) != -1) {
                out.write(temp, 0, size);
            }
            return out.toByteArray();
        } finally {
            if (in != null) {
                in.close();
            }
        }

    }

    /**
     * 读取json文件，返回json串
     * @param filePath
     * @return
     */
    public static String readFileByStr(String filePath) throws IOException {
        File file = new File(filePath);
        return readFileByStr(file);

    }

    /**
     * 读取json文件，返回json串
     * @param file
     * @return
     */
    public static String readFileByStr(File file) throws IOException {
        FileReader fileReader = null;
        Reader reader = null;
        try {
            fileReader = new FileReader(file);
            reader = new InputStreamReader(new FileInputStream(file),"utf-8");
            StringBuffer sb = new StringBuffer();
            int ch;
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            return sb.toString();
        } finally {
            if(null != fileReader) {
                fileReader.close();
            }
            if(null != reader) {
                reader.close();
            }
        }
    }


    public static void main(String[] args) throws IOException {
        File[] files = FileUtil.readFileList("E://data/abnormal_operation");
        for (File file:files) {
            String json = FileUtil.readFileByStr(file);
            List<AbnormalOperation> list = JSON.parseObject("["+json+"]", new TypeReference<List<AbnormalOperation>>() {});
            System.out.println(list.size());
        }
    }


}
