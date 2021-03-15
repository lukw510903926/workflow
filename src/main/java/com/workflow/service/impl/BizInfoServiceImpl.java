package com.workflow.service.impl;

import com.github.pagehelper.PageInfo;
import com.workflow.common.constants.Constants;
import com.workflow.common.mybatis.BaseServiceImpl;
import com.workflow.entity.BizFile;
import com.workflow.entity.BizInfo;
import com.workflow.entity.BizInfoConf;
import com.workflow.entity.ProcessVariableInstance;
import com.workflow.entity.auth.SystemUser;
import com.workflow.service.BizInfoConfService;
import com.workflow.service.IBizFileService;
import com.workflow.service.IBizInfoService;
import com.workflow.service.IVariableInstanceService;
import com.workflow.service.auth.ISystemUserService;
import com.workflow.util.PageUtil;
import com.workflow.util.WebUtil;
import com.workflow.vo.BaseVo;
import com.workflow.vo.BizInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author : yangqi
 * @email : lukewei@mockuai.com
 * @description :
 * @since : 2021/3/15 22:49
 */
@Slf4j
@Service
public class BizInfoServiceImpl extends BaseServiceImpl<BizInfo> implements IBizInfoService {

    @Autowired
    private BizInfoConfService bizInfoConfService;

    @Autowired
    private IVariableInstanceService variableInstanceService;

    @Autowired
    private IBizFileService bizFileService;

    @Autowired
    private ISystemUserService userService;

    @Override
    public List<BizInfo> getBizByParentId(Long parentId) {

        BizInfo bizInfo = new BizInfo();
        bizInfo.setParentId(parentId);
        return this.select(bizInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BizInfo copyBizInfo(Long bizId, String processInstanceId, Map<String, Object> variables) {

        BizInfo oldBiz = this.selectByKey(bizId);
        List<BizInfo> list = this.getBizByParentId(bizId);
        BizInfo newBiz = oldBiz.clone();
        newBiz.setId(null);
        if (CollectionUtils.isEmpty(list)) {
            newBiz.setWorkNum(newBiz.getWorkNum() + "-00" + 1);
        } else {
            newBiz.setWorkNum(newBiz.getWorkNum() + "-00" + (list.size() + 1));
        }
        newBiz.setProcessInstanceId(processInstanceId);
        newBiz.setParentId(bizId);
        newBiz.setCreateTime(new Date());
        newBiz.setStatus(Constants.BIZ_NEW);
        newBiz.setCreateUser(WebUtil.getLoginUsername());
        this.saveOrUpdate(newBiz);
        this.copyProcessVarInstance(processInstanceId, oldBiz, newBiz);
        this.copyBizInfoConf(newBiz, oldBiz.getId());
        this.copyBizFile(oldBiz, newBiz);
        return newBiz;
    }

    private void copyProcessVarInstance(String processInstanceId, BizInfo oldBiz, BizInfo newBiz) {

        ProcessVariableInstance instance = new ProcessVariableInstance();
        instance.setBizId(oldBiz.getId());
        instance.setTaskId(Constants.TASK_START);
        List<ProcessVariableInstance> processInstances = variableInstanceService.select(instance);
        if (CollectionUtils.isNotEmpty(processInstances)) {
            processInstances.forEach(oldInstance -> {
                ProcessVariableInstance processVariableInstance = oldInstance.clone();
                processVariableInstance.setId(null);
                processVariableInstance.setBizId(newBiz.getId());
                processVariableInstance.setProcessInstanceId(processInstanceId);
                processVariableInstance.setCreateTime(new Date());
                variableInstanceService.save(processVariableInstance);
            });
        }
    }

    private void copyBizFile(BizInfo oldBiz, BizInfo newBiz) {

        BizFile entity = new BizFile();
        entity.setBizId(oldBiz.getId());
        entity.setTaskId(oldBiz.getTaskId());
        List<BizFile> files = bizFileService.select(entity);
        if (CollectionUtils.isNotEmpty(files)) {
            files.forEach(oldFile -> {
                BizFile bizFile = oldFile.clone();
                bizFile.setId(null);
                bizFile.setBizId(newBiz.getId());
                bizFile.setCreateDate(new Date());
                bizFile.setCreateUser(WebUtil.getLoginUsername());
                bizFileService.save(bizFile);
            });
        }
    }

    private void copyBizInfoConf(BizInfo newBiz, Long bizId) {

        BizInfoConf example = new BizInfoConf();
        example.setBizId(bizId);
        List<BizInfoConf> list = this.bizInfoConfService.select(example);
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(bizConf -> {
                BizInfoConf newConf = bizConf.clone();
                newConf.setId(null);
                newConf.setBizId(newBiz.getId());
                newConf.setCreateTime(new Date());
                bizInfoConfService.save(newConf);
            });
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByIds(List<Serializable> list) {

        list.stream().filter(Objects::nonNull).forEach(id -> {
            BizInfo bizInfo = new BizInfo();
            bizInfo.setId(Long.valueOf(id.toString()));
            bizInfo.setStatus(Constants.BIZ_DELETE);
            this.updateNotNull(bizInfo);
        });
    }

    @Override
    public PageInfo<BizInfo> findBizInfo(BizInfoVo bizInfoVo, PageInfo<BaseVo> page) {

        log.info("工单查询 bizInfoVo : {}", bizInfoVo);
        PageUtil.startPage(page);
        BizInfo bizInfo = new BizInfo();
        BeanUtils.copyProperties(bizInfoVo, bizInfo);
        List<BizInfo> list = this.select(bizInfo);
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(entity -> {
                String createUser = Optional.ofNullable(this.userService.getUserByUsername(entity.getCreateUser())).map(SystemUser::getName).orElse(entity.getCreateUser());
                entity.setCreateUser(createUser);
                if (StringUtils.isNotBlank(entity.getTaskAssignee())) {
                    String taskAssignee = Optional.ofNullable(this.userService.getUserByUsername(entity.getTaskAssignee())).map(SystemUser::getName).orElse(entity.getTaskAssignee());
                    entity.setTaskAssignee(taskAssignee);
                }
            });
        }
        return PageInfo.of(list);
    }
}
