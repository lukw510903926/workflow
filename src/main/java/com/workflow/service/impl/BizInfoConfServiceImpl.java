package com.workflow.service.impl;

import com.workflow.common.constants.Constants;
import com.workflow.common.mybatis.BaseServiceImpl;
import com.workflow.entity.BizInfoConf;
import com.workflow.service.BizInfoConfService;
import com.workflow.util.LoginUser;
import com.workflow.util.WebUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author : yangqi
 * @email : lukewei@mockuai.com
 * @description :
 * @since : 2021/3/15 22:45
 */
@Service
public class BizInfoConfServiceImpl extends BaseServiceImpl<BizInfoConf> implements BizInfoConfService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(BizInfoConf bizInfoConf) {

        BizInfoConf example = new BizInfoConf();
        example.setBizId(bizInfoConf.getBizId());
        example.setTaskAssignee(bizInfoConf.getTaskAssignee());
        example.setTaskId(bizInfoConf.getTaskId());
        this.deleteByModel(example);
        if (bizInfoConf.getId() != null) {
            this.updateAll(bizInfoConf);
        } else {
            this.save(bizInfoConf);
        }
    }

    @Override
    public void deleteByBizId(Long bizId) {

        BizInfoConf bizInfoConf = new BizInfoConf();
        bizInfoConf.setBizId(bizId);
        this.deleteByModel(bizInfoConf);
    }

    @Override
    public BizInfoConf getMyWork(Long bizId) {

        LoginUser loginUser = WebUtil.getLoginUser();
        Example example = new Example(BizInfoConf.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("bizId", bizId);

        Example.Criteria orCriteria = example.createCriteria();
        orCriteria.orEqualTo("taskAssignee", WebUtil.getLoginUsername());
        orCriteria.orIsNull("taskAssignee");
        if (CollectionUtils.isNotEmpty(loginUser.getRoles())) {
            loginUser.getRoles().forEach(role -> orCriteria.orLike("taskAssignee", Constants.BIZ_GROUP + role));
        }
        example.and(orCriteria);
        List<BizInfoConf> list = this.selectByExample(example);
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }
}
