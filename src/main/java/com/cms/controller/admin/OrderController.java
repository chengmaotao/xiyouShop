package com.cms.controller.admin;

import com.cms.CommonAttribute;
import com.cms.config.JdbcConfig;
import com.cms.entity.Order;
import com.cms.entity.OrderItem;
import com.cms.routes.RouteMapping;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;


/**
 * Controller - 订单
 */
@RouteMapping(url = "/admin/order")

public class OrderController extends BaseController {

    /**
     * 查看
     */
    public void view() {
        Long id = getParaToLong("id");
        setAttr("order", new Order().dao().findById(id));
        render(getView("order/view"));
    }


    /**
     * 发货
     */
    public void shipping() {
        Long id = getParaToLong("id");
        Order order = new Order().dao().findById(id);
        order.setStatus(CommonAttribute.ORDER_STATUS_SHIPPED);
        order.setModifyDate(new Date());
        order.update();
        redirect("/admin/order/view?id=" + id);
    }

    /**
     * 完成
     */
    public void complete() {
        Long id = getParaToLong("id");
        Connection connection = JdbcConfig.getConnection();
        String orderSql = "update kf_order set modifyDate = now(),completeDate = now(), status = ? where status != ? and id=?";
        PreparedStatement pst = null;
        try {
            pst = connection.prepareStatement(orderSql);
            pst.setString(1, CommonAttribute.ORDER_STATUS_COMPLETED);
            pst.setString(2, CommonAttribute.ORDER_STATUS_COMPLETED);
            pst.setLong(3, id);
            int i = pst.executeUpdate();
            if (i > 0) {
                // 修改产品销量
                Order order = new Order().dao().findById(id);
                List<OrderItem> orderItems = order.getOrderItems();

                if (orderItems != null && orderItems.size() > 0) {
                    String productSql = "update kf_product set sales = sales + ? where id = ?";
                    for (OrderItem orderItem : orderItems) {
                        pst = connection.prepareStatement(productSql);
                        pst.setInt(1, orderItem.getQuantity());
                        pst.setLong(2, orderItem.getProductId());
                        pst.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        JdbcConfig.close(connection);

        redirect("/admin/order/view?id=" + id);
    }


    /**
     * 列表
     */
    public void list() {
        String sn = getPara("sn");
        Integer pageNumber = getParaToInt("pageNumber");
        if (pageNumber == null) {
            pageNumber = 1;
        }
        setAttr("page", new Order().dao().findPage(sn, pageNumber, PAGE_SIZE));
        setAttr("sn", sn);
        render(getView("order/list"));
    }
}
