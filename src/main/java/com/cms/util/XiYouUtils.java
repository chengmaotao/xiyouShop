package com.cms.util;

import com.cms.response.LightningResponse;

/**
 * @Auther: CTC
 * @Date: 2019/7/5 18:18
 * @Description:
 */
public class XiYouUtils {
    public static LightningResponse getRightResponse(String msg, Object content) {
        LightningResponse res = new LightningResponse();
        res.setStatus(0);
        res.setMessage(msg);
        res.setContent(content);
        return res;
    }

    public static LightningResponse getErrorResponse(int status, String msg) {
        LightningResponse res = new LightningResponse();
        res.setStatus(status);
        res.setMessage(msg);
        return res;
    }
}
