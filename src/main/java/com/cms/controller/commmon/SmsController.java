package com.cms.controller.commmon;

import java.util.Date;

import com.cms.entity.Member;
import com.cms.response.LightningResponse;
import com.cms.util.XiYouUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.cms.CommonAttribute;
import com.cms.entity.SafeKey;
import com.cms.routes.RouteMapping;
import com.cms.util.SmsUtils;
import com.jfinal.core.Controller;

@RouteMapping(url = "/common/sms")
public class SmsController extends Controller{

    
    public void send(){
        String mobile = getPara("mobile");
        String type = getPara("type");
        String code = RandomStringUtils.randomNumeric(4);
        if(CommonAttribute.messageConfigTypeNames.keySet().contains(type)){

            Member pMember = new Member().dao().findByUsername(mobile);

            boolean isExist = (pMember == null);

            switch (type) {
                case CommonAttribute.MESSAGE_CONFIG_TYPE_TEST_MESSAGE:
                    //SmsUtils.sendTestSms(mobile);
                    break;
                case CommonAttribute.MESSAGE_CONFIG_TYPE_REGISTER_MEMBER:

                    // 用户已存在
                    if(!isExist){
                        LightningResponse errorResponse = XiYouUtils.getErrorResponse(1000, "用户已存在");
                        renderJson(errorResponse);
                        return;
                    }

                    SmsUtils.sendRegisterMemberSms(code,mobile);
                    break;
                case CommonAttribute.MESSAGE_CONFIG_TYPE_FIND_PASSWORD:

                    // 用户不存在
                    if(isExist){
                        LightningResponse errorResponse =XiYouUtils.getErrorResponse(1000,"用户不存在");
                        renderJson(errorResponse);
                        return;
                    }
                    SmsUtils.sendFindPasswordSms(code,mobile);
                    break;
            }

            SafeKey safeKey = new SafeKey();
            safeKey.setExpire(DateUtils.addMinutes(new Date(), 10));
            safeKey.setMobile(mobile);
            safeKey.setCreateDate(new Date());
            safeKey.setModifyDate(new Date());
            safeKey.setValue(code);
            safeKey.setType(type);
            safeKey.save();
        }
        LightningResponse sucessResponse = XiYouUtils.getRightResponse("sucess", null);
        renderJson(sucessResponse);
    }
    
}
