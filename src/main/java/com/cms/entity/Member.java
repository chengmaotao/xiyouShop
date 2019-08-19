package com.cms.entity;

import com.cms.entity.base.BaseMember;
import com.cms.util.DBUtils;
import com.jfinal.plugin.activerecord.Page;
import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * Entity -会员
 */
@SuppressWarnings("serial")
public class Member extends BaseMember<Member> {

    /**
     * 会员session名称
     */
    public static final String SESSION_MEMBER = "session_member";


    private List<CtcUserMember> ctcUserMembers;

    /**
     * 查找会员分页
     *
     * @return 会员分页
     */
    public Page<Member> findPage(String mobile, Integer pageNumber, Integer pageSize) {
        String filterSql = "";
        if (StringUtils.isNotBlank(mobile)) {
            filterSql += " and mobile like '%" + mobile + "%'";
        }
        String orderBySql = DBUtils.getOrderBySql("createDate desc");
        return paginate(pageNumber, pageSize, "select *", "from kf_member where 1=1 " + filterSql + orderBySql);
    }

    /**
     * 根据用户名查找会员
     *
     * @param username 用户名
     * @return 会员
     */
    public Member findByUsername(String username) {
        return findFirst("select * from kf_member where mobile=? ", username);
    }


    public List<CtcUserMember> getCtcUserMembers() {

        Long userId = getId();
        if (userId == null) {
            return null;
        }

        List<CtcUserMember> ctcUserMembers = new CtcUserMember().find("select distinct userId,ctcMemberId from ctc_user_member where userId = ? and expireDayte >= " +
                        "now() ",
                userId);

        return ctcUserMembers;
    }


}
