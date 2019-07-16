package com.cms.response;

/**
 * @Auther: CTC
 * @Date: 2019/7/5 18:06
 * @Description:
 */
public class LightningResponse {
    private int status;

    private String message;

    private Object content;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }
}
