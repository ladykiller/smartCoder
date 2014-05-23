/*
 * Copyright 1997-2014 of PCGroup
 *
 * http://www.pcauto.com.cn/
 *
 */
package com.wheat.code.generator;

import com.wheat.code.constant.Env;
import com.wheat.code.generator.base.Column;
import com.wheat.code.generator.base.Table;
import com.wheat.code.inter.Generator;
import com.wheat.code.utils.CommonUtils;
import com.wheat.code.utils.StringUtil;


/**
 * @author maihaijie
 *
 */
public class PojoGenerator extends BaseGenerator implements Generator {
	private static PojoGenerator pojoGenerator;
	private PojoGenerator(){}
	
	public static PojoGenerator instance(){
		if(pojoGenerator==null){
			pojoGenerator = new PojoGenerator();
		}
		return pojoGenerator;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PojoGenerator.instance().generate();
	}
	
	/* (non-Javadoc)
	 * @see com.wheat.code.inter.Generator#generate(java.util.Map)
	 */
	@Override
	public void generate() {
		//清空文件
		CommonUtils.delAllFile(Env.class.getResource("/").getPath().replace("bin", "src")+Env.POJO_PATH );
		try {
			//生产包信息
			CommonUtils.saveFile(null, Env.class.getResource("/").getPath().replace("bin", "src")
					+ Env.POJO_PATH + "/" + "package-info.java", "package "+ Env.POJO_PATH.replace("/", ".") + ";", false);
			String pojoName="";
			String[] temp;
			int i,size;
			StringBuffer buf = new StringBuffer();
			StringBuffer getAndsetBuf = new StringBuffer();
			boolean isImportDate;
			String header = "";
			for(Table t : tableList){
				buf.setLength(0);
				getAndsetBuf.setLength(0);
				isImportDate = false;
				temp = t.getName().split("_");
				size = temp.length;
				for(i=0;i<size;i++){
					pojoName += StringUtil.toUpperCaseFirstOne(temp[i]);
				}
				String colName,colType;
				for(Column c : t.getColumns()){
					colName = c.getName();
					colType = c.getType();
					if("Date".equals(colType)){
						isImportDate = true;
					}
					buf.append("\tprivate "+colType+" "+colName +";\n");
					getAndsetBuf.append(generateGetterAndSetter(colType,colName));
					
				}
				if(isImportDate){
					header = "package "+Env.POJO_PATH.replace("/", ".")+";\n\nimport java.util.Date;\nimport com.wheat.code.generator.base.AppObject;\n\npublic class "+pojoName+"  extends AppObject{\n";
				}else{
					header = "package "+Env.POJO_PATH.replace("/", ".")+";\n\nimport com.wheat.code.generator.base.AppObject;\n\npublic class "+pojoName+"  extends AppObject{\n";
				}
				
				CommonUtils.saveFile(null, Env.class.getResource("/").getPath()
						.replace("bin", "src")
						+ Env.POJO_PATH + "/" + pojoName +".java",header+buf.toString()+"\n"+getAndsetBuf.toString()+"}", false);
				
				pojoName ="";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
		}
	}
}
