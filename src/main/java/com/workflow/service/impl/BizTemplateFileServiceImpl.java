package com.workflow.service.impl;

import com.workflow.common.exception.ServiceException;
import com.workflow.common.mybatis.BaseServiceImpl;
import com.workflow.entity.BizInfo;
import com.workflow.entity.BizTemplateFile;
import com.workflow.service.BizTemplateFileService;
import com.workflow.service.IBizInfoService;
import com.workflow.util.WebUtil;
import com.workflow.util.io.UploadHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author : yangqi
 * @email : lukewei@mockuai.com
 * @description :
 * @since : 2021/3/15 22:45
 */
@Slf4j
@Service
public class BizTemplateFileServiceImpl extends BaseServiceImpl<BizTemplateFile> implements BizTemplateFileService {

    @Resource
    private IBizInfoService bizInfoService;

    @Resource
    private Environment environment;

    @Resource
    private UploadHelper uploadHelper;

    @Override
    public BizTemplateFile getBizTemplateFile(BizTemplateFile templateFile) {

        Long bizId = templateFile.getBizId();
        if (bizId != null) {
            BizInfo bizInfo = this.bizInfoService.selectByKey(bizId);
            String bizType = Optional.ofNullable(bizInfo).map(BizInfo::getBizType).orElse(null);
            templateFile.setFlowName(bizType);
        }
        templateFile.setId(templateFile.getId());
        return this.selectOne(templateFile);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(BizTemplateFile bizTemplateFile, MultipartFile file) {

        String fileName = file.getOriginalFilename();
        bizTemplateFile.setFileName(fileName);
        if (!this.check(bizTemplateFile)) {
            throw new ServiceException(" 相同名称模版已存在,请将原模板文件删除后再上传,所属流程+文件名唯一");
        }
        try (InputStream inputStream = file.getInputStream()) {
            String upload = this.uploadHelper.upload(inputStream, environment.getProperty("biz.file.path"), fileName);
            bizTemplateFile.setFilePath(upload);
            if (bizTemplateFile.getId() == null) {
                bizTemplateFile.setCreateUser(WebUtil.getLoginUserId());
                bizTemplateFile.setFullName(WebUtil.getLoginUser().getName());
                bizTemplateFile.setCreateTime(new Date());
                this.save(bizTemplateFile);
            } else {
                this.updateNotNull(bizTemplateFile);
            }
        } catch (IOException e) {
            log.error("模板保存失败 :", e);
            throw new ServiceException("模板保存失败!");
        }
    }

    private boolean check(BizTemplateFile dataFile) {

        BizTemplateFile file = new BizTemplateFile();
        file.setFileName(dataFile.getFileName());
        file.setFlowName(dataFile.getFlowName());
        List<BizTemplateFile> list = this.select(file);
        return this.check(dataFile.getId(), list);
    }
}
