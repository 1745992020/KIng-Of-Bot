package com.kob.backend.controller.pk;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pk/")
public class IndexController {
    @RequestMapping("/info/")
    public Map<String,String> test(){
        Map<String,String> map=new HashMap<>();
        map.put("name","王业晟");
        map.put("score","100分");
        return map;
    }
}
