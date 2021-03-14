package com.workflow.service.dict.impl;

import com.workflow.common.exception.ServiceException;
import com.workflow.common.mybatis.BaseServiceImpl;
import com.workflow.entity.dict.DictType;
import com.workflow.entity.dict.DictValue;
import com.workflow.service.dict.IDictTypeService;
import com.workflow.service.dict.IDictValueService;
import com.workflow.util.WebUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author lukw
 * @email 13507615840@163.com
 * @create 2017-12-26 13:47
 **/
@Service
public class DictValueServiceImplImpl extends BaseServiceImpl<DictValue> implements IDictValueService {

    @Autowired
    private IDictTypeService dictTypeService;

    @Override
    public void saveOrUpdate(DictValue dictValue) {

        if (!this.check(dictValue)) {
            throw new ServiceException("名称/编码不可重复");
        }
        dictValue.setModified(new Date());
        dictValue.setModifier(WebUtil.getLoginUser().getUsername());
        if (dictValue.getId() != null) {
            this.updateNotNull(dictValue);
        } else {
            dictValue.setCreator(WebUtil.getLoginUser().getUsername());
            dictValue.setCreateTime(new Date());
            this.save(dictValue);
        }
    }

    @Override
    public DictValue getById(Integer valueId) {

        Optional<DictValue> dictValue = Optional.ofNullable(this.selectByKey(valueId));
        dictValue.map(value -> {
            if (null != value.getDictTypeId()) {
                DictType dictType = this.dictTypeService.selectByKey(value.getDictTypeId());
                value.setDictType(dictType);
            }
            return value;
        });
        return dictValue.orElse(null);
    }

    private boolean check(DictValue dictValue) {

        DictValue example = new DictValue();
        example.setDictTypeId(dictValue.getDictTypeId());
        example.setName(dictValue.getName());
        List<DictValue> list = this.findByModel(example, false);
        if (this.check(dictValue.getId(), list)) {
            example.setName(null);
            example.setCode(dictValue.getCode());
            list = this.findByModel(example, false);
            return this.check(dictValue.getId(), list);
        }
        return true;
    }
}
