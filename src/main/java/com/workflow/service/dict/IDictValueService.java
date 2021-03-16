package com.workflow.service.dict;


import com.workflow.common.mybatis.IBaseService;
import com.workflow.entity.dict.DictValue;

/**
 * @author : yangqi
 * @email : lukewei@mockuai.com
 * @description :
 * @since : 2021/3/16 23:02
 */
public interface IDictValueService extends IBaseService<DictValue> {

    /**
     * @param valueId
     * @return
     */
    DictValue getById(Integer valueId);
}
