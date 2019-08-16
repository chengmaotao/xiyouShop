package com.cms.service;

import com.cms.controller.front.PaymentController;
import com.cms.entity.CtcMember;
import com.cms.entity.CtcUserMember;
import com.cms.entity.Order;
import com.cms.entity.OrderItem;
import com.jfinal.log.Log;
import org.apache.commons.lang.time.DateUtils;

import java.util.Date;
import java.util.List;

/**
 * @Auther: CTC
 * @Date: 2019/8/16 17:34
 * @Description:
 */
public class CtcUserMemeberService {

    private static Log logger = Log.getLog(CtcUserMemeberService.class);

    //
    public void updateUserMember(Order order) {
        List<OrderItem> orderItems = new OrderItem().dao().find("select * from kf_order_item where orderId = ?", order.getId());
        if (orderItems == null || orderItems.size() == 0) {
            logger.error("更新用户会员状态错误：order = {" + order + "}");
        }

        for (OrderItem orderItem : orderItems) {
            Long productId = orderItem.getProductId();

            // 会员id 是从 900000000 开始的
            if(productId >= 900000000){

                CtcMember ctcMember = new CtcMember().dao().findById(productId);
                if(ctcMember == null){
                    logger.error("更新用户会员状态 会员不存在：order = {" + order + "}");
                    continue;
                }

                CtcUserMember ctcUserMember = new CtcUserMember();

                Date now = new Date();
                ctcUserMember.setCreateDate(now);
                ctcUserMember.setModifyDate(now);
                ctcUserMember.setUserId(order.getMemberId());
                ctcUserMember.setCtcMemberId(orderItem.getProductId());
                ctcUserMember.setExpireDayte(DateUtils.addDays(now,ctcMember.getExpireDays()));
                ctcUserMember.save();

            }else{
                logger.warn("更新用户会员状态，订单里包含非会员商品：order = {" + order + "}");
            }
        }
    }
}

