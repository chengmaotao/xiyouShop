package com.cms.controller.front;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayConstants;
import com.alipay.api.internal.util.AlipaySignature;
import com.cms.CommonAttribute;
import com.cms.Config;
import com.cms.Feedback;
import com.cms.entity.Order;
import com.cms.entity.Payment;
import com.cms.routes.RouteMapping;
import com.cms.service.CtcUserMemeberService;
import com.cms.util.AlipayUtils;
import com.cms.util.SystemUtils;
import com.cms.util.WeixinUtils;
import com.jfinal.kit.HttpKit;
import com.jfinal.log.Log;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Controller - 收款单
 */
@RouteMapping(url = "/payment")
public class PaymentController extends BaseController {


    // private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    private static Log logger = Log.getLog(PaymentController.class);
    ;

    /**
     * 页面
     */
    public void index() {
        Long orderId = getParaToLong("orderId");
        Order order = new Order().dao().findById(orderId);
        setAttr("order", order);
        render("/templates/" + getTheme() + "/" + getDevice() + "/payment.html");
    }

    public void payByWxBrowser() {
        Long orderId = getParaToLong("orderId");
        String paymentMethod = getPara("paymentMethod");
        Order order = new Order().dao().findById(orderId);
        if (StringUtils.isNotBlank(paymentMethod) && order != null && CommonAttribute.ORDER_STATUS_PENDING_PAYMENT.equals(order.getStatus())) {
            switch (paymentMethod) {
                case CommonAttribute.PAYMENT_METHOD_ALIPAY:
                    String wapWxPayBody = AlipayUtils.wapWxPay(order.getSn(), order.getTotalPrice(), "订单支付", getRequest(), getResponse());
                    renderJson(Feedback.success(wapWxPayBody));
                    break;
                case CommonAttribute.PAYMENT_METHOD_WEIXIN:
                    Map<String, String> result = WeixinUtils.wechatPay(order.getSn(), order.getTotalPrice(), "订单支付", order.getMember().getWeixinOpenId(), getRequest(), getResponse());
                    //renderJson(result);
                    renderJson(Feedback.success(result));
                    break;
            }
        } else if (order != null) {
            logger.info("payByWxBrowser()已经支付成功过了 直接进入订单详情页面" + order.getId());
            renderJson(new Feedback("paymented", null, null));
            return;
        } else {
            logger.info("payByWxBrowser() 微信浏览器支付宝支付 订单不存在 ");
            redirect("/");
        }
    }


    /**
     * 非微信浏览器 调取微信h5支付
     */
    public void wechatH5Pay() {
        Long orderId = getParaToLong("orderId");
        String paymentMethod = getPara("paymentMethod");
        Order order = new Order().dao().findById(orderId);
        if (StringUtils.isNotBlank(paymentMethod) && order != null && CommonAttribute.ORDER_STATUS_PENDING_PAYMENT.equals(order.getStatus())) {
            Map<String, String> result = WeixinUtils.h5Pay(order.getId(), order.getSn(), order.getTotalPrice(), "订单支付", getRequest());
            logger.info("wechatH5Pay:" + result);
            Feedback success = Feedback.success(result);
            logger.info("returnHtml:" + success);
            renderJson(success);
        } else if (order != null) {
            logger.info("wechatH5Pay() 已经支付成功过了 直接进入订单详情页面" + order.getId());
            renderJson(new Feedback("paymented", null, null));
            return;
        } else {
            logger.info("wechatH5Pay() 支付 订单不存在 ");
            redirect("/");
        }
    }

    /**
     * 支付
     */
    public void pay() throws IOException {
        Long orderId = getParaToLong("orderId");
        String paymentMethod = getPara("paymentMethod");
        Order order = new Order().dao().findById(orderId);
        if (StringUtils.isNotBlank(paymentMethod) && order != null && CommonAttribute.ORDER_STATUS_PENDING_PAYMENT.equals(order.getStatus())) {

            switch (paymentMethod) {
                case CommonAttribute.PAYMENT_METHOD_ALIPAY:
                    AlipayUtils.wapPay(order.getSn(), order.getTotalPrice(), "订单支付", getRequest(), getResponse());
                    break;
                case CommonAttribute.PAYMENT_METHOD_WEIXIN:
                    Map<String, String> result = WeixinUtils.h5Pay(order.getId(), order.getSn(), order.getTotalPrice(), "订单支付", getRequest());
                    String mweb_url = result.get("mweb_url");
                    getResponse().sendRedirect(mweb_url);
                    break;
            }

        } else if (order != null) {
            logger.info("pay() 已经支付成功过了 直接进入订单详情页面" + order.getId());
            redirect("/member/order/detail/" + order.getId());
        } else {
            logger.info("pay() 支付 订单不存在 ");
            redirect("/");
        }
    }

    /**
     * 支付宝同步
     */
    public void alipayReturn() {
        render("/templates/" + getTheme() + "/" + getDevice() + "/index.html");
    }

