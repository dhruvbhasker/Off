package main;

//Java Program to Convert File to a Byte Array
//Using read(byte[]) Method

//Importing required classes
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mlj.util.property.FilePropertiesDetail;

import au.com.bytecode.opencsv.CSVReader;

//Main class
public class GetMismatch {
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		File file = new File(
				"S:\\MCL\\Information Technology\\VASystemG\\Regression_Test_Result\\PhoenixS2_Cycle3\\AGW\\ComparisonResult\\EQ31_KanjiNames\\CompleteMismatch.csv");
		List<String> lstTemplate = getMismatchcsv(file);

	}

	public static List getMismatchcsv(File file) throws IOException {
		String colName1 = "Key Column Value";
		String colName2 = "Column Name / Index";
		String colName3 = "Source Data";
		String colName4 = "Target Data";
		String strHdr[] = { "Col-1", "Col-2", "Col-3" };
		List<String> FieldsList = new ArrayList<String>();
		CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(file), "Shift-JIS"), '~', '\'', 1);

		String[] row;
		long lineNo = 1;
		List<String> lstSrc = new ArrayList<String>();
		List<String> lstTrt = new ArrayList<String>();
		
		while ((row = reader.readNext()) != null) {
			List<String> lstTemp = new ArrayList<String>();
			for (int r = 1; r < row.length; r++) {
				byte[] byteColumnName = row[r].getBytes("Shift-JIS");
				String strColmnName = new String(byteColumnName, "Shift-JIS");
				lstTemp.add(strColmnName);
			}
			if (lineNo % 2 == 1  && lineNo % 3 != 0) {
				lstSrc.addAll(lstTemp);
			} else if (lineNo % 2 == 0) {
				lstTrt.addAll(lstTemp);
			}else {
				for (int c = 1; c < lstTrt.size(); c++) {
					FieldsList.add(row[0] + "~" + strHdr[c] + "~" + lstSrc.get(c) + "~" + lstTrt.get(c));
				}				
			}
//				if (lineNo % 1 == 0  && lineNo % 3 != 0) {
//					lstSrc.add(strHdr[r - 1] + "~" + strColmnName);
//				} else if (lineNo % 2 == 0) {
//					lstTrt.add("~" + strColmnName);
//				} else {
//					FieldsList.add(row[0] + "~" + lstSrc + "~" + lstTrt);
//				}

//			}
			
			lineNo = lineNo+1;
		}
		return FieldsList;

	}
}
