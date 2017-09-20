package com.github.tyang513.kafka.service;

import com.github.tyang513.kafka.model.PipelineDefinition;
import com.github.tyang513.kafka.util.ApplicaitonContextManager;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by yangtao on 2017/9/21.
 */
public class PipelineDefinitionService{

    JdbcTemplate jdbcTemplate = ApplicaitonContextManager.getInstance().getBean(JdbcTemplate.class);

    public PipelineDefinition findPipelineDefinition(Integer id) {
        return jdbcTemplate.queryForObject("select * from TD_MKT_PIPELINE_DEFINITION " +
                " where id = ?", new Object[]{id}, new BeanPropertyRowMapper<PipelineDefinition>(PipelineDefinition.class));
    }


}
