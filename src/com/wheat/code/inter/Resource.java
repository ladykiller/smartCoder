/*
 * Copyright 1997-2014 of PCGroup
 *
 * http://www.pcauto.com.cn/
 *
 */
package com.wheat.code.inter;

/**
 * @author maihaijie
 *
 */
public interface Resource {
    /**
     * 一个JSON字符串还原成一个Resource对象
     * @param json JSON字符串
     */
	public void fromJson(String json);

        /**
         * 获取对象的JSON字符串
         * @return JSON字符串
         */
	public String getJson();

        /**
         * 返回对象的唯一标识
         * @return 以字符串表示的唯一标识
         */
	public String getKey();

        /**
         * 资源编码，把一个对象转换为JSON字符串
         * @return JSON字符串
         */
	public String encode();

        /**
         * 资源解码，从一个JSON数据还原成一个Resource对象
         * @param content JSON内容
         * @return Resource对象
         */
	public Resource decode(String content);

        /**
         *  获取资源名
         * @return
         */
	public String getResourceName();
}
