/*
 *
 *
 *
 */
package com.cms.controller.admin;

import com.cms.entity.Admin;
import com.cms.routes.RouteMapping;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Date;


/**
 * Controller - 个人资料
 */
@RouteMapping(url = "/admin/profile")

public class ProfileController extends BaseController {


    /**
     * 验证当前密码
     */
    public void checkCurrentPassword(String currentPassword) {
        if (StringUtils.isEmpty(currentPassword)) {
            renderJson(false);
            return;
        }
        Admin admin = getCurrentAdmin();
        renderJson(StringUtils.equals(DigestUtils.md5Hex(currentPassword), admin.getPassword()));
    }

    /**
     * 编辑
     */
    public void edit() {
        setAttr("admin", getCurrentAdmin());
        render(getView("profile/edit"));
    }

    /**
     * 更新
     */
    public void update() {
        String currentPassword = getPara("currentPassword");
        String password = getPara("password");
        Admin pAdmin = getCurrentAdmin();
        setAttr("admin", pAdmin);

        if (StringUtils.isEmpty(password)) {
            setAttr("msg", "新密码密码不能为空。");
            render(getView("profile/edit"));
            return;
        }

        if (StringUtils.isEmpty(currentPassword) || !StringUtils.equals(DigestUtils.md5Hex(currentPassword), pAdmin.getPassword())) {
            setAttr("msg", "当前密码不正确。");
            render(getView("profile/edit"));
            return;
        }

        pAdmin.setPassword(DigestUtils.md5Hex(password));
        pAdmin.setModifyDate(new Date());
        pAdmin.update();
        setAttr("msg", "修改密码成功。");
        render(getView("profile/edit"));
    }

}