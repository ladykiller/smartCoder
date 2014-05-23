/*
 * Copyright 1997-2014 of PCGroup
 *
 * http://www.pcauto.com.cn/
 *
 */
package com.wheat.code.generator;

import java.io.IOException;

import com.wheat.code.constant.Env;
import com.wheat.code.utils.CommonUtils;

/**
 * @author maihaijie
 * 
 */
public class BaseGenerator {

	/**
	 * 执行这个方法生成所有包路径
	 * 
	 * @param argc
	 */
	public static void main(String[] argc) {
		try {
			String[] paths = { Env.CONTORLER_PATH, Env.SERVICE_PATH,
					Env.POJO_PATH, Env.DAO_PATH, Env.CONSTANT_PATH,
					Env.GENERATOR_PATH, Env.UTILS_PATH };
			for (String path : paths) {
				CommonUtils.saveFile(null, Env.class.getResource("/").getPath()
						.replace("bin", "src")
						+ path + "/" + "package-info.java",
						"package " + path.replace("/", ".") + ";", false);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
