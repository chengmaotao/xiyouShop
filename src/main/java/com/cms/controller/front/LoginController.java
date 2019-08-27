package com.cms.controller.front;

import com.cms.Config;
import com.cms.Feedback;
import com.cms.config.JdbcConfig;
import com.cms.entity.Member;
import com.cms.routes.RouteMapping;
import com.cms.util.SystemUtils;
import com.cms.util.WeixinUtils;
import com.jfinal.kit.PropKit;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;

/**
 * Controller - 登录
 */
@RouteMapping(url = "/login")
public class LoginController extends BaseController {

    /**
     * 登录页面
     */
    public void index() {
        Config config = SystemUtils.getConfig();
        String appId = config.getWeixinpayAppId();
        setAttr("appId", appId);

        setAttr("weixinAuthorizeReditUrl", getPara("weixinAuthorizeReditUrl"));

        render("/templates/" + getTheme() + "/" + getDevice() + "/login.html");
    }

    public void login() {
        Config config = SystemUtils.getConfig();
        String username = getPara("username");
        String password = getPara("password");
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
            Member member = new Member().dao().findByUsername(username);
            if (member == null) {
                setAttr("feedback", Feedback.error("用户不存在"));
            } else if (!DigestUtils.md5Hex(password).equals(member.getPassword())) {
                setAttr("feedback", Feedback.error("用户名密码错误"));
            } else {
                getSession().setAttribute(Member.SESSION_MEMBER, member);

                // 是在公众号里打开  进行微信授权
                String weixin = getPara("wxId");
                if (StringUtils.equals(weixin, "weixin") && StringUtils.isEmpty(member.getWeixinOpenId())) {
                    //  微信code
                    String code = getPara("code");
                    String weixinAppSecret = PropKit.get("weixinAppSecret");
                    Map<String, Object> wxOpenId = WeixinUtils.getOauth2Token(config.getWeixinpayAppId(), weixinAppSecret, code);

                    // openId
                    String openid = (String) wxOpenId.get("openid");

                    if (StringUtils.isNotEmpty(openid)) {
                        // 将OpenId更新到用户表
                        Connection connection = JdbcConfig.getConnection();
                        String orderSql = "update kf_member set modifyDate = now(),weixinOpenId = ? where id = ?";
                        PreparedStatement pst = null;

                        try {
                            pst = connection.prepareStatement(orderSql);
                            pst.setString(1, openid);
                            pst.setLong(2, member.getId());
                            pst.executeUpdate();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }

                redirect("/");
                return;
            }
        }
        render("/templates/" + getTheme() + "/" + getDevice() + "/login.html");
    }
}
