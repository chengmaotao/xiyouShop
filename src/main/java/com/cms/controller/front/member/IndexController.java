package com.cms.controller.front.member;

import com.cms.controller.front.BaseController;
import com.cms.entity.CtcMember;
import com.cms.entity.CtcUserMember;
import com.cms.entity.Member;
import com.cms.routes.RouteMapping;

import java.util.List;

/**
 * Controller - 首页
 */
@RouteMapping(url = "/member")
public class IndexController extends BaseController {


    public void index() {

        Member currentMember = getCurrentMember();

        List<CtcUserMember> ctcUserMembers = currentMember.getCtcUserMembers();

        if (ctcUserMembers != null && ctcUserMembers.size() > 0) {

            String memberNames = "";
            CtcMember ctcMember = null;
            for (CtcUserMember ctcUserMember : ctcUserMembers) {
                ctcMember = new CtcMember().dao().findById(ctcUserMember.getCtcMemberId());
                memberNames = new StringBuffer(memberNames).append(ctcMember.getTitle()).append(" ").toString();
            }
            setAttr("memberNames", memberNames);
        } else {
            setAttr("memberNames", "普通会员");
        }

        render("/templates/" + getTheme() + "/" + getDevice() + "/member.html");
    }


}
