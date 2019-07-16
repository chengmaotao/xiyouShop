package com.cms.controller.front;

import java.math.BigDecimal;
import java.util.Date;

import com.cms.CommonAttribute;
import com.cms.entity.SafeKey;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import com.cms.Feedback;
import com.cms.entity.Member;
import com.cms.routes.RouteMapping;

/**
 * Controller - 注册
 */
@RouteMapping(url = "/register")
public class RegisterController extends BaseController {

    /**
     * 注册页面
     */
    public void index() {
        render("/templates/" + getTheme() + "/" + getDevice() + "/register.html");
    }


    public void register() {
        String username = getPara("username");
        String password = getPara("password");
        String code = getPara("code");
        String rePassword = getPara("rePassword");

        if (StringUtils.isEmpty(username)) {
            Feedback feedback = Feedback.error("请输入手机号码！");
            setAttr("feedback", feedback);
            render("/templates/" + getTheme() + "/" + getDevice() + "/register.html");
            return;
        }
        SafeKey codeByMobile = new SafeKey().dao().findCodeByMobile(username, CommonAttribute.MESSAGE_CONFIG_TYPE_REGISTER_MEMBER);

        if (StringUtils.isEmpty(code) || codeByMobile == null || !code.equals(codeByMobile.getValue())) {
            Feedback feedback = Feedback.error("验证码不正确！");
            setAttr("feedback", feedback);
            render("/templates/" + getTheme() + "/" + getDevice() + "/register.html");
            return;
        }

        if (StringUtils.isEmpty(password)) {
            Feedback feedback = Feedback.error("请输入密码！");
            setAttr("feedback", feedback);
            render("/templates/" + getTheme() + "/" + getDevice() + "/register.html");
            return;
        }

        if (!password.equals(rePassword)) {
            Feedback feedback = Feedback.error("两次输入密码不一致，请重新输入！");
            setAttr("feedback", feedback);
            render("/templates/" + getTheme() + "/" + getDevice() + "/register.html");
            return;
        }




        Member pMember = new Member().dao().findByUsername(username);
        if (pMember != null) {
            Feedback feedback = Feedback.error("用户已存在");
            setAttr("feedback", feedback);
            render("/templates/" + getTheme() + "/" + getDevice() + "/register.html");
        } else {
            Member member = new Member();
            member.setMobile(username);
            member.setPassword(DigestUtils.md5Hex(password));
            member.setAmount(BigDecimal.ZERO);
            member.setBalance(BigDecimal.ZERO);
            member.setRegisterIp(getRequest().getRemoteAddr());
            member.setCreateDate(new Date());
            member.setModifyDate(new Date());
            member.save();
            redirect("/login");
        }

    }
}
