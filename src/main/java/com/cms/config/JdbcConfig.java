package com.cms.config;

import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordException;
import com.jfinal.plugin.druid.DruidPlugin;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @Auther: CTC
 * @Date: 2019/7/9 20:36
 * @Description:
 */
public class JdbcConfig {

    private static ThreadLocal<Connection> threadLocal = null;

    private static DataSource getDataSource() {
        PropKit.use("config.properties");
        DruidPlugin druidPlugin = new DruidPlugin(PropKit.get("jdbc.url"), PropKit.get("jdbc.username"), PropKit.get("jdbc.password"), PropKit.get("jdbc.driver"));
        druidPlugin.start();
        return druidPlugin.getDataSource();
    }


    public static Connection getConnection() {

        Connection connection = null;
        try {
            if (threadLocal == null) {
                threadLocal = new ThreadLocal<Connection>();
                threadLocal.set(getDataSource().getConnection());
            }
            connection = threadLocal.get();
            if (connection == null) {
                connection = getDataSource().getConnection();
                threadLocal.set(connection);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return connection;

    }

    public static void close(Connection conn) {
        if (threadLocal.get() == null && conn != null) {
            try {
                conn.close();
            } catch (SQLException var3) {
                throw new ActiveRecordException(var3);
            }
        }

    }


}
