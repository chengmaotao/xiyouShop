package com.cms.entity;

import com.alibaba.fastjson.JSONObject;
import com.cms.entity.base.BaseProduct;
import com.cms.util.DBUtils;
import com.jfinal.kit.StrKit;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Entity - 商品
 */
@SuppressWarnings("serial")
public class CtcProduct extends BaseProduct<CtcProduct> {

    /**
     * 路径
     */
    private static final String PATH = "/product/detail/%d";


    /**
     * 查找商品列表
     *
     * @param productCategoryId 商品分类Id
     * @param first             起始记录
     * @param count             数量
     * @param orderBy           排序
     * @return 商品列表
     */
    public List<CtcProduct> findList(Long productCategoryId, Integer first, Integer count, String orderBy) {
        String filterSql = "";
        if (productCategoryId != null) {
            filterSql += " and productCategoryId=" + productCategoryId;
        }
        String orderBySql = "";
        if (StringUtils.isBlank(orderBy)) {
            orderBySql = DBUtils.getOrderBySql("createDate desc");
        } else {
            orderBySql = DBUtils.getOrderBySql(orderBy);
        }
        String countSql = DBUtils.getCountSql(first, count);
        return find("select * from kf_product where 1=1 " + filterSql + orderBySql + countSql);
    }


    public ProductImage getProductImages() {
        if (StrKit.notBlank(getProductImage())) {
            //return JSONArray.parseArray(getProductImage(),ProductImage.class);
            return JSONObject.parseObject(getProductImage(), ProductImage.class);
        }
        return null;
    }

    /**
     * 获取路径
     *
     * @return 路径
     */
    public String getPath() {
        return String.format(CtcProduct.PATH, getId());
    }
}
