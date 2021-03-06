package com.cms.entity.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseArticle<M extends BaseArticle<M>> extends Model<M> implements IBean {

	public M setId(java.lang.Long id) {
		set("id", id);
		return (M)this;
	}
	
	public java.lang.Long getId() {
		return getLong("id");
	}

	public M setCreateDate(java.util.Date createDate) {
		set("createDate", createDate);
		return (M)this;
	}
	
	public java.util.Date getCreateDate() {
		return get("createDate");
	}

	public M setModifyDate(java.util.Date modifyDate) {
		set("modifyDate", modifyDate);
		return (M)this;
	}
	
	public java.util.Date getModifyDate() {
		return get("modifyDate");
	}

	public M setContent(java.lang.String content) {
		set("content", content);
		return (M)this;
	}
	
	public java.lang.String getContent() {
		return getStr("content");
	}

	public M setTitle(java.lang.String title) {
		set("title", title);
		return (M)this;
	}
	
	public java.lang.String getTitle() {
		return getStr("title");
	}

	public M setHits(java.lang.Long hits) {
		set("hits", hits);
		return (M)this;
	}
	
	public java.lang.Long getHits() {
		return getLong("hits");
	}

	public M setArticleCategoryId(java.lang.Long articleCategoryId) {
		set("articleCategoryId", articleCategoryId);
		return (M)this;
	}
	
	public java.lang.Long getArticleCategoryId() {
		return getLong("articleCategoryId");
	}

	public M setAuthor(java.lang.String author) {
		set("author", author);
		return (M)this;
	}
	
	public java.lang.String getAuthor() {
		return getStr("author");
	}

	public M setIsPublication(java.lang.Boolean isPublication) {
		set("isPublication", isPublication);
		return (M)this;
	}
	
	public java.lang.Boolean getIsPublication() {
		return get("isPublication");
	}

}
