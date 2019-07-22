package com.cms.util;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.domain.ExtendParams;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConstants;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.cms.Config;
import sun.misc.BASE64Encoder;

public class AlipayUtils {

    public static void webPay(String orderSn,BigDecimal amount,String subject,HttpServletRequest httpRequest,HttpServletResponse httpResponse){
        try{
            Config config = SystemUtils.getConfig();
            AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", config.getAlipayAppId(), config.getAlipayAppPrivateKey(), AlipayConstants.FORMAT_JSON, AlipayConstants.CHARSET_UTF8, config.getAlipayPublicKey(), AlipayConstants.SIGN_TYPE_RSA2); //获得初始化的AlipayClient
            AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();//创建API对应的request
            alipayRequest.setReturnUrl(config.getSiteUrl()+"/payment/alipayReturn");
            alipayRequest.setNotifyUrl(config.getSiteUrl()+"/payment/alipayNotify");//在公共参数中设置回跳和通知地址
            alipayRequest.setBizContent("{" +
                    "    \"out_trade_no\":\""+DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS")+RandomStringUtils.randomNumeric(4)+"\"," +
                    "    \"product_code\":\"FAST_INSTANT_TRADE_PAY\"," +
                    "    \"total_amount\":"+amount+"," +
                    "    \"subject\":\""+subject+"\"," +
                    "    \"body\":\""+subject+"\"," +
                    "    \"passback_params\":\""+URLEncoder.encode(orderSn,AlipayConstants.CHARSET_UTF8)+"\"," +    //URLEncoder.encode(orderId,"UTF-8")
                    "    \"extend_params\":{" +
                    "    \"sys_service_provider_id\":\""+config.getAlipayAppId()+"\"" +
                    "    }"+
                    "  }");//填充业务参数
            String form="";
            try {
                form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
            } catch (AlipayApiException e) {
                e.printStackTrace();
            }
            httpResponse.setContentType("text/html;charset=" + AlipayConstants.CHARSET_UTF8);
            httpResponse.getWriter().write(form);//直接将完整的表单html输出到页面
            httpResponse.getWriter().flush();
            httpResponse.getWriter().close();
        }catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public static void wapPay(String orderSn, BigDecimal amount, String subject, HttpServletRequest request, HttpServletResponse response) {
        try {
        Config config = SystemUtils.getConfig();
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", config.getAlipayAppId(), config.getAlipayAppPrivateKey(), AlipayConstants.FORMAT_JSON, AlipayConstants.CHARSET_UTF8, config.getAlipayPublicKey(), AlipayConstants.SIGN_TYPE_RSA2); //获得初始化的AlipayClient

        AlipayTradeWapPayRequest alipay_request=new AlipayTradeWapPayRequest();

        AlipayTradeWapPayModel model=new AlipayTradeWapPayModel();
        model.setOutTradeNo(DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS")+RandomStringUtils.randomNumeric(4));
        model.setProductCode("QUICK_WAP_WAY");
        model.setTotalAmount(amount.stripTrailingZeros().toPlainString());
        model.setSubject(subject);
        model.setBody(subject);
        model.setPassbackParams(URLEncoder.encode(orderSn,AlipayConstants.CHARSET_UTF8));
        //model.setTimeoutExpress(timeout_express);
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId(config.getAlipayAppId());
        model.setExtendParams(extendParams);
        alipay_request.setBizModel(model);
        // 设置异步通知地址
        alipay_request.setNotifyUrl(config.getSiteUrl()+"/payment/alipayNotify");
        // 设置同步地址
        alipay_request.setReturnUrl(config.getSiteUrl()+"/payment/alipayReturn");

        // form表单生产

            // 调用SDK生成表单
            String form = alipayClient.pageExecute(alipay_request).getBody(); //调用SDK生成表单
            response.setContentType("text/html;charset=" + AlipayConstants.CHARSET_UTF8);
            response.getWriter().write(form);//直接将完整的表单html输出到页面
            response.getWriter().flush();
            response.getWriter().close();
        } catch (AlipayApiException e) {

            e.printStackTrace();
        }catch (Exception ex){

            ex.printStackTrace();
        }

    }

    public static String wapWxPay(String orderSn, BigDecimal amount, String subject, HttpServletRequest request, HttpServletResponse response) {

        try {
            Config config = SystemUtils.getConfig();
            AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", config.getAlipayAppId(), config.getAlipayAppPrivateKey(), AlipayConstants.FORMAT_JSON, AlipayConstants.CHARSET_UTF8, config.getAlipayPublicKey(), AlipayConstants.SIGN_TYPE_RSA2); //获得初始化的AlipayClient

            AlipayTradeWapPayRequest alipay_request=new AlipayTradeWapPayRequest();

            AlipayTradeWapPayModel model=new AlipayTradeWapPayModel();
            model.setOutTradeNo(DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS")+RandomStringUtils.randomNumeric(4));
            model.setProductCode("QUICK_WAP_WAY");
            model.setTotalAmount(amount.stripTrailingZeros().toPlainString());
            model.setSubject(subject);
            model.setBody(subject);
            model.setPassbackParams(URLEncoder.encode(orderSn,AlipayConstants.CHARSET_UTF8));
            //model.setTimeoutExpress(timeout_express);
            ExtendParams extendParams = new ExtendParams();
            extendParams.setSysServiceProviderId(config.getAlipayAppId());
            model.setExtendParams(extendParams);
            alipay_request.setBizModel(model);
            // 设置异步通知地址
            alipay_request.setNotifyUrl(config.getSiteUrl()+"/payment/alipayNotify");
            // 设置同步地址
            alipay_request.setReturnUrl(config.getSiteUrl()+"/payment/alipayReturn");

            // form表单生产

            // 调用SDK生成表单
            String form = alipayClient.pageExecute(alipay_request).getBody(); //调用SDK生成表单
            byte[] bytes = form.getBytes("UTF-8");
            BASE64Encoder encoder = new BASE64Encoder();
            String res = encoder.encode(bytes);
            return  res;
        } catch (AlipayApiException e) {

            e.printStackTrace();
        }catch (Exception ex){

            ex.printStackTrace();
        }
        return null;

    }
}
