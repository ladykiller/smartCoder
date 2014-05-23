/*
 * Copyright 1997-2014 of PCGroup
 *
 * http://www.pcauto.com.cn/
 *
 */
package com.wheat.code.generator;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;

import com.wheat.code.constant.Env;
import com.wheat.code.generator.base.Column;
import com.wheat.code.generator.base.Table;
import com.wheat.code.utils.CommonUtils;
import com.wheat.code.utils.MybatisUtils;
import com.wheat.code.utils.StringUtil;

/**
 * @author maihaijie
 * 
 */
public class BaseGenerator {
	protected Log LOG = LogFactory.getLog(getClass().getSimpleName());
	/**
	 * 获取数据库表对象
	 */
	protected List<Table> tableList = getTables();
	
	protected BaseGenerator(){}
	/**
	 * 执行这个方法生成所有包路径
	 * 
	 * @param argc
	 */
	public static void createBasePackage() {
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
	
	private List<Table> getTables(){
		List<Table> tableList = new ArrayList<Table>();
		try {
			SqlSession session = MybatisUtils.getSessionFactory().openSession();
			Connection conn= session.getConnection();
			DatabaseMetaData dbMeta = conn.getMetaData();
			String catalog = null;//使用默认
			String[] types = { "TABLE","VIEW"};
	        String schemaPattern = catalog ;
	        String tableNamePattern = null;//使用默认
	        Table table;
	        String tableName, desc;
	        ResultSet tablesRs = dbMeta.getTables(catalog, schemaPattern, tableNamePattern, types);
		    while (tablesRs.next()) {
		    	table = new Table();
		    	tableName = tablesRs.getString("TABLE_NAME");
		    	desc = tablesRs.getString("REMARKS");
		    	table.setName(tableName);
		    	table.setDesc(desc);
		    	List<Column> columns = getColumns(catalog, tableName,conn);
		    	table.setColumns(columns);
		    	tableList.add(table);
		    }
		}catch(Exception e){
			e.printStackTrace();
		}
		return tableList;
	}
	
	/**
	 * 
	 * @param catalog
	 * @param table
	 * @param conn
	 * @return
	 */
	protected List<Column> getColumns(String catalog, String table,Connection conn) {
        try {
            DatabaseMetaData dbMeta = conn.getMetaData();
            String schemaPattern = catalog;
            String columnNamePattern = null;
            ResultSet colsRs = dbMeta.getColumns(catalog, schemaPattern, table, columnNamePattern);
            String name, desc,defaultValue;
            int type, size, presize;
            Column column;
            if (colsRs != null) {
                List<Column> colums = new ArrayList<Column>();
                while (colsRs.next()) {
                    name = colsRs.getString("COLUMN_NAME").toLowerCase();
                    desc = colsRs.getString("REMARKS");
                    type = colsRs.getInt("DATA_TYPE");
                    size = colsRs.getInt("COLUMN_SIZE");
                    presize = colsRs.getInt("DECIMAL_DIGITS");
                    defaultValue = colsRs.getString("COLUMN_DEF");
                    
                    column = new Column();
                    column.setName(name);
                    column.setDesc(desc);
                    column.setNullable(colsRs.getInt("NULLABLE") == DatabaseMetaData.columnNullable?1:0);
                    column.setDefaultValue(defaultValue);
                    
                    switch (type) {
                    case Types.CHAR:
                    case Types.VARCHAR:
                    case Types.LONGNVARCHAR:
                    case Types.LONGVARCHAR:
                    case Types.LONGVARBINARY:
                        column.setType("String");
                        column.setLength(colsRs.getInt("CHAR_OCTET_LENGTH"));
                        break;
                    case Types.INTEGER:
                    case Types.BIGINT:
                        column.setType("int");
                        break;
                    case Types.FLOAT:
                        column.setType("float");
                        break;
                    case Types.DECIMAL:
                        /*
                        if (presize == 0) {
                            column.setType("long");
                        } else {
                            column.setType("float");
                        }
                        */ 
                        int[] m = getNumberMeta(conn, table, name);
                        int l = m[0];
                        int p = m[1];
                        int s = m[2];
                        if (s == 0) {
                            column.setType("long");
                            presize = 0;
                        } else {
                            column.setType("float");
                            presize = s;
                        }
                        if (size == 0) {
                            if (p > 0) {
                                size = p;
                            } else {
                                size = l;
                            }
                        }
                        break;
                    case Types.DOUBLE:
                        column.setType("double");
                        break;
                    case Types.DATE:
                    case Types.TIME:
                    case Types.TIMESTAMP:
                        column.setType("Date");
                        break;
                    case Types.CLOB:
                        column.setType("clob");
                        break;
                    default:
                        LOG.warn(StringUtil .concat("\tFind unkown type:", type, " for Table:", table, " field:", name, ". NOTE"));
                    }
                    column.setPrecision(presize);
                    column.setLength(size);
                    colums.add(column);
                }
                return colums;
            }

        } catch (SQLException e) {
        	e.printStackTrace();
        } 
        return null;
    }
  
	/**
	 * 
	 * @param conn
	 * @param table
	 * @param column
	 * @return
	 */
	protected static int[] getNumberMeta(Connection conn, String table, String column) {
        int[] result = new int[3];
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("select * from information_schema.columns where table_name = ? and column_name = ? and data_type = 'DECIMAL'");

            ps.setString(1, table);
            ps.setString(2, column);
            rs = ps.executeQuery();

            if (rs.next()) {
                result[0] = rs.getInt("NUMERIC_PRECISION");
                result[1] = rs.getInt("NUMERIC_PRECISION");
                result[2] = rs.getInt("NUMERIC_SCALE");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException sex) { sex.printStackTrace(); } }
            if (ps != null) { try { ps.close(); } catch (SQLException sex) { sex.printStackTrace(); } }
        }

        return result;
    }

	protected String generateGetter(String type,String name){
		return "\tpublic "+ type +" get"+StringUtil.toUpperCaseFirstOne(name)+"(){\n\t\treturn this."+name+";\n\t}\n";
	}
	
	protected String generateSetter(String type,String name){
		return "\tpublic void set"+StringUtil.toUpperCaseFirstOne(name)+"("+type+" "+name+"){\n\t\tthis."+name+"="+name+";\n\t}\n";
	}
	
	protected String generateGetterAndSetter(String type,String name){
		return generateGetter(type,name)+generateSetter(type,name);
	}
	
}
