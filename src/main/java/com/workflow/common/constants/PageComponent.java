package com.workflow.common.constants;

/**
 * @author : yangqi
 * @email : lukewei@mockuai.com
 * @description :
 * @since : 2021-03-15 22:40
 */
public enum PageComponent {
    /**
     * 文本
     */
    TEXT("文本"),
    /**
     * 文本域
     */
    TEXTAREA("文本域"),
    /**
     * 数字
     */
    NUMBER("数字"),
    /**
     * 日期
     */
    DATE("日期"),
    /**
     * 时间
     */
    TIME("时间"),
    /**
     * 日期时间
     */
    DATETIME("日期时间"),
    /**
     * 布尔
     */
    BOOLEAN("布尔"),
    /**
     * 手机号
     */
    MOBILE("手机号"),
    /**
     * 邮箱
     */
    EMAIL("邮箱");
    private String name;

    private PageComponent(String name) {
        this.name = name;
    }
}