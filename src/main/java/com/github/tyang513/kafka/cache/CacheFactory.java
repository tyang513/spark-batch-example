package com.github.tyang513.kafka.cache;

import com.github.tyang513.kafka.util.ApplicaitonContextManager;
import net.sf.ehcache.Cache;

import java.util.HashMap;

public class CacheFactory {



    private static HashMap<String, Cache> mappingCache = ApplicaitonContextManager.getInstance().getBean("mappingCache", HashMap.class);

    public static HashMap<String, Cache> getMappingCache() {
        if (mappingCache == null){
            mappingCache = ApplicaitonContextManager.getInstance().getBean("mappingCache", HashMap.class);
        }
        return mappingCache;
    }

    public static void setMappingCache(HashMap<String, Cache> mappingCache) {
        CacheFactory.mappingCache = mappingCache;
    }

    /**
     * @return
     */
    public static Cache getProjectCache() {
        return getMappingCache().get("ProjectCache");
    }
}