    /**
     * 支付宝异步
     */
    public void alipayNotify() {
        //获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map requestParams = getRequest().getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        // System.out.println("==alipayNotify===" + params);
        //切记alipaypublickey是支付宝的公钥，请去open.alipay.com对应应用下查看。
        //boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
        try {
            Config config = SystemUtils.getConfig();
            boolean flag = AlipaySignature.rsaCheckV1(params, config.getAlipayPublicKey(), AlipayConstants.CHARSET_UTF8, AlipayConstants.SIGN_TYPE_RSA2);
            if (flag) {
                //插入流水
                String trade_status = params.get("trade_status");
                String out_trade_no = params.get("out_trade_no");
                String passback_params = params.get("passback_params");
                String orderSn = "";
                try {
                    orderSn = URLDecoder.decode(passback_params, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if ("TRADE_SUCCESS".equals(trade_status) || "TRADE_FINISHED".equals(trade_status)) {
                    //支付流水添加
                    BigDecimal total_amount = new BigDecimal(params.get("total_amount"));
                    //Order order = new Order().dao().findById(orderId);
                    Order order = new Order().dao().findFirst("select * from kf_order where sn = ?", orderSn);

                    // 支付宝返回的金额 和 订单金额不一致
                    if (total_amount.compareTo(order.getTotalPrice()) != 0) {
                        logger.warn("支付宝回调：orderSn = {" + orderSn + "} 订单金额{" + order.getTotalPrice() + "} 和 回调金额{" + total_amount + "}不一致");
                        renderJson("error");
                        return;
                    }

                    Payment payment = new Payment();
                    payment.setCreateDate(new Date());
                    payment.setModifyDate(new Date());
                    payment.setMemberId(order.getMemberId());
                    payment.setOrderId(order.getId());
                    payment.setAmount(total_amount);
                    payment.setSn(out_trade_no);
                    payment.setMethod(CommonAttribute.PAYMENT_METHOD_ALIPAY);
                    payment.save();
                    //订单状态修改

                    // 状态是 待支付 才修改为 修改为 已经支付
                    if (StringUtils.equals(order.getStatus(), CommonAttribute.ORDER_STATUS_PENDING_PAYMENT)) {
                        order.setStatus(CommonAttribute.ORDER_STATUS_PENDING_SHIPMENT);
                        // order.update();
                    } else {
                        logger.warn("支付宝回调：order = {" + order + "},支付宝内部唯一标识号：out_trade_no={" + out_trade_no + "} 不是待支付状态");
                        return;
                    }
                    //返回

                    // 购买的会员
                    if (order.getSn().startsWith("XYHY_")) {
                        order.setStatus(CommonAttribute.ORDER_STATUS_COMPLETED);
                        CtcUserMemeberService ctcUserMemeberService = new CtcUserMemeberService();

                        ctcUserMemeberService.updateUserMember(order);
                    }
                    order.update();
                    renderJson("success");
                }
            }
        } catch (AlipayApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 微信异步
     */
    public void weixinNotify() {
        System.out.println("微信回调guolaile……");
        String data = HttpKit.readData(getRequest());
        Map<String, String> weixinResult = new HashMap<String, String>();
        try {
            System.out.println("==weixinpayNotify=data=" + data);
            Map<String, String> result = WeixinUtils.xmlToMap(data);
            System.out.println("==weixinpayNotify=result=" + result);
            Config config = SystemUtils.getConfig();
            //判断验签
            if (WeixinUtils.isSignatureValid(data, config.getWeixinpayKey())) {
                String out_trade_no = result.get("out_trade_no");
                String result_code = result.get("result_code");
                String orderId = result.get("attach");


                if ("SUCCESS".equals(result_code)) {
                    //支付流水添加
                    Integer total_fee = Integer.valueOf(result.get("total_fee"));

                    BigDecimal total_amount = new BigDecimal(total_fee).divide(new BigDecimal(100));

                    Order order = new Order().dao().findFirst("select * from kf_order where sn = ?", orderId);

                    // 支付宝返回的金额 和 订单金额不一致
                    if (total_amount.compareTo(order.getTotalPrice()) != 0) {
                        logger.warn("微信回调：orderSn = {" + orderId + "} 订单金额{" + order.getTotalPrice() + "} 和 回调金额{" + total_amount + "}不一致");
                        renderJson("error");
                        return;
                    }


                    Payment payment = new Payment();
                    payment.setCreateDate(new Date());
                    payment.setModifyDate(new Date());
                    payment.setMemberId(order.getMemberId());
                    payment.setOrderId(order.getId());
                    payment.setAmount(total_amount);
                    payment.setSn(out_trade_no);
                    payment.setMethod(CommonAttribute.PAYMENT_METHOD_WEIXIN);
                    payment.save();

                    // 状态是 待支付 才修改为 修改为 已经支付
                    if (StringUtils.equals(order.getStatus(), CommonAttribute.ORDER_STATUS_PENDING_PAYMENT)) {
                        order.setStatus(CommonAttribute.ORDER_STATUS_PENDING_SHIPMENT);
                        // order.update();
                    } else {
                        logger.warn("微信回调：order = {" + order + "},微信内部唯一标识号：out_trade_no={" + out_trade_no + "} 不是待支付状态");
                        return;
                    }

                    // 购买的会员
                    if (order.getSn().startsWith("XYHY_")) {
                        order.setStatus(CommonAttribute.ORDER_STATUS_COMPLETED);
                        CtcUserMemeberService ctcUserMemeberService = new CtcUserMemeberService();
                        ctcUserMemeberService.updateUserMember(order);
                    }
                    order.update();

                    //返回
                    weixinResult.put("return_code", "SUCCESS");
                    weixinResult.put("return_msg", "OK");
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            renderText(WeixinUtils.mapToXml(weixinResult));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}
