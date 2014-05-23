/*
 * Copyright 1997-2014 of PCGroup
 *
 * http://www.pcauto.com.cn/
 *
 */
package com.wheat.code.generator;

import java.util.List;
import java.util.Map;

import com.wheat.code.constant.Env;
import com.wheat.code.generator.base.Table;
import com.wheat.code.inter.Generator;
import com.wheat.code.utils.CommonUtils;
import com.wheat.code.utils.StringUtil;

/**
 * @author maihaijie
 *
 */
public class DaoGenerator extends BaseGenerator implements Generator {
	private static DaoGenerator daoGenerator;
	private DaoGenerator(){}
	
	public static DaoGenerator instance(){
		if(daoGenerator==null){
			daoGenerator = new DaoGenerator();
		}
		return daoGenerator;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DaoGenerator.instance().generate();
	}
	
	/* (non-Javadoc)
	 * @see com.wheat.code.inter.Generator#generate()
	 */
	@Override
	public void generate() {
		//清空文件
		//CommonUtils.delAllFile(Env.class.getResource("/").getPath().replace("bin", "src")+Env.DAO_PATH );
		try {
			//生产包信息
			CommonUtils.saveFile(null, Env.class.getResource("/").getPath().replace("bin", "src")
					+ Env.DAO_PATH + "/" + "package-info.java", "package "+ Env.DAO_PATH.replace("/", ".") + ";", false);
			String pojoName = "";
			String daoName = "";
			String[] temp;
			int i,size;
			StringBuffer buf = new StringBuffer();
			StringBuffer mapperBuf = new StringBuffer();
			String header = "";
			for(Table t : tableList){
				buf.setLength(0);
				mapperBuf.setLength(0);
				temp = t.getName().split("_");
				size = temp.length;
				for(i=0;i<size;i++){
					pojoName += StringUtil.toUpperCaseFirstOne(temp[i]);
				}
				daoName = pojoName+ "Dao";
				String colName,colType;
				
				buf.append("\tpublic Object select"+pojoName+"ById(String Id);\n\n");
				buf.append("\t@SuppressWarnings(\"rawtypes\")\n\tpublic Object insert"+pojoName+"(Map requestMap);\n\n");
				buf.append("\t@SuppressWarnings(\"rawtypes\")\n\tpublic void update"+pojoName+"(Map requestMap);");
				buf.append("\tpublic void delete"+pojoName+"ById(String Id);\n\n");
				buf.append("\t@SuppressWarnings(\"rawtypes\")\n\tpublic List delete"+pojoName+"ByCondition(Map requestMap);\n");
			}
			header = "package "+Env.DAO_PATH.replace("/", ".")+";\n\nimport java.util.List;\nimport java.util.Map;\n\npublic interface "+daoName+"{\n";
			
		}catch(Exception e){
			e.printStackTrace();
		}

	}

}
