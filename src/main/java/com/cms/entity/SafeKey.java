package com.cms.entity;

import com.cms.entity.base.BaseSafeKey;

/**
 * Entity - 密钥
 * 
 * 
 * 
 */
@SuppressWarnings("serial")
public class SafeKey extends BaseSafeKey<SafeKey> {
    
   /**
     * 根据手机号码查找秘钥
     * 
     * @param mobile
     *          手机号码
     * @param type
     *          类型
     * @param siteId
     *          站点id   
     * @return
     */

    public SafeKey findCodeByMobile(String mobile,String type){
        return findFirst("select * from kf_safe_key where mobile=? and type=? and expire > now() order By id desc limit 1",mobile,type);
    }
}
