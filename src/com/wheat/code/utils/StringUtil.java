package com.wheat.code.utils;


import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

public final class StringUtil {
    public static String clob2String(Clob clob) throws SQLException {
        if (clob == null) {
            return null;
        }
        String result = null;
        Reader inReader = clob.getCharacterStream();
        char[] c = new char[(int) clob.length()];
        try {
            inReader.read(c);
            result = new String(c);
            inReader.close();
        } catch (IOException e) {
        }
        return result;
    }

    
    public static String getSizeDesc(int size){
    	StringBuilder buf = new StringBuilder();
    	int g = size/(1024*1024*1024);
    	int mm = size%(1024*1024*1024);
    	int m = mm/(1024*1204);
    	int kk = mm%(1024*1204);
    	int k = kk/1024;
    	int b = kk%1024;
    	if(g>0){
    		buf.append(g).append("G ");
    	}
    	if(m>0){
    		buf.append(m).append("M ");
    	}
    	if(k>0){
    		buf.append(k).append("K ");
    	}
    	if(b>0){
    		buf.append(b).append("B ");
    	}
    	return buf.toString();
    }
    public static String getTimeDesc(long time){
        StringBuilder buf = new StringBuilder();
        long h = time/(60*60*1000);
        long mm = time%(60*60*1000);
        long m = mm/(60*1000);
        long ss = mm%(60*1000);
        long s = ss/1000;
        if(h>0){
            buf.append(h).append(" 小时 ");
        }
        if(m>0){
            buf.append(m).append(" 分 ");
        }
        if(s>0){
            buf.append(s).append(" 秒 ");
        }
        buf.append(ss%1000).append(" 毫秒");
        return buf.toString();
    }

    public static boolean isEmpty(String s) {
        if (s != null && !"".equals(s.trim())) {
            return false;
        }
        return true;
    }

    public static String concat(String... argv) {
        StringBuilder buf = new StringBuilder();
        for (String s : argv) {
            buf.append(s);
        }
        return buf.toString();
    }

    public static String replaceChar(String s, char from, char to) {
        if (s != null && s.length() > 0) {
            StringBuilder buf = new StringBuilder();
            int len = s.length();
            for (int i = 0; i < len; i++) {
                char c = s.charAt(i);
                if (c == from) {
                    buf.append(to);
                } else {
                    buf.append(c);
                }
            }
            return buf.toString();
        }
        return s;
    }


    public static String concat(Object... argv) {
        StringBuilder buf = new StringBuilder();
        for (Object s : argv) {
            buf.append(s);
        }
        return buf.toString();
    }

    public static boolean containNumber(String s) {
        if(!isEmpty(s)){
            int len = s.length();
            char c;
            for(int i=len-1;i>=0;i--){
                c = s.charAt(i);
                if((c>='0' && c<='9') ){
                    return true;
                }
            }
        }
        return false;
    }
    
    public static SimpleDateFormat getDateFormat(){
    	return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
    
    public static java.util.Date getDate(String value){
    	try {
    		if(!StringUtil.isEmpty(value)){
    			//yyyy-MM-dd HH:mm:ss
    			if(value.length()==10){
    				value = StringUtil.concat(value," 00:00:00");
    			}
    			return getDateFormat().parse(value);
    		}
		} catch (Exception e) {
			throw new RuntimeException(StringUtil.concat("You must provide date value[",value,"],yyyy-mm-dd hh:mi:ss or yyyy-mm-dd"));
		}
		return null;
    }

    /*
     * 获取汉语拼音，小写字母输出
     */
    public static String toPinYin(String hanzhis) {
		try{
			CharSequence s = hanzhis;
	
			char[] hanzhi = new char[s.length()];
			for (int i = 0; i < s.length(); i++) {
				hanzhi[i] = s.charAt(i);
			}
	
			char[] t1 = hanzhi;
			String[] t2 = new String[s.length()];
			/** */
			/**
			 * 设置输出格式
			 */
			HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
			t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
			t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
			t3.setVCharType(HanyuPinyinVCharType.WITH_V);
	
			int t0 = t1.length;
			String py = "";
			
			for (int i = 0; i < t0; i++) {
				if ((t1[i] >= 0 && t1[i] <= 255)) {
					py += String.valueOf(t1[i]);
					continue;
				}
				try{
					t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);
					py = py + t2[0].toString();
				}catch(Exception e){
					
				}
			}
	
			return py.trim();
		}catch(Exception e){
			return null;
		}
	}
    

	public static Date parseDate(String date) throws ParseException {
		int c = date.charAt(0);
		if (c >= '0' && c <= '9') {
			if (date.length() == 10) {
				date = date + " 00:00:00";
			}
			return getThreadLocalDateFormat().parse(date);
		} else {
			return getThreadLocalDateFormat2().parse(date);
		}
	}

    public static String formatDate(Date date) {
    		return getThreadLocalDateFormat().format(date);
    }

    public static String formatDate2(Date date) {
    		return getThreadLocalDateFormat2().format(date);
    }

    	/**
    	 * Get an Threadsafe DateFormat object.
    	 * @return
    	 */
    private static DateFormat getThreadLocalDateFormat() {
    	return fmtThreadLocal.get();
    }
    	
    private static ThreadLocalDateFormat fmtThreadLocal = new ThreadLocalDateFormat();
    	
    private static class ThreadLocalDateFormat extends ThreadLocal<DateFormat> {
    	DateFormat proto;
    	
    	public ThreadLocalDateFormat() {
    	super();
    	
    	SimpleDateFormat tmp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		proto = tmp;
    	}
    	@Override
    	protected DateFormat initialValue() {
    		return (DateFormat) proto.clone();
    		}
    }

    private static DateFormat getThreadLocalDateFormat2() {
    	return fmtThreadLocal2.get();
    }
    	
    private static ThreadLocalDateFormat2 fmtThreadLocal2 = new ThreadLocalDateFormat2();
    	
    private static class ThreadLocalDateFormat2 extends ThreadLocal<DateFormat> {
    	DateFormat proto;
	    public ThreadLocalDateFormat2() {
	    	super();
	    	SimpleDateFormat tmp = new SimpleDateFormat("EEE MMM d HH:mm:ss 'CST' yyyy", Locale.ROOT);
	    	proto = tmp;
	   	}
	   	@Override
	   	protected DateFormat initialValue() {
	   		return (DateFormat) proto.clone();
	   	}
   	}

	// 首字母转小写
	public static String toLowerCaseFirstOne(String s) {
		if (Character.isLowerCase(s.charAt(0)))
			return s;
		else
			return (new StringBuilder())
					.append(Character.toLowerCase(s.charAt(0)))
					.append(s.substring(1)).toString();
	}

	// 首字母转大写
	public static String toUpperCaseFirstOne(String s) {
		if (Character.isUpperCase(s.charAt(0)))
			return s;
		else
			return (new StringBuilder())
					.append(Character.toUpperCase(s.charAt(0)))
					.append(s.substring(1)).toString();
	}
    
}

