package com.yjg.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 对excel文件操作,基于pio.jar
 * @author yjg
 *
 */
public class ExcelPIO {


	/**
	 * 读取excel文件(自动识别xls和xlsx)
	 * @param path 文件全路径
	 * @return 封装好的结果集.每行是一个String[],每个sheet是List<String[]>,整个文件是List<List<String[]>>
	 * @throws IOException
	 */
	public static List<List<String[]>> read(String path) throws IOException{
		File file=new File(path);
		FileInputStream in=new FileInputStream(file);
		Workbook workbook;
		List<List<String[]>> re_workbook=new LinkedList<List<String[]>>();
		if(path.endsWith(".xls")){
			workbook=new HSSFWorkbook(in);
		}
		else{
			workbook=new XSSFWorkbook(in);
		}
		in.close();
		int sheetNum=workbook.getNumberOfSheets();
		for(int i=0;i<sheetNum;i++){// 每个sheet
			Sheet sheet=workbook.getSheetAt(i);
			List<String[]> re_sheet=new LinkedList<String[]>();
			int rowNumOfSheet=sheet.getLastRowNum();
			int cellNum=maxCellNumOfRow(sheet);
			if(rowNumOfSheet==0 && cellNum==0)
				continue;
			for(int j=0;j<=rowNumOfSheet;j++){// 每行
				try {
					Row row=sheet.getRow(j);
					String[] re_row=new String[cellNum];
					for(int c_i=0;c_i<row.getLastCellNum();c_i++){
						re_row[c_i]="";
						Cell cell=row.getCell(c_i);
						if(cell!=null){
							re_row[c_i]=cell.toString();
						}					
					}
					re_sheet.add(re_row);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				
			}
			re_workbook.add(re_sheet);
		}
		return re_workbook;
	}
	
	/**
	 * 读取excel文件(自动识别xls和xlsx)
	 * @param is 输入流
	 * @param filename 文件名称(用于识别文件类型)
	 * @return 封装好的结果集.每行是一个String[],每个sheet是List<String[]>,整个文件是List<List<String[]>>
	 * @throws IOException
	 */
	public static List<List<String[]>> readFromInput(InputStream is,String filename) throws IOException{
		if(is instanceof FileInputStream){
			FileInputStream in=(FileInputStream) is;
			Workbook workbook;
			List<List<String[]>> re_workbook=new LinkedList<List<String[]>>();
			if(filename.endsWith(".xls")){
				workbook=new HSSFWorkbook(in);
			}
			else{
				workbook=new XSSFWorkbook(in);
			}
			in.close();
			int sheetNum=workbook.getNumberOfSheets();
			for(int i=0;i<sheetNum;i++){// 每个sheet
				Sheet sheet=workbook.getSheetAt(i);
				List<String[]> re_sheet=new LinkedList<String[]>();
				int rowNumOfSheet=sheet.getLastRowNum();
				int cellNum=maxCellNumOfRow(sheet);
				if(rowNumOfSheet==0 && cellNum==0)
					continue;
				for(int j=0;j<=rowNumOfSheet;j++){// 每行
					Row row=sheet.getRow(j);
					String[] re_row=new String[cellNum];
					for(int c_i=0;c_i<row.getLastCellNum();c_i++){
						re_row[c_i]="";
						Cell cell=row.getCell(c_i);
						if(cell!=null){
							re_row[c_i]=cell.toString();
						}					
					}
					re_sheet.add(re_row);
				}
				re_workbook.add(re_sheet);
			}
			return re_workbook;		
		}
		else
			return new LinkedList<List<String[]>>();
		
	}
	
	/**
	 * 写excel文件,以.xls格式保存
	 * @param data 文件数据List<List<String[]>>,每行是一个String[],每个sheet是List<String[]>,整个文件是List<List<String[]>>
	 * @param path 文件路径,若为Null,将采用默认路径
	 * @param filename 带后缀名,若为Null,将采用默认名称
	 * @throws IOException 
	 * @returns 保存文件的全路径，如果操作出错，返回空路径[""]
	 */
	public static String write(List<List<String[]>> data,String path,String filename){
		if(path==null || path.equals(""))
			path="e:/file/tmp/";
		if(filename==null || filename.equals(""))
			filename="data.xls";
		String pathname=path+filename;
		HSSFWorkbook wb=new HSSFWorkbook();
		int sheetNum=data.size();
		for(int i=0;i<sheetNum;i++){//sheet
			Sheet f_sheet=wb.createSheet("第"+(i+1)+"页");
			List<String[]> sheet=data.get(i);
			int rowNum=sheet.size();
			for(int j=0;j<rowNum;j++){
				Row f_row=f_sheet.createRow(j);
				String[] row=sheet.get(j);
				int cellNum=row.length;
				for(int n=0;n<cellNum;n++){
					Cell f_cell=f_row.createCell(n);
					f_cell.setCellValue(row[n]);
				}
			}
		}
		
		try{
			File file=new File(pathname);
			OutputStream os=new FileOutputStream(file);
			wb.write(os);
			os.close();
		}catch(Exception e){
			System.out.println("写文件出错");
			e.printStackTrace();
			return "";
		}
		return pathname;
	}
	
	/**
	 * 获取最大的列数
	 * @param sheet 数据
	 * @return
	 */
	private static int maxCellNumOfRow(Sheet sheet){
		int max=0;
		try {
			if(sheet==null)
				return 0;
			
			int rowNum=sheet.getLastRowNum();
			for(int i=0;i<rowNum;i++){
				Row row=sheet.getRow(i);
				if(row!=null){
					int cellNum=sheet.getRow(i).getLastCellNum();
					if(cellNum>max)
						max=cellNum;
				}				
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}		
		return max;
	}
}
