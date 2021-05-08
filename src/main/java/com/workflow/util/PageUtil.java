package com.workflow.util;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.workflow.vo.BaseVo;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 *
 * @author yangqi
 * @Description </p>
 * @email 13507615840@163.com
 * @since 2019/2/22 22:55
 **/
public class PageUtil {

    public static PageInfo<BaseVo> getPage(BaseVo baseVo) {

        PageInfo<BaseVo> pageInfo = new PageInfo<>();
        Integer pageNum = Optional.ofNullable(baseVo).map(BaseVo::getPageNum).orElse(1);
        Integer pageSize = Optional.ofNullable(baseVo).map(BaseVo::getPageNum).orElse(20);
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        int startRow = pageNum > 0 ? (pageNum - 1) * pageSize : 0;
        int endRow = startRow + pageSize * (pageNum > 0 ? 1 : 0);
        pageInfo.setStartRow(startRow);
        pageInfo.setEndRow(endRow);
        return pageInfo;

    }

    public static void startPage(BaseVo baseVo) {

        if (baseVo.getPageNum() != null && baseVo.getPageSize() != null) {
            PageHelper.startPage(baseVo.getPageNum(), baseVo.getPageSize());
        }
    }

    public static void startPage(PageInfo<?> pageInfo) {

        if (pageInfo != null && pageInfo.getPageNum() > 0 && pageInfo.getPageSize() > 0) {
            PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        }
    }

    public static <T> PageInfo<T> getResult(List<T> list, long totalCount) {

        PageInfo<T> pageInfo = new PageInfo<>();
        pageInfo.setTotal(totalCount);
        pageInfo.setList(list);
        return pageInfo;
    }
}
