package com.cms.util;

import com.cms.entity.CtcUserMember;
import com.cms.entity.Member;
import com.cms.response.LightningResponse;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import java.util.List;

/**
 * @Auther: CTC
 * @Date: 2019/7/5 18:18
 * @Description:
 */
public class XiYouUtils {

    /**
     * CacheManager
     */
    private static final CacheManager CACHE_MANAGER = CacheUtils.getCacheManager();

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

    public static boolean getByMember(Member member) {

        if(member == null){
            return false;
        }

        Ehcache cache = CACHE_MANAGER.getEhcache("ctc_member");

        String cacheKey = "user_" + member.getId();
        Element cacheElement = cache.get(cacheKey);

        if (cacheElement == null) {

            List<CtcUserMember> ctcUserMembers = member.getCtcUserMembers();
            if (ctcUserMembers == null || ctcUserMembers.size() == 0) {
                return false;
            } else {
                cache.put(new Element(cacheKey, member));
                return true;
            }
        } else {
            return true;
        }
    }

    public static void cleanCacheByMember(Long userGid) {
        Ehcache cache = CACHE_MANAGER.getEhcache("ctc_member");

        String cacheKey = "user_" + userGid;

        cache.remove(cacheKey);

    }
}
