package com.cms.controller.front.ctcmember;

import com.cms.Feedback;
import com.cms.controller.front.BaseController;
import com.cms.entity.CtcMember;
import com.cms.entity.Member;
import com.cms.routes.RouteMapping;
import com.jfinal.kit.PropKit;

import java.util.List;

/**
 * @Auther: CTC
 * @Date: 2019/8/15 17:51
 * @Description:
 */
@RouteMapping(url = "/ctc/member")
public class CtcMemberController extends BaseController {


    /**
     * 会员页面 默认接口
     */
    public void index() {

        List<CtcMember> ctcMembers = new CtcMember().dao().find("select id,price,marketPrice,expireDays,title from ctc_member where type = 99 and isEnabled = 1 order by sort");

        if (ctcMembers == null || ctcMembers.size() == 0) {
            renderJson(Feedback.error("没有售卖的会员"));
        } else {
            renderJson(Feedback.success(ctcMembers));
        }
    }

    /**
     * 我的里面  打开会员页面
     */
    public void openMemberView() {

        String xiyouBaseUrl = PropKit.get("xiyou.member.url");

        Member currentMember = getCurrentMember();

        String resData = new StringBuffer("").append(xiyouBaseUrl).append("?userGid=").append(currentMember.getId()).toString();

        renderJson(Feedback.success(resData));

    }

}
