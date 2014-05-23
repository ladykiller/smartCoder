package com.wheat.code.utils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtils {
	/**
	 * 生成UUID
	 * 
	 * @return
	 */
	public static String generateUUID() {
		StringBuffer buf = new StringBuffer();
		for (String s : (java.util.UUID.randomUUID().toString().split("-"))) {
			buf.append(s);
		}
		return buf.toString();
	}

	public static String generateXLH() {
		return String.valueOf(System.currentTimeMillis());
	}

	/**
	 * Java保存文件工具类
	 * 
	 * @param savePalce
	 *            文件夹路径
	 * @param fileName
	 *            文件路径
	 * @param context
	 *            保存内容
	 * @throws IOException
	 */
	public static void saveFile(String savePalce, String fileName,
			String context,boolean append) throws IOException {
		if (savePalce != null) {
			File dir = new File(savePalce);
			if (!dir.exists()) {
				dir.mkdirs();
			}
		}
		if (fileName != null) {
			String[] parts = fileName.split("/");
			int partslen = parts.length, i;
			StringBuffer dirName = new StringBuffer();
			for (i = 0; i < partslen - 1; i++) {
				dirName.append(parts[i] + "/");
			}
			File dir = new File(dirName.toString());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File file = new File(fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream out = new FileOutputStream(file, append);
			if (context != null) {
				out.write(context.getBytes("UTF-8"));
			}
			out.close();
		}
	}
	
	/**
	 * 读取文件内容，编码可指定
	 * @param fileName
	 * @param encoding
	 * @return
	 * @throws IOException
	 */
	public static String readFile(String fileName,String encoding) throws IOException {
		if (fileName != null) {
			File file = new File(fileName);
			if(file.exists()){
				InputStream fileReader1=new FileInputStream(file);
				BufferedReader buffer1=new BufferedReader(new InputStreamReader(fileReader1,encoding));
				String str = null;
				StringBuffer strBuff = new StringBuffer();
				while((str = buffer1.readLine() ) != null){
					strBuff.append(str);
				}
				return strBuff.toString();
			}
				
		}
		return null;
	}
	public static String formatDate(Timestamp date) {
		java.text.Format format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(date);
	}
	
	public static String formatDate(Date date) {
		java.text.Format format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(date);
	}

	public static String formatTime(Timestamp date) {
		java.text.Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(date);
	}
	
	public static String formatTime(Date date) {
		java.text.Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(date);
	}

	public static int[] quickSort(int[] arr, int start, int end) {
		if (start < end) {
			int key = arr[start], i = start, j = end + 1, temp;
			while (i < j) {
				while (arr[++i] < key && i < end)
					;
				while (arr[--j] > key)
					;// --j，可以让arr[j]等于他自己或小于他的数时停下来。j--,停下来后还要减1。容易出现-1越界。
				if (i < j) {
					temp = arr[i];
					arr[i] = arr[j];
					arr[j] = temp;
				}
			}
			arr[start] = arr[j];
			arr[j] = key;
			quickSort(arr, start, j - 1);
			quickSort(arr, j + 1, end);
		}
		return arr;
	}
	
	//删除文件夹
	//param folderPath 文件夹完整绝对路径
	public static void delFolder(String folderPath) {
	     try {
	        delAllFile(folderPath); //删除完里面所有内容
	        String filePath = folderPath;
	        filePath = filePath.toString();
	        java.io.File myFilePath = new java.io.File(filePath);
	        myFilePath.delete(); //删除空文件夹
	     } catch (Exception e) {
	       e.printStackTrace(); 
	     }
	}
	
	//删除指定文件夹下所有文件
	//param path 文件夹完整绝对路径
	public static boolean delAllFile(String path) {
       boolean flag = false;
       File file = new File(path);
       if (!file.exists()) {
         return flag;
       }
       if (!file.isDirectory()) {
         return flag;
       }
       String[] tempList = file.list();
       File temp = null;
       for (int i = 0; i < tempList.length; i++) {
          if (path.endsWith(File.separator)) {
             temp = new File(path + tempList[i]);
          } else {
              temp = new File(path + File.separator + tempList[i]);
          }
          if (temp.isFile()) {
             temp.delete();
          }
          if (temp.isDirectory()) {
             delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
             delFolder(path + "/" + tempList[i]);//再删除空文件夹
             flag = true;
          }
       }
       return flag;
     }
}
