package com.cms.controller.front;

import com.alibaba.fastjson.JSONObject;
import com.cms.rabbitmq.RabbitMqFactory;
import com.cms.routes.RouteMapping;
import com.jfinal.kit.PropKit;
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

        String delivery = PropKit.get("delivery");
        String deliveryFee = PropKit.get("deliveryFee");

        setAttr("delivery",delivery);
        setAttr("deliveryFee",deliveryFee);

        render("/templates/" + getTheme() + "/" + getDevice() + "/index.html");
    }
}
