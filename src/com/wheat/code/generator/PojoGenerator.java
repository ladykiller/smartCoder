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
import com.wheat.code.inter.Generator;
import com.wheat.code.pojo.Column;
import com.wheat.code.pojo.Table;
import com.wheat.code.utils.CommonUtils;
import com.wheat.code.utils.MybatisUtils;
import com.wheat.code.utils.StringUtil;


/**
 * @author maihaijie
 *
 */
public class PojoGenerator implements Generator {
	private Log LOG = LogFactory.getLog(getClass().getSimpleName());
	
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
	
	/**
	 * 
	 * @param catalog
	 * @param table
	 * @param conn
	 * @return
	 */
	private List<Column> getColumns(String catalog, String table,Connection conn) {
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
                        column.setType("string");
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
                        column.setType("date");
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
	private static int[] getNumberMeta(Connection conn, String table, String column) {
        int[] result = new int[3];
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("select * from user_tab_cols where table_name = ? and column_name = ? and data_type = 'NUMBER'");

            ps.setString(1, table.toUpperCase());
            ps.setString(2, column.toUpperCase());
            rs = ps.executeQuery();

            if (rs.next()) {
                result[0] = rs.getInt("DATA_LENGTH");
                result[1] = rs.getInt("DATA_PRECISION");
                result[2] = rs.getInt("DATA_SCALE");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException sex) { sex.printStackTrace(); } }
            if (ps != null) { try { ps.close(); } catch (SQLException sex) { sex.printStackTrace(); } }
        }

        return result;
    }

	/* (non-Javadoc)
	 * @see com.wheat.code.inter.Generator#generate(java.util.Map)
	 */
	@Override
	public void generate() {
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
	        List<Table> tableList = new ArrayList<Table>();
		    while (tablesRs.next()) {
		    	table = new Table();
		    	tableName = tablesRs.getString("TABLE_NAME");
		    	desc = tablesRs.getString("REMARKS");
		    	table.setName(tableName);
		    	table.setDesc(desc);
		    	List<Column> columns = PojoGenerator.instance().getColumns(catalog, tableName,conn);
		    	table.setColumns(columns);
		    	tableList.add(table);
		    }
			String pojoName="";
			String[] temp;
			int i,size;
			StringBuffer buf = new StringBuffer();
			boolean isImportDate = false;
			String header = "";
			for(Table t : tableList){
				buf.setLength(0);
				temp = t.getName().split("_");
				size = temp.length;
				for(i=0;i<size;i++){
					pojoName += StringUtil.toUpperCaseFirstOne(temp[i]);
				}
				String colName,colType;
				for(Column c : t.getColumns()){
					colName = c.getName();
					colType = c.getType();
					if("string".equals(colType)){
						buf.append("\tprivate String "+colName +";\n");
					}
					if("int".equals(colType)){
						buf.append("\tprivate int "+colName +";\n");
					}
					if("float".equals(colType)){
						buf.append("\tprivate float "+colName +";\n");
					}
					if("double".equals(colType)){
						buf.append("\tprivate double "+colName +";\n");
					}
					if("date".equals(colType)){
						isImportDate = true;
						buf.append("\tprivate Date "+colName +";\n");
					}
				}
				if(isImportDate){
					header = "package "+Env.POJO_PATH.replace("/", ".")+";\n\nimport java.util.Date;\n\npublic class "+pojoName+"  extends AppObject{\n";
				}else{
					header = "package "+Env.POJO_PATH.replace("/", ".")+";\n\npublic class "+pojoName+"  extends AppObject{\n";
				}
				try {
					CommonUtils.saveFile(null, Env.class.getResource("/").getPath()
							.replace("bin", "src")
							+ Env.POJO_PATH + "/" + pojoName +".java",header+buf.toString()+"}", false);
				} catch (IOException e) {
					e.printStackTrace();
				}
				pojoName ="";
			}
			session.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
		}
	}
}
