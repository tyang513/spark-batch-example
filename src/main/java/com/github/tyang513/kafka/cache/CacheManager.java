
package com.github.tyang513.kafka.cache;

public class CacheManager extends net.sf.ehcache.CacheManager {
	
	public CacheManager() {
		super(CacheManager.class.getClassLoader().getResource("ehcache.xml"));
	}
}
