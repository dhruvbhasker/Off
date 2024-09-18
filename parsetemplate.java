package fileutil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import au.com.bytecode.opencsv.CSVReader;

public class ParseTemplate {
	
	public List getTemplateList(File file) throws IOException { 
		List <ExtractTemplate> FieldsList = new ArrayList <ExtractTemplate>();
		CSVReader reader=new CSVReader(
			    new InputStreamReader(new FileInputStream(file), "Shift-JIS"), 
			    ',', '\'', 1);

		String[] row;
		while ((row = reader.readNext()) != null)
		{
		    if (row.length != 4) {
		        throw new RuntimeException("Unexpected number of entries: " + row.length);
		    }
		    byte[] byteColumnName = row[0].getBytes("Shift-JIS"); 
		    String strColmnName = new String(byteColumnName,"Shift-JIS");
		    FieldsList.add(new ExtractTemplate(strColmnName, row[1], row[2], row[3]));
		}
		
		return FieldsList;
	}
	
	public List getMismatchcsv(File file) throws IOException { 
		List <ExtractTemplate> FieldsList = new ArrayList <ExtractTemplate>();
		CSVReader reader=new CSVReader(
			    new InputStreamReader(new FileInputStream(file), "Shift-JIS"), 
			    ',', '\'', 1);

		String[] row;
		while ((row = reader.readNext()) != null)
		{
		    if (row.length != 4) {
		        throw new RuntimeException("Unexpected number of entries: " + row.length);
		    }
		    byte[] byteColumnName = row[0].getBytes("Shift-JIS"); 
		    String strColmnName = new String(byteColumnName,"Shift-JIS");
		    FieldsList.add(new ExtractTemplate(strColmnName, row[1], row[2], row[3]));
		}
		
		return FieldsList;
	}

}
