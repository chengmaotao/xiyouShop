package com.cms.controller.admin;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cms.Feedback;
import com.cms.entity.*;
import com.cms.routes.RouteMapping;
import com.cms.util.ProductImageUtils;
import com.cms.util.SystemUtils;
import com.jfinal.upload.UploadFile;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.time.DateFormatUtils;

import java.util.*;

/**
 * Controller - 商品
 */
@RouteMapping(url = "/admin/product")

public class ProductController extends BaseController {

    /**
     * 添加
     */
    public void add() {
        setAttr("productCategoryTree", new ProductCategory().dao().findTree());
        setAttr("config", SystemUtils.getConfig());
        render(getView("product/add"));
    }

    /**
     * 保存
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void save() {

        List<UploadFile> uploadFiles = getFiles();
        Product product = getModel(Product.class, "", true);

        ProductImage productImage = new ProductImage();
        if (uploadFiles != null && uploadFiles.size() > 0) {
            UploadFile uploadFile = uploadFiles.get(0);
            productImage.setFile(uploadFile);
            ProductImageUtils.generate(productImage);
            product.setProductImage(JSONObject.toJSONString(productImage));
            product.setImage(productImage.getSource());
        }

        product.setParameterValue("[]");
        product.setSn(DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS") + RandomStringUtils.randomNumeric(5));
        product.setCreateDate(new Date());
        product.setModifyDate(new Date());
        product.setHits(0l);
        product.setSales(0l);
        product.save();
        redirect(getListQuery("/admin/product/list"));
    }

    /**
     * 编辑
     */
    public void edit() {


        List<ProductCategory> tree = new ProductCategory().dao().findTree();
        setAttr("productCategoryTree", tree);
        Product product = new Product().dao().findById(getParaToLong("id"));
        setAttr("product", product);

        render(getView("product/edit"));
    }

    /**
     * 更新
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void update() {
        List<UploadFile> uploadFiles = getFiles();
        Product product = getModel(Product.class, "", true);
        ProductImage productImage = new ProductImage();
        if (uploadFiles != null && uploadFiles.size() > 0) {
            UploadFile uploadFile = uploadFiles.get(0);
            productImage.setFile(uploadFile);
            ProductImageUtils.generate(productImage);
            product.setProductImage(JSONObject.toJSONString(productImage));
            product.setImage(productImage.getSource());
        }
        product.setModifyDate(new Date());
        product.update();
        redirect(getListQuery("/admin/product/list"));
    }

    /**
     * 列表
     */
    public void list() {
        Integer pageNumber = getParaToInt("pageNumber");
        if (pageNumber == null) {
            pageNumber = 1;
        }
        Long productCategoryId = getParaToLong("productCategoryId");
        setAttr("page", new Product().dao().findPage(pageNumber, PAGE_SIZE, productCategoryId));
        render(getView("product/list"));
    }

    /**
     * 删除
     */
    public void delete() {
        Long ids[] = getParaValuesToLong("ids");
        for (Long id : ids) {
            new Product().dao().deleteById(id);
        }
        renderJson(Feedback.success(new HashMap<>()));
    }

    /**
     * 获取参数
     */
    public void parameters() {
        Long productCategoryId = getParaToLong("productCategoryId");
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        ProductCategory productCategory = new ProductCategory().dao().findById(productCategoryId);
        List<Parameter> parameters = productCategory.getParameters();
        if (productCategory == null || parameters == null || parameters.size() == 0) {
            renderJson(data);
        }
        for (Parameter parameter : parameters) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("subGroup", parameter.getSubgroup());
            item.put("names", parameter.getNames());
            data.add(item);
        }
        renderJson(data);
    }

    private void filterParameterValue(List<ParameterValue> parameterValues) {
        CollectionUtils.filter(parameterValues, new Predicate() {
            public boolean evaluate(Object object) {
                ParameterValue parameterValue = (ParameterValue) object;
                if (parameterValue == null || StringUtils.isEmpty(parameterValue.getGroup())) {
                    return false;
                }
                CollectionUtils.filter(parameterValue.getEntries(), new Predicate() {
                    private Set<String> set = new HashSet<String>();

                    public boolean evaluate(Object object) {
                        ParameterValue.Entry entry = (ParameterValue.Entry) object;
                        return entry != null && StringUtils.isNotEmpty(entry.getName())
                                && StringUtils.isNotEmpty(entry.getValue()) && set.add(entry.getName());
                    }
                });
                return CollectionUtils.isNotEmpty(parameterValue.getEntries());
            }
        });
    }

    private void filterProductImage(List<ProductImage> productImages) {
        CollectionUtils.filter(productImages, new Predicate() {
            public boolean evaluate(Object object) {
                ProductImage productImage = (ProductImage) object;
                return productImage != null && !productImage.isEmpty();
            }
        });
    }
}
