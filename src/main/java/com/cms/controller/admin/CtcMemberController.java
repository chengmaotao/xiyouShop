/*
 *
 *
 *
 */
package com.cms.controller.admin;

import com.cms.CommonAttribute;
import com.cms.Feedback;
import com.cms.entity.CtcMember;
import com.cms.routes.RouteMapping;
import org.apache.commons.lang.ArrayUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Controller - 西柚会员
 */
@RouteMapping(url = "/admin/ctc/member")

public class CtcMemberController extends BaseController {


    /**
     * 添加
     */
    public void add() {

        Map ctcMemeberTypeNames = CommonAttribute.ctcMemeberTypeNames;

        setAttr("ctcMemberTypeNames", ctcMemeberTypeNames);
        render(getView("ctc/member/add"));
    }

    /**
     * 保存
     */
    public void save() {
        CtcMember ctcMember = getModel(CtcMember.class, "", true);
        if (ctcMember.getIsEnabled() == null) {
            ctcMember.setIsEnabled(false);
        }
        ctcMember.setCreateDate(new Date());
        ctcMember.setModifyDate(new Date());
        ctcMember.save();
        redirect(getListQuery("/admin/ctc/member/list"));
    }

    /**
     * 编辑
     */
    public void edit() {
        Long id = getParaToLong("id");
        setAttr("ctcMemberTypeNames", CommonAttribute.ctcMemeberTypeNames);
        setAttr("ctcMember", new CtcMember().dao().findById(id));
        render(getView("ctc/member/edit"));
    }

    /**
     * 更新
     */
    public void update() {
        CtcMember ctcMember = getModel(CtcMember.class, "", true);
        if (ctcMember.getIsEnabled() == null) {
            ctcMember.setIsEnabled(false);
        }
        ctcMember.setModifyDate(new Date());
        ctcMember.update();
        redirect(getListQuery("/admin/ctc/member/list"));
    }

    /**
     * 列表
     */
    public void list() {
        String title = getPara("title");
        Integer pageNumber = getParaToInt("pageNumber");
        if (pageNumber == null) {
            pageNumber = 1;
        }

        setAttr("page", new CtcMember().dao().findPage(title, pageNumber, PAGE_SIZE));
        setAttr("ctcMemberTypeNames", CommonAttribute.ctcMemeberTypeNames);
        setAttr("title", title);
        render(getView("ctc/member/list"));
    }

    /**
     * 删除
     */
    public void delete() {
        Long ids[] = getParaValuesToLong("ids");
        if (ArrayUtils.isNotEmpty(ids)) {
            for (Long id : ids) {
                new CtcMember().dao().deleteById(id);
            }
        }
        renderJson(Feedback.success(new HashMap<>()));
    }

}