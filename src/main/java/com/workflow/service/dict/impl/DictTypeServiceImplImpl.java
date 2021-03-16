package com.workflow.service.dict.impl;

import com.workflow.common.exception.ServiceException;
import com.workflow.common.mybatis.BaseServiceImpl;
import com.workflow.entity.dict.DictType;
import com.workflow.entity.dict.DictValue;
import com.workflow.service.dict.IDictTypeService;
import com.workflow.service.dict.IDictValueService;
import com.workflow.util.WebUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author lukw
 * @email 13507615840@163.com
 * @create 2017-12-26 13:46
 **/
@Service
public class DictTypeServiceImplImpl extends BaseServiceImpl<DictType> implements IDictTypeService {

    @Resource
    private IDictValueService dictValueService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(DictType dictType) {

        if (!this.check(dictType)) {
            throw new ServiceException("字典名称不可重复");
        }
        dictType.setModified(new Date());
        dictType.setModifier(WebUtil.getLoginUserId());
        if (null != dictType.getId()) {
            this.updateNotNull(dictType);
        } else {
            dictType.setCreator(WebUtil.getLoginUserId());
            dictType.setCreateTime(new Date());
            this.save(dictType);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {

        if (null != id) {
            this.deleteById(id);
            DictValue dictValue = new DictValue();
            dictValue.setDictTypeId(id);
            this.dictValueService.deleteByModel(dictValue);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> list) {

        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(this::delete);
        }
    }

    private boolean check(DictType dictType) {

        DictType example = new DictType();
        example.setName(dictType.getName());
        List<DictType> list = this.findByModel(example, false);
        return this.check(dictType.getId(), list);
    }
}
