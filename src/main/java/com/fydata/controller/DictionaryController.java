package com.fydata.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author xfgeng
 * @Date 2020/04/28 14:46
 * 后台API管理
 */
@Log4j2
@RestController
@RequestMapping("/api/dictionary")
public class DictionaryController {

    /**
     * 查询所有接口信息
     */
    @RequestMapping(path = "/custom")
    public String custom() {
        StringBuilder sb = new StringBuilder();
        sb.append("木桐").append("\n");
        sb.append("拉菲").append("\n");
        sb.append("奥利给");
        return sb.toString();
    }



}