package com.kob.backend.service.record;

import com.alibaba.fastjson2.JSONObject;

import java.util.List;

public interface GetRecordListService {
    JSONObject getRecordList(Integer page);
}
