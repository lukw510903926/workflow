package com.workflow.config;

import com.workflow.util.io.DiskUploadHelper;
import com.workflow.util.io.UploadHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : yangqi
 * @email : lukewei@mockuai.com
 * @description :
 * @since : 2021-03-14 16:50
 */
@Configuration
public class ApplicationConfig {

    @Bean
    public UploadHelper uploadHelper() {
        return new DiskUploadHelper();
    }
}
