package com.workflow.service.dict;


import com.workflow.common.mybatis.IBaseService;
import com.workflow.entity.dict.DictType;

import java.util.List;

/**
 * @author lukw
 * @email 13507615840@163.com
 * @create 2017-12-26 13:46
 **/
public interface IDictTypeService extends IBaseService<DictType> {

    void delete(Long id);

    void delete(List<Long> list);
}
