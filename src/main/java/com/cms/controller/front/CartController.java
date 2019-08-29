package com.cms.controller.front;

import com.cms.entity.Cart;
import com.cms.entity.CartItem;
import com.cms.entity.Member;
import com.cms.entity.Product;
import com.cms.routes.RouteMapping;
import com.cms.util.XiYouUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller - 购物车
 */
@RouteMapping(url = "/cart")
public class CartController extends BaseController {


    /**
     * 列表
     */
    public void list() {
        Cart currentCart = getCurrentCart();
        setAttr("currentCart", currentCart);


        Member currentMember = getCurrentMember();
        if(currentMember == null || currentMember.getId() == null){
            setAttr("isLogin", false);
        }else{
            setAttr("isLogin", true);
        }

        //  西柚会员  TODO
        boolean member = XiYouUtils.getByMember(currentMember);

        if(!member){
            BigDecimal subtract = currentCart.getMarketTotalPrice().subtract(currentCart.getTotalPrice());
            setAttr("jies", subtract.stripTrailingZeros().toPlainString());
        }

        setAttr("isMember", member);

        render("/templates/" + getTheme() + "/" + getDevice() + "/cartList.html");
    }


    /**
     * 查看
     */
    public void view() {

        Long productId = getParaToLong(0);

        Cart currentCart = getCurrentCart();
        Map<String, Object> data = new HashMap<>();
        data.put("quantity", currentCart.getQuantity());
        data.put("totalPrice", currentCart.getTotalPrice());
        List<CartItem> cartItems = currentCart.getCartItems();
        List<Map<String, Object>> cartItemDatas = new ArrayList<>();
        for (CartItem item : cartItems) {
            Map<String, Object> cartItemData = new HashMap<>();
            cartItemData.put("quantity", item.getQuantity());
            cartItemData.put("id", item.getId());
            Product product = item.getProduct();
            Map<String, Object> productData = new HashMap<>();
            productData.put("path", product.getPath());
            productData.put("name", product.getName());
            productData.put("image", product.getProductImages().getMinimum());
            productData.put("price", product.getPrice());
            productData.put("productId", product.getId());
            cartItemData.put("product", productData);

            if (productId != null && productId == product.getId()) {
                data.put("hasNum", true);
                data.put("cartProductId", productId);
                data.put("cartProductQuantity", item.getQuantity());
            }

            cartItemDatas.add(cartItemData);
        }
        data.put("cartItems", cartItemDatas);
        renderJson(data);
    }

}
