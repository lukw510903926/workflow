package com.workflow.service.dict;


import com.workflow.common.mybatis.IBaseService;
import com.workflow.entity.dict.DictValue;

/**
 * @author lukw
 * @email 13507615840@163.com
 * @create 2017-12-26 13:47
 **/
public interface IDictValueService extends IBaseService<DictValue> {

    DictValue getById(Integer valueId);
}
