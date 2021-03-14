package com.workflow.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <br>
 * Title: ipman_topic<br>
 * Description: <br>
 * Copyright: eastcom_sw Copyright (C) 2013 <br>
 *
 * @author <a href="mailto:邮箱">hujian</a><br>
 * @version 3.0 <br>
 * @e-mail: hujian@eastcom-sw.com<br>
 * @creatdate 2013-11-18 下午4:46:39 <br>
 */
public class SSOUtil {

    public static final String CACHE_SESSIONINFO = "sessionInfo";

    private static Logger logger = LoggerFactory.getLogger("SSOUtil");

    public static LoginUser getInstance(HttpServletRequest request, HttpServletResponse response) {

        LoginUser user = (LoginUser) request.getSession().getAttribute(WebUtil.LOGIN_USER);
        if (user == null) {
            try {
                request.getSession().invalidate();
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("error ", e);
            }
        }
        return user;
    }

    public static LoginUser getUser(HttpServletRequest request, HttpServletResponse response) {
        LoginUser user = (LoginUser) request.getSession().getAttribute(WebUtil.LOGIN_USER);
        return user;
    }

    public static String getUserName(HttpServletRequest request, HttpServletResponse response) {
        LoginUser user = (LoginUser) request.getSession().getAttribute(WebUtil.LOGIN_USER);
        String userName = user == null ? null : user.getUsername();
        return userName;
    }

    // ============== SSO Cache ==============
    private static Map<String, Object> cacheMap;

    public static Object getCache(String key) {
        return getCache(key, null);
    }

    public static Object getCache(String key, Object defaultValue) {
        Object obj = getCacheMap().get(key);
        return obj == null ? defaultValue : obj;
    }

    public static void putCache(String key, Object value) {
        getCacheMap().put(key, value);
    }

    public static void removeCache(String key) {
        getCacheMap().remove(key);
    }

    public static Map<String, Object> getCacheMap() {
        if (cacheMap == null) {
            cacheMap = new HashMap<String, Object>();
        }
        return cacheMap;
    }

    public static String list2ConvertToString(List<String> bussinessList2, String bussinessCol,
                                              List<String> regionList2, String regionCol) {
        return list2ConvertToStringBuffer(bussinessList2, bussinessCol, regionList2, regionCol).toString();
    }

    public static StringBuffer list2ConvertToStringBuffer(List<String> bussinessList2, String bussinessCol,
                                                          List<String> regionList2, String regionCol) {
        StringBuffer buffer = new StringBuffer(" (");
        Map<String, List<String>> bMap = constructMapByList2(bussinessList2);
        Map<String, List<String>> rMap = constructMapByList2(regionList2);
        for (String role : bMap.keySet()) {
            buffer.append("( " + bussinessCol).append(constructSqlIn(bMap.get(role))).append(" and ")
                    .append(regionCol).append(constructSqlIn(rMap.get(role))).append(") or ");
        }
        int len = buffer.length();
        return buffer.delete(len - 4, len).append(")");
    }

    private static Map<String, List<String>> constructMapByList2(List<String> list) {
        String[] array;
        List<String> l;
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        for (String str : list) {
            array = str.split(":");
            if (!map.containsKey(array[0])) {
                l = new ArrayList<String>();
                map.put(array[0], l);
            } else {
                l = map.get(array[0]);
            }
            l.add(array[1]);
        }
        return map;
    }

    private static StringBuffer constructSqlIn(List<String> list) {
        StringBuffer buffer = new StringBuffer();
        for (String str : list) {
            buffer.append(",'").append(str).append("'");
        }
        return buffer.deleteCharAt(0).insert(0, " in (").append(")");
    }

}