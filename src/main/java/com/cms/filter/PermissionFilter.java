package com.cms.filter;

import com.cms.Config;
import com.cms.entity.Admin;
import com.cms.entity.Member;
import com.cms.util.SystemUtils;
import com.jfinal.kit.PropKit;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PermissionFilter implements Filter {

    /**
     * 不包含
     */
    private List<String> adminExcludes = new ArrayList<String>() {{
        add("/admin/login");
        add("/admin/error");
    }};

    /**
     * 不包含
     */
    private List<String> permissionExcludes = new ArrayList<String>() {{
        add("/admin/logout");
        add("/admin/common");
        add("/admin/file");
    }};

    /**
     * 不包含
     */
    private List<String> memberExcludes = new ArrayList<String>() {{
        add("/register");
        add("/login");
        add("/password");
        add("/payment/alipayNotify");
        add("/payment/weixinNotify");
        add("/payment/index");  // todo
        add("/ctc/member/index");
        add("/order/ctcMemberSave");
    }};

    /**
     * 包含
     */
    private List<String> memberIncludes = new ArrayList<String>() {{
        add("/member");
        add("/member/");
        add("/order/");
        add("/receiver/");
        add("/payment/");
        add("/ctc/member/openMemberView");
    }};

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // TODO Auto-generated method stub

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        // TODO Auto-generated method stub
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;


        // 如果不是80端口,需要将端口加上,如果是集群,则用Nginx的地址,同理不是80端口要加上端口

        String allowDomains = PropKit.get("cors_access.allow_origins");
        String[] allowDomain = allowDomains.split(",");

        String origin = request.getHeader("Origin");

        System.out.println("origin: " + origin);
        
        boolean bAllow = false;

        if (origin == null) {
            //return;  // access
            bAllow = true;
            origin = "*";
        } else {
            for (String tmp : allowDomain) {
                if (StringUtils.isBlank(tmp)) {
                    continue;
                }
                if (equalsAddress(origin, tmp)) {
                    bAllow = true;
                    break;
                }
            }
        }


        if (bAllow) {
            {
                response.setHeader("Access-Control-Allow-Origin", origin);
                response.setContentType("application/json;charset=UTF-8");
                response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
                response.setHeader("Access-Control-Max-Age", "3600");
                response.setHeader("Access-Control-Allow-Headers", "Origin,X-Requested-With,Content-Type,Accept,X-DEV-ID,X-WeshareAuth-Token");
                // 如果要把Cookie发到服务器，需要指定Access-Control-Allow-Credentials字段为true
                response.setHeader("Access-Control-Allow-Credentials", "true");
                response.setHeader("Access-Control-Expose-Headers", "*");
            }


            String url = request.getRequestURI().toString();
            String contextPath = request.getContextPath();
            url = url.substring(contextPath.length());
            if (StringUtils.isBlank(url)
                    || url.equals("/")
                    || url.startsWith("/upload")
                    || url.startsWith("/uploadXiYouMember")
                    || url.startsWith("/static")
                    || url.startsWith("/common")
                    || StringUtils.isNotBlank(FilenameUtils.getExtension(url))
                    ) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
            //匹配member
            for (String key : memberExcludes) {
                if (url.startsWith(key)) {
                    filterChain.doFilter(servletRequest, servletResponse);
                    return;
                }
            }
            for (String key : memberIncludes) {
                if (url.startsWith(key)) {
                    Member currentMember = (Member) request.getSession().getAttribute(Member.SESSION_MEMBER);
                    if (currentMember != null) {
                        filterChain.doFilter(servletRequest, servletResponse);
                        return;
                    }

                    Config config = SystemUtils.getConfig();
                    String weixinAuthorizeReditUrl = PropKit.get("weixinAuthorizeReditUrl");
                    String wxAuthorizeReditUrl = new StringBuffer("").append(config.getSiteUrl()).append(weixinAuthorizeReditUrl).toString();

                    response.sendRedirect(contextPath + "/login/index?weixinAuthorizeReditUrl=" + wxAuthorizeReditUrl);
                    return;

                }
            }
            if (!url.startsWith("/admin")) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
            //匹配admin
            for (String key : adminExcludes) {
                if (url.startsWith(key)) {
                    filterChain.doFilter(servletRequest, servletResponse);
                    return;
                }
            }
            Admin currentAdmin = (Admin) request.getSession().getAttribute(Admin.SESSION_ADMIN);
            if (currentAdmin != null) {
                for (String key : permissionExcludes) {
                    if (url.startsWith(key)) {
                        filterChain.doFilter(servletRequest, servletResponse);
                        return;
                    }
                }
                List<String> permissions = currentAdmin.getPermissions();
                for (String key : permissions) {
                    if (url.startsWith(key)) {
                        filterChain.doFilter(servletRequest, servletResponse);
                        return;
                    }
                }
                response.sendRedirect(contextPath + "/admin/error/unauthorized");
                return;
            }
            response.sendRedirect(contextPath + "/admin/login");
            return;
        }


    }

    public static boolean equalsAddress(String address, String regex) {
        regex = regex.replace(".", "\\.");
        regex = regex.replace("*", "(.*)");
        Pattern pattern = Pattern.compile("^" + regex + "$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(address);
        return matcher.find();
    }
}
