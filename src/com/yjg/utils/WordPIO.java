package com.yjg.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

/**
 * 读写word
 * @author yjg
 *
 */
public class WordPIO {

	/**
	 * 读取word内容
	 * @param filepath
	 * @return
	 */
	public static String readWord(String filepath){
		if(MyUtils.isEmpty(filepath))
			return null;
		String buffer = "";  
        try {  
            if (filepath.endsWith(".doc")) {  
                InputStream is = new FileInputStream(new File(filepath));  
                WordExtractor ex = new WordExtractor(is);  
                buffer = ex.getText();  
                ex.close();  
            } else if (filepath.endsWith("docx")) {  
                OPCPackage opcPackage = POIXMLDocument.openPackage(filepath);  
                POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);  
                buffer = extractor.getText();  
                extractor.close();  
            } else {  
                System.out.println("此文件不是word文件！");  
            }  
  
        } catch (Exception e) {  
            e.printStackTrace();  
        }          
        return buffer; 		
	}
}
