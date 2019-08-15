package com.cms.controller.front.ctcmember;

import com.cms.Feedback;
import com.cms.controller.front.BaseController;
import com.cms.entity.CtcMember;
import com.cms.routes.RouteMapping;

import java.util.List;

/**
 * @Auther: CTC
 * @Date: 2019/8/15 17:51
 * @Description:
 */
@RouteMapping(url = "/ctc/member")
public class CtcMemberController extends BaseController {

    public void index() {

        List<CtcMember> ctcMembers = new CtcMember().dao().find("select * from ctc_member where type = 99 order by sort");

        if (ctcMembers == null || ctcMembers.size() == 0) {
            renderJson(Feedback.error("没有售卖的会员"));
        } else {
            renderJson(Feedback.success(ctcMembers));
        }
    }
}
