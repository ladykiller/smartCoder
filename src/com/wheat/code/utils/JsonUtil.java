package com.wheat.code.utils;

import org.apache.commons.logging.LogFactory;

public class JsonUtil {
	
    static final org.apache.commons.logging.Log LOG = LogFactory.getLog(JsonUtil.class.getSimpleName());

	public static String toJson(Object obj){
    	return org.nutz.json.Json.toJson(obj);
    }
	
    
	public static <T>T fromJson(Class<T> t,String content){
		return org.nutz.json.Json.fromJson(t,content);
	}
	
}

