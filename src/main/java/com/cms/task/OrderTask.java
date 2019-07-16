package com.cms.task;

import com.cms.CommonAttribute;
import com.cms.config.JdbcConfig;
import com.cms.entity.Order;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: CTC
 * @Date: 2019/7/9 19:12
 * @Description:
 */
public class OrderTask implements ServletContextListener {

    public void contextInitialized(ServletContextEvent event) {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3);
        scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                List<Order> orders = new Order().dao().find("select * from kf_order where status = '" + CommonAttribute.ORDER_STATUS_PENDING_PAYMENT + "' and expire < now()");

                if (orders != null && orders.size() > 0) {
                    Connection connection = JdbcConfig.getConnection();
                    String sql = "update kf_order set modifyDate = now(), status = ? where status = ? and id=?";
                    for (Order order : orders) {
                        try {
                            PreparedStatement pst = connection.prepareStatement(sql);
                            pst.setString(1, CommonAttribute.ORDER_STATUS_CANCELED);
                            pst.setString(2, CommonAttribute.ORDER_STATUS_PENDING_PAYMENT);
                            pst.setLong(3, order.getId());
                            pst.executeUpdate();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    JdbcConfig.close(connection);
                }
            }
        }, 1, 300, TimeUnit.SECONDS);
    }
}
