package com.yjg.utils;

import com.aspose.words.Document;
import com.aspose.words.License;
import com.aspose.words.SaveFormat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
/**
 * 利用 aspose.word实现 word2pdf
 * @author yjg
 *
 */
public class Word2PdfUtils {	

	/**
	 * 获取破解License,否则有水印
	 * @return
	 */
	private static boolean getLicense() {
        boolean result = false;
        try {
        	File _key=new File(Params.rootUrl+"files/asposeLicense.xml");//破解license,正版太贵
        	
            InputStream is = new FileInputStream(_key);
            License aposeLic = new License();
            aposeLic.setLicense(is);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
 
	/**
	 * word转pdf
	 * @param inPath 待转换文件路径(doc,docx)
	 * @param outPath 已转换文件(pdf)
	 * @return 转换成功,返回新文件路径,失败返回空字符串
	 */
    public static String doc2pdf(String inPath, String outPath) {
        if (!getLicense()) { // 验证License 若不验证则转化出的pdf文档会有水印产生
            return "";
        }
        try {            
            File file = new File(outPath); // 新建一个空白pdf文档
            FileOutputStream os = new FileOutputStream(file);
            Document doc = new Document(inPath); // Address是将要被转化的word文档
            doc.save(os, SaveFormat.PDF);// 全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF,
                                         // EPUB, XPS, SWF 相互转换          
            return outPath;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
