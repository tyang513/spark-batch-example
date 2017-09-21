package com.github.tyang513.kafka.util;

import com.github.tyang513.kafka.model.PipelineDefinition;
import org.springframework.beans.BeansException;
import org.springframework.cache.Cache;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by yangtao on 2017/9/20.
 */
public class ApplicaitonContextManager {

    private static ApplicaitonContextManager instance;

    private ApplicationContext applicationContext;

    private Object lock = new Object();

    private ApplicaitonContextManager() {
        synchronized (lock) {
            if (applicationContext == null) {
                applicationContext = new ClassPathXmlApplicationContext(new String[]{"classpath:spark-batch-example-spring-beans.xml", "classpath:spark-batch-cache-spring-beans.xml"});
            }
        }
    }

    public static ApplicaitonContextManager getInstance() {
        if (instance == null) {
            instance = new ApplicaitonContextManager();
        }
        return instance;
    }

    public synchronized ApplicationContext getApplicationContext() {
        if (applicationContext == null) {
            applicationContext = new ClassPathXmlApplicationContext(new String[]{"classpath:spark-batch-example-spring-beans.xml", "classpath:spark-batch-cache-spring-beans.xml"});
        }
        return applicationContext;
    }

    public <T> T getBean(Class<T> requiredType) throws BeansException {
        return getApplicationContext().getBean(requiredType);
    }

    public Object getBean(String name) throws BeansException {
        return getApplicationContext().getBean(name);
    }

    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return getApplicationContext().getBean(name, requiredType);
    }

    public static void main(String[] args) {
        ApplicationContext applicationContext = ApplicaitonContextManager.getInstance().getApplicationContext();

        JdbcTemplate jdbcTemplate = applicationContext.getBean(JdbcTemplate.class);
        PipelineDefinition pipelineDefinition = jdbcTemplate.queryForObject("select * from TD_MKT_PIPELINE_DEFINITION " +
                " where id = 18 ", new BeanPropertyRowMapper<PipelineDefinition>(PipelineDefinition.class));

        EhCacheCacheManager ehCacheCacheManager = (EhCacheCacheManager) applicationContext.getBean("cacheManager");

        Cache pipelineDefinitionCahce = ehCacheCacheManager.getCache("PipelineDefinitionCache");

        pipelineDefinitionCahce.put("11", pipelineDefinition);

        pipelineDefinitionCahce.get("11").get();
        System.out.println(pipelineDefinition);
        System.out.println(jdbcTemplate.toString());

    }

}
