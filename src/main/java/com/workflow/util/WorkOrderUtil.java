package com.workflow.util;

import java.util.Date;


/**
 * Title: workflowWeb <br>
 * Description: <br>
 * Copyright: eastcom Copyright (C) 2009 <br>
 *
 * @author <a href="mailto:liyoujun@eastcom-sw.com">李友均</a><br>
 * @version 1.0 <br>
 * @e-mail: liyoujun@eastcom-sw.com <br>
 * @creatdate 2015年5月8日 下午3:23:18 <br>
 */
public class WorkOrderUtil {
    /**
     * 根据类型转换
     *
     * @param value
     * @param type
     * @return
     */
    public static Object convObject(String value, String type) {
        if (value == null) {
            return null;
        }
        if (PageComponent.NUMBER.toString().equalsIgnoreCase(type)) {
            if (value.indexOf(".") == -1) {
                return Integer.parseInt(value);
            } else {
                return Double.parseDouble(value);
            }
        } else if (PageComponent.DATE.toString().equalsIgnoreCase(type) || PageComponent.TIME.toString().equalsIgnoreCase(type) | PageComponent.DATETIME.toString().equalsIgnoreCase(type)) {
            //暂时只处理日期时间
            return DateUtils.parseDate(value);
        } else if (PageComponent.BOOLEAN.toString().equalsIgnoreCase(type)) {
            //布尔类型界面传入
            return "true".equalsIgnoreCase(value) || "1".equals(value);
        }
        return value;
    }

    public static enum PageComponent {
        TEXT("文本"), TEXTAREA("文本域"), NUMBER("数字"), DATE("日期"), TIME("时间"), DATETIME("日期时间"), BOOLEAN("布尔"), MOBILE("手机号"), EMAIL("邮箱");
        private String name;

        private PageComponent(String name) {
            this.name = name;
        }
    }

    /**
     * 生成工单号<br>
     * 根据工单类型转换成拼音首字母-yyMMdd-xxxxx
     *
     * @return
     */
    public static String builWorkNumber(String workType) {
        String workNumber = null;
        if ("circuitDispatch".equals(workType)) {
            workNumber = "DLDD";
        } else if ("common_order_flow".equals(workType)) {
            workNumber = "TYGZ";
        } else if ("complaintHandle".equals(workType)) {
            workNumber = "TSCL";
        } else if ("emergencyHandle".equals(workType)) {
            workNumber = "JJGZCL";
        } else if ("faultHandle".equals(workType)) {
            workNumber = "GZCL";
        } else if (workType.startsWith("ITSuperMarket") || workType.startsWith("itSuperMarketRecyced")) {
            workNumber = "ITShop";
        } else if (workType.startsWith("changeManagement")) {
            workNumber = "UNMP-C";
        } else if (workType.startsWith("eventManagement")) {
            workNumber = "UNMP-S";
        } else if (workType.startsWith("faultManagement")) {
            workNumber = "UNMP-F";
        } else if (workType.startsWith("maintainManagement")) {
            workNumber = "UNMP-M";
        } else if (workType.startsWith("nonFunctionalAcceptanceManagement")) {
            workNumber = "UNMP-M";
        } else if (workType.startsWith("problemManagement")) {
            workNumber = "UNMP-P";
        } else {
            workNumber = "OTHER";
        }
        workNumber = workNumber + DateUtils.formatDate(new Date(), "yyMMdd");
        workNumber = workNumber + "-" + Math.round(Math.random() * 89999 + 10000);
        return workNumber;
    }
}
