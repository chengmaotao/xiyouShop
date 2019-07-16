package com.cms.controller.front;

import com.cms.CommonAttribute;
import com.cms.entity.SafeKey;
import org.apache.commons.codec.digest.DigestUtils;

import com.cms.Feedback;
import com.cms.entity.Member;
import com.cms.routes.RouteMapping;
import org.apache.commons.lang.StringUtils;

/**
 * Controller - 密码
 * 
 * 
 * 
 */
@RouteMapping(url = "/password")
public class PasswordController extends BaseController{

	/**
	 * 忘记密码
	 */
	public void forgot(){
	    if(isGet()){
	        render("/templates/"+getTheme()+"/"+getDevice()+"/passwordForgot.html");
	    }else{
	        String username = getPara("username");
	        String password = getPara("password");
			String code = getPara("code");
			String rePassword = getPara("rePassword");

			if (StringUtils.isEmpty(username)) {
				Feedback feedback = Feedback.error("请输入手机号码！");
				setAttr("feedback", feedback);
				render("/templates/"+getTheme()+"/"+getDevice()+"/passwordForgot.html");
				return;
			}
			SafeKey codeByMobile = new SafeKey().dao().findCodeByMobile(username, CommonAttribute.MESSAGE_CONFIG_TYPE_FIND_PASSWORD);

			if (StringUtils.isEmpty(code) || codeByMobile == null || !code.equals(codeByMobile.getValue())) {
				Feedback feedback = Feedback.error("验证码不正确！");
				setAttr("feedback", feedback);
				render("/templates/"+getTheme()+"/"+getDevice()+"/passwordForgot.html");
				return;
			}

			if (StringUtils.isEmpty(password)) {
				Feedback feedback = Feedback.error("请输入密码！");
				setAttr("feedback", feedback);
				render("/templates/"+getTheme()+"/"+getDevice()+"/passwordForgot.html");
				return;
			}

			if (!password.equals(rePassword)) {
				Feedback feedback = Feedback.error("两次输入密码不一致，请重新输入！");
				setAttr("feedback", feedback);
				render("/templates/"+getTheme()+"/"+getDevice()+"/passwordForgot.html");
				return;
			}

	        Member member = new Member().dao().findByUsername(username);
	        if(member==null){
	            setAttr("feedback", Feedback.error("用户不存在"));
	            render("/templates/"+getTheme()+"/"+getDevice()+"/passwordForgot.html");
	        }else{
	            member.setPassword(DigestUtils.md5Hex(password));
	            member.update();
	            redirect("/login");
	        }
	    }
	}
}
