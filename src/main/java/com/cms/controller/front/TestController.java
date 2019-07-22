package com.cms.controller.front;

import com.alibaba.fastjson.JSONObject;
import com.cms.Feedback;
import com.cms.entity.Receiver;
import com.cms.routes.RouteMapping;
import com.jfinal.log.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: CTC
 * @Date: 2019/7/19 17:37
 * @Description:
 */
@RouteMapping(url = "/testk")
public class TestController extends BaseController {

    private static Log logger = Log.getLog(TestController.class);

    public void index(){
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("wife1","温博博");
        map.put("key1","李倩");
        //Object jsonObject = JSONObject.toJSON(map);

        System.out.println("日志我来了");
        logger.info("日志loggerinfo 我来了");

        renderJson(Feedback.success(map));
    }

    public void testredirect(){


        System.out.println("testredirect日志我来了");
        logger.info("testredirect日志loggerinfo 我来了");

        redirect("/member/order/detail/" + 57);
    }

    public void delete(){
        renderJson(Feedback.success(""));
    }
}
