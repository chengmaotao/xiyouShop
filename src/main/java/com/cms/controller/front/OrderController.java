package com.cms.controller.front;

import com.cms.CommonAttribute;
import com.cms.Config;
import com.cms.Feedback;
import com.cms.entity.*;
import com.cms.routes.RouteMapping;
import com.cms.util.SystemUtils;
import com.cms.util.XiYouUtils;
import com.jfinal.kit.PropKit;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * Controller - 订单
 */
@RouteMapping(url = "/order")
public class OrderController extends BaseController {


    /**
     * 添加
     */
    public void add() {
        Member currentMember = getCurrentMember();
        Receiver defaultReceiver = new Receiver().dao().findDefault(currentMember.getId());
        if (defaultReceiver == null) {
            redirect("/receiver/add");
        } else {
            Cart currentCart = getCurrentCart();
            setAttr("currentCart", currentCart);
            setAttr("defaultReceiver", defaultReceiver);

            String delivery = PropKit.get("delivery");
            String deliveryFee = PropKit.get("deliveryFee");

            setAttr("delivery", delivery);
            setAttr("deliveryFee", deliveryFee);

            boolean member = XiYouUtils.getByMember(currentMember);
            setAttr("isMember", member);
            BigDecimal totalPrice = BigDecimal.ZERO;
            if (member) {
                totalPrice = currentCart.getTotalPrice();
            } else {
                totalPrice = currentCart.getMarketTotalPrice();
            }

            setAttr("hasDeliveryFee", false);
            if (totalPrice.compareTo(new BigDecimal(delivery)) < 0) {
                totalPrice = totalPrice.add(new BigDecimal(deliveryFee));
                setAttr("hasDeliveryFee", true);
            }

            setAttr("realTotalPrice", totalPrice);

            render("/templates/" + getTheme() + "/" + getDevice() + "/orderAdd.html");
        }
    }

    /**
     * 保存
     */
    public void save() {
        Order order = getModel(Order.class, "", true);
        Member currentMember = getCurrentMember();
        Cart currentCart = getCurrentCart();

        List<CartItem> cartItems = currentCart.getCartItems();

        if (cartItems == null || cartItems.isEmpty()) {
            int pageSize = 20;
            setAttr("page", new Order().dao().findPage(1, pageSize, currentMember.getId(), null));

            render("/templates/" + getTheme() + "/" + getDevice() + "/memberOrderList.html");
            return;
        }

        String delivery = PropKit.get("delivery");
        String deliveryFee = PropKit.get("deliveryFee");

        //  西柚会员  TODO
        boolean byMember = XiYouUtils.getByMember(currentMember);
        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        if (byMember) {
            totalAmount = currentCart.getTotalPrice();
        } else {
            totalAmount = currentCart.getMarketTotalPrice();
        }

        totalPrice = totalAmount;

        if (totalPrice.compareTo(new BigDecimal(delivery)) < 0) {
            totalPrice = totalPrice.add(new BigDecimal(deliveryFee));
        }
        Date now = new Date();
        order.setCreateDate(now);
        order.setModifyDate(now);

        Integer orderExpireTimes = PropKit.getInt("order.expire.time", 30);

        order.setExpire(DateUtils.addMinutes(now, orderExpireTimes));
        order.setAmount(totalAmount);
        order.setTotalPrice(totalPrice);
        order.setQuantity(currentCart.getQuantity());
        order.setSn(DateFormatUtils.format(now, "yyyyMMddHHmmssSSS") + RandomStringUtils.randomNumeric(5));
        order.setStatus(CommonAttribute.ORDER_STATUS_PENDING_PAYMENT);
        order.setMemberId(currentMember.getId());
        order.save();

        if (byMember) {
            for (CartItem cartItem : cartItems) {
                OrderItem orderItem = new OrderItem();
                orderItem.setCreateDate(new Date());
                orderItem.setModifyDate(new Date());
                orderItem.setName(cartItem.getProduct().getName());
                orderItem.setPrice(cartItem.getPrice());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setSn(cartItem.getProduct().getSn());
                orderItem.setImage(cartItem.getProduct().getProductImages().getMinimum());
                orderItem.setOrderId(order.getId());
                orderItem.setProductId(cartItem.getProductId());
                orderItem.save();
            }
        } else {
            for (CartItem cartItem : cartItems) {
                OrderItem orderItem = new OrderItem();
                orderItem.setCreateDate(new Date());
                orderItem.setModifyDate(new Date());
                orderItem.setName(cartItem.getProduct().getName());
                orderItem.setPrice(cartItem.getMarketPrice());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setSn(cartItem.getProduct().getSn());
                orderItem.setImage(cartItem.getProduct().getProductImages().getMinimum());
                orderItem.setOrderId(order.getId());
                orderItem.setProductId(cartItem.getProductId());
                orderItem.save();
            }
        }


        for (CartItem cartItem : cartItems) {
            new CartItem().dao().deleteById(cartItem.getId());
        }
        redirect("/payment?orderId=" + order.getId());
    }


    /**
     * 购买会员生成订单
     */
    public void ctcMemberSave() {

        Long memberId = getParaToLong("ctcMemberId");

        Long userGid = getParaToLong("userGid");

        if (memberId == null || userGid == null) {
            renderJson(Feedback.error("参数错误"));
            return;
        }

        CtcMember ctcMember = new CtcMember().dao().findById(memberId);

        if (ctcMember == null) {
            renderJson(Feedback.error("会员不存在"));
            return;
        }

        Member currentMember = new Member().dao().findById(userGid);

        if (currentMember == null) {
            renderJson(Feedback.error("用户不存在"));
            return;
        }

        Order order = new Order();

        Date now = new Date();
        order.setCreateDate(now);
        order.setModifyDate(now);

        Integer orderExpireTimes = PropKit.getInt("order.expire.time", 30);

        order.setExpire(DateUtils.addMinutes(now, orderExpireTimes));
        order.setAmount(ctcMember.getPrice());
        order.setTotalPrice(ctcMember.getPrice());
        order.setQuantity(1);

        order.setSn("XYHY_" + DateFormatUtils.format(now, "yyyyMMddHHmmssSSS") + RandomStringUtils.randomNumeric(5));
        order.setStatus(CommonAttribute.ORDER_STATUS_PENDING_PAYMENT);
        order.setMemberId(currentMember.getId());
        order.save();


        OrderItem orderItem = new OrderItem();
        orderItem.setCreateDate(now);
        orderItem.setModifyDate(now);

        orderItem.setName(ctcMember.getTitle());
        orderItem.setPrice(ctcMember.getPrice());
        orderItem.setQuantity(1);
        //orderItem.setSn();
        orderItem.setImage(ctcMember.getImage());
        orderItem.setOrderId(order.getId());
        orderItem.setProductId(ctcMember.getId());
        orderItem.save();

        Config config = SystemUtils.getConfig();
        //
        String orderUrl = new StringBuffer("").append(config.getSiteUrl()).append("/payment?orderId=").append(order.getId()).toString();

        renderJson(Feedback.success(orderUrl));
        //redirect("/payment?orderId=" + order.getId());
    }


}
