package com.github.tyang513.kafka.util;

import com.github.tyang513.kafka.model.PipelineDefinition;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by yangtao on 2017/9/20.
 */
public class ApplicaitonContextManager {

    private static final ApplicaitonContextManager instance = new ApplicaitonContextManager();

    private ApplicationContext applicationContext;

    private Object lock = new Object();

    private ApplicaitonContextManager() {
        synchronized (lock) {
            if (applicationContext == null) {
                applicationContext = new ClassPathXmlApplicationContext("classpath:marketing-spring-beans.xml");
            }
        }
    }

    public static ApplicaitonContextManager getInstance() {
        return instance;
    }

    public synchronized ApplicationContext getApplicationContext() {
        if (applicationContext == null) {
            applicationContext = new ClassPathXmlApplicationContext("classpath:marketing-spring-beans.xml");
        }
        return applicationContext;
    }

    public <T> T getBean(Class<T> requiredType) throws BeansException {
        return getApplicationContext().getBean(requiredType);
    }

    public static void main(String[] args) {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:marketing-spring-beans.xml");

        JdbcTemplate jdbcTemplate = applicationContext.getBean(JdbcTemplate.class);
        PipelineDefinition pipelineDefinition = jdbcTemplate.queryForObject("select * from TD_MKT_PIPELINE_DEFINITION " +
                " where id = 18 ", new BeanPropertyRowMapper<PipelineDefinition>(PipelineDefinition.class));
        System.out.println(pipelineDefinition);
        System.out.println(jdbcTemplate.toString());

    }

}
