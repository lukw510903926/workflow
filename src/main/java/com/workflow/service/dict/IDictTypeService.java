package com.workflow.service.dict;


import com.workflow.common.mybatis.IBaseService;
import com.workflow.entity.dict.DictType;

import java.util.List;

/**
 * @author : yangqi
 * @email : lukewei@mockuai.com
 * @description :
 * @since : 2021/3/16 23:02
 */
public interface IDictTypeService extends IBaseService<DictType> {

    /**
     * @param id
     */
    void delete(Long id);

    /**
     * @param list
     */
    void delete(List<Long> list);
}
