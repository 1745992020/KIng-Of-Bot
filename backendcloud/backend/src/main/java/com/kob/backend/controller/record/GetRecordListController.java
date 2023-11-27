package com.kob.backend.controller.record;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.service.record.GetRecordListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class GetRecordListController {
    @Autowired
    private GetRecordListService getRecordListService;
    @GetMapping("/api/record/getlist/")
    JSONObject getRecordList(@RequestParam Map<String,String> date){
        Integer page = Integer.parseInt(date.get("page"));
        return getRecordListService.getRecordList(page);
    }
}
