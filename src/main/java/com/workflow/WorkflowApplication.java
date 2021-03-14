package com.workflow;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author : yangqi
 * @email : lukewei@mockuai.com
 * @description :
 * @since : 2021-03-13 22:51
 */
@Slf4j
@EnableCaching
@SpringBootApplication//(exclude = {ProcessEngineAutoConfiguration.class})
@MapperScan(basePackages = "com.workflow.dao")
@ServletComponentScan("com.workflow.config")
public class WorkflowApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {

        SpringApplication.run(WorkflowApplication.class, args);
        log.info("workflow application start successfully----------");
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return super.configure(builder);
    }
}
