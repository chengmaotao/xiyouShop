package com.cms.controller.front;

import com.alibaba.fastjson.JSONObject;
import com.cms.rabbitmq.RabbitMqFactory;
import com.cms.routes.RouteMapping;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Controller - 首页
 */
@RouteMapping(url = "/")
public class IndexController extends BaseController {


    /**
     * 首页
     */
    public void index() {

/*        JSONObject msg = new JSONObject();
        msg.put("mobile", "15664008013");
        msg.put("smsContent", "【资债通】验证码：{0}，10分钟内有效，请勿泄漏给他人！");
        msg.put("smsCode", "258963");
        msg.put("b",101);
        msg.put("smsType","SMS_CODE");
        RabbitMqFactory.getInstances().convertAndSend("smscenter.sms.queue", msg);*/

        render("/templates/" + getTheme() + "/" + getDevice() + "/index.html");
    }
}
