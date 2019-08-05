package com.cms.entity;

import com.cms.entity.base.BaseCtcMember;
import com.cms.util.DBUtils;
import com.jfinal.plugin.activerecord.Page;
import org.apache.commons.lang.StringUtils;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class CtcMember extends BaseCtcMember<CtcMember> {
	public static final CtcMember dao = new CtcMember().dao();


	public Page<CtcMember> findPage(String title, Integer pageNumber, Integer pageSize){
		String filterSql = "";
		if(StringUtils.isNotBlank(title)){
			filterSql+= " and title like '%"+title+"%'";
		}
		String orderBySql = DBUtils.getOrderBySql("createDate desc");
		return paginate(pageNumber, pageSize, "select *", "from ctc_member where 1=1 "+filterSql+orderBySql);
	}

}