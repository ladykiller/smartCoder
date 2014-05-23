/*
 * Copyright 1997-2014 of PCGroup
 *
 * http://www.pcauto.com.cn/
 *
 */
package com.wheat.code.utils;

import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

/**
 * @author maihaijie
 *
 */
public class MybatisUtils {
	private static final String RESOURCE= "Configuration.xml";
	private static SqlSessionFactory sqlSessionFactory=null;
	static {
		Reader reader;
		try {
			reader = Resources.getResourceAsReader(RESOURCE);
			sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public static SqlSessionFactory getSessionFactory(){
		return sqlSessionFactory;
	}
}
