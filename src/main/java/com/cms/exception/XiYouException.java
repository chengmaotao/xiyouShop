package com.cms.exception;

/**
 * @Auther: CTC
 * @Date: 2019/7/5 10:33
 * @Description:
 */
public class XiYouException extends RuntimeException {

    private String message;

    public XiYouException(){

    }

    public XiYouException(String message){
        this.message = message;
    }
}
