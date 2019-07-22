package com.cms.controller.front.member;

import com.cms.CommonAttribute;
import com.cms.controller.front.BaseController;
import com.cms.entity.Member;
import com.cms.entity.Order;
import com.cms.routes.RouteMapping;
import com.jfinal.kit.PropKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;


/**
 * Controller - 订单
 */
@RouteMapping(url = "/member/order")

public class OrderController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    /**
     * 列表
     */
    public void list() {
        Integer pageNumber = getParaToInt("pageNumber");
        String status = getPara("status");
        if (pageNumber == null) {
            pageNumber = 1;
        }
        int pageSize = 20;
        Member currentMember = getCurrentMember();
        setAttr("page", new Order().dao().findPage(pageNumber, pageSize, currentMember.getId(), status));


        render("/templates/" + getTheme() + "/" + getDevice() + "/memberOrderList.html");
    }


    public void detail() {
        Long id = getParaToLong(0);
        Order order = new Order().dao().findById(id);

        String delivery = PropKit.get("delivery");
        String deliveryFee = PropKit.get("deliveryFee");

        BigDecimal totalPrice = order.getTotalPrice();
        setAttr("hasDeliveryFee", false);
        if (totalPrice.compareTo(new BigDecimal(delivery)) < 0) {
            setAttr("hasDeliveryFee", true);
            setAttr("deliveryFee", deliveryFee);
        }

        setAttr("order", order);
        render("/templates/" + getTheme() + "/" + getDevice() + "/memberOrderDetail.html");
    }

    /**
     * 已收货
     */
    public void received() {
        Long id = getParaToLong("id");
        Order order = new Order().dao().findById(id);
        order.setStatus(CommonAttribute.ORDER_STATUS_RECEIVED);
        order.update();

        redirect("/member/order/list");
    }
}
