
package main;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import com.cognizant.platinum.core.util.comparsionUtility.ComparisonLogic;
import com.cognizant.platinum.core.util.comparsionUtility.ComparisonPreparer;
import com.cognizant.platinum.core.util.comparsionUtility.DTO.ComparisonDataDTO;
import com.mlj.util.property.FilePropertiesDetail;

import fileutil.Convert2CSV;
import fileutil.ExtractTemplate;
import fileutil.ParseTemplate;

import au.com.bytecode.opencsv.CSVReader;


public class Main4 {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		dbutil.DB2Connect dbConnect = new dbutil.DB2Connect();
		FilePropertiesDetail extractFileDetails = new FilePropertiesDetail();		
		Connection con = null;		 
		String[] strExtractName = extractFileDetails.getStrExtractName().split(";");		

		try {
//				con = dbConnect.connectDB2DB(extractFileDetails.getStrDBHostName(),extractFileDetails.getStrDBPortNumber(),extractFileDetails.getStrDBSchema(),extractFileDetails.getStrDBUserName(),extractFileDetails.getStrDBPassword());
				Convert2CSV appGeneratedCSV = new Convert2CSV();
				boolean BlnFetchExtractUsingSQL = extractFileDetails.isBlnFetchExtractUsingSQL();
				for(int index=0;index<strExtractName.length;index++){				
					System.out.println("Data preprocessing start"); 
//					removeCommaInQuote(System.getProperty("user.dir")+"\\AppGenExtractFiles\\", strExtractName[index]);
					System.out.println("Data preprocessing end");
					if(BlnFetchExtractUsingSQL){ // To check whether using SQL query or not
						String sql = new String(Files.readAllBytes(Paths.get("./Templates/"+strExtractName[index]+".sql")));  
						sql = sql.replace("PROCESSING~DATE", extractFileDetails.getStrprocessingDate());
						boolean fileGen = 	dbConnect.extractGenerationFromQuery(con, strExtractName[index], sql);
						if(fileGen) 
						{
							System.out.println("Tool Generated Extract File >> "+strExtractName[index]);
						}else{
							System.out.println("Tool Not Generated >> "+strExtractName[index]);
						}
						appGeneratedCSV.convertCSVFromFile(strExtractName[index]);
					}else{
//						appGeneratedCSV.convertCSVFromFile(strExtractName[index],"PRE");
//						appGeneratedCSV.convertCSVFromFile(strExtractName[index],"POST");						
					}						
					ComparisonDataDTO compDTO = getDTO(strExtractName[index], BlnFetchExtractUsingSQL);

					ComparisonLogic comp = new ComparisonLogic(System.getProperty("user.dir")+"\\TestCase\\"+compDTO.getTestSuiteName()+"\\"+compDTO.getTestCaseName());		
					comp.compareData(compDTO);

					ParseTemplate extractTemplate = new ParseTemplate();
					File template_file = new File("./Templates/"+strExtractName[index]+".csv");  
					List <ExtractTemplate> lstTemplate = extractTemplate.getTemplateList(template_file);	
				
//					ConvertMismatchCsv.ConvertToMismatchCSV(compDTO, lstTemplate);
					ConvertToMismatchCSV(compDTO, lstTemplate);
//					ConvertToMismatchCSV2(compDTO, lstTemplate);
//					ConvertMismatchCsv.main();
					System.out.println("done");
//				    Files.move(Paths.get(compDTO.getStrFolderPath()+"\\Mismatch.csv"), Paths.get(compDTO.getStrFolderPath()+"\\Mismatch.csv").resolveSibling("Mismatch(old file dont use).csv"));
				}
//				con.close();
				
				
				
//		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // UAT
	 catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	private static void removeCommaInQuote(String url, String fileName) throws IOException, InterruptedException {
		String preCsvFile = url+"PRE\\CSV_Files\\"+fileName+".csv";
		String postCsvFile = url+"POST\\CSV_Files\\"+fileName+".csv";
		
		String newPreCsvFile = url+"PRE\\CSV_Files\\"+fileName+"(new).csv";;
		String newPostCsvFile = url+"POST\\CSV_Files\\"+fileName+"(new).csv";;

		File preNewFile = new File(newPreCsvFile);
		File postNewFile = new File(newPostCsvFile);

		preNewFile.delete();
		postNewFile.delete();
		FileWriter preFileWriter = new FileWriter(preNewFile, true);
		FileWriter postFileWriter = new FileWriter(postNewFile, true);

		int releasePoint = 0;

		BufferedReader PreBr = new BufferedReader(new InputStreamReader( new FileInputStream(preCsvFile), Charset.forName("cp932")));
		BufferedReader PostBr = new BufferedReader(new InputStreamReader( new FileInputStream(postCsvFile), Charset.forName("cp932")));

		String PreLine, PostLine;
		while ((PreLine = PreBr.readLine())!=null) {
			
			boolean PreinQuotes = false;
			StringBuilder Presb = new StringBuilder();
			for (char c : PreLine.toCharArray()) {
				if (c == '"' && PreinQuotes == false) {
					PreinQuotes = true;
					Presb.append(c);

				}else if(c == '"' && PreinQuotes == true) {
					PreinQuotes = false;
					Presb.append(c);

				}else if ((c == ',') && PreinQuotes==true) {
					Presb.append('�C');
				}else {
					Presb.append(c);
				}
			}
			preFileWriter.write(Presb.toString()+"\r\n");	
			releasePoint++;
			if (releasePoint>=100) {
				releasePoint=0;
				preFileWriter.close();
				try {
					preFileWriter = new FileWriter(preNewFile, true);
				}catch(FileNotFoundException e ) {
					System.out.println("File is used by another process. Sleep 6 sec and open file again..");
					Thread.sleep(6000);
					try {
						preFileWriter = new FileWriter(preNewFile, true);
					}catch(Exception error) {
						System.out.println("File is used by another process. Sleep 3 sec and open file again..");
						Thread.sleep(3000);
						preFileWriter = new FileWriter(preNewFile, true);
					}
				}catch(Exception e) {
					System.out.println("Sleep 6 sec and open file again..");
					Thread.sleep(6000);
					try {
						preFileWriter = new FileWriter(preNewFile, true);
					}catch(Exception error) {
						System.out.println("Sleep 3 sec and open file again..");
						Thread.sleep(3000);
						preFileWriter = new FileWriter(preNewFile, true);
					}
				}

			}
		}
		
		preFileWriter.close();
		PreBr.close();
		 
		while ((PostLine = PostBr.readLine())!=null) {
//			System.out.println(PostLine);
			boolean PostinQuotes = false;
			StringBuilder Postsb = new StringBuilder();
			for (char c : PostLine.toCharArray()) {
				if (c == '"' && PostinQuotes == false) {
					PostinQuotes = true;
					Postsb.append(c);

				}else if(c == '"' && PostinQuotes == true) {
					PostinQuotes = false;
					Postsb.append(c);

				}else if ((c == ',') && PostinQuotes==true) {
					Postsb.append('�C');
				}else {
					Postsb.append(c);
				}
			}
//			System.out.println(" PostLine2: " + Postsb.toString());
			postFileWriter.write(Postsb.toString()+"\r\n");	
			releasePoint++;
			if (releasePoint>=100) {
				releasePoint=0;
				postFileWriter.close();
				try {
					postFileWriter = new FileWriter(postNewFile, true);
				}catch(FileNotFoundException e ) {
					System.out.println("File is used by another process. Sleep 6 sec and open file again..");
					Thread.sleep(6000);
					try {
						postFileWriter = new FileWriter(postNewFile, true);
					}catch(Exception error) {
						System.out.println("File is used by another process. Sleep 3 sec and open file again..");
						Thread.sleep(3000);
						postFileWriter = new FileWriter(postNewFile, true);
					}
				}catch(Exception e) {
					System.out.println("Sleep 6 sec and open file again..");
					Thread.sleep(6000);
					try {
						postFileWriter = new FileWriter(postNewFile, true);
					}catch(Exception error) {
						System.out.println("Sleep 3 sec and open file again..");
						Thread.sleep(3000);
						postFileWriter = new FileWriter(postNewFile, true);
					}
				}

			}
		}
		postFileWriter.close();
		PostBr.close();
		
//		String preCsvFile = url+"PRE\\CSV_Files\\"+fileName+".csv";
//		String postCsvFile = url+"POST\\CSV_Files\\"+fileName+".csv";
//		
//		String newPreCsvFile = url+"PRE\\CSV_Files\\"+fileName+"(new).csv";;
//		String newPostCsvFile = url+"POST\\CSV_Files\\"+fileName+"(new).csv";;
//
//		File preNewFile = new File(newPreCsvFile);
//		File postNewFile = new File(newPostCsvFile);
//		
		File oldPreCsvFile1 = new File(preCsvFile);
		File oldPreCsvFile2 = new File(preCsvFile.replaceAll(".csv", "(old).csv"));
		oldPreCsvFile1.renameTo(oldPreCsvFile2);

		File newPreCsvFile1 = new File(preCsvFile.replaceAll(".csv", "(new).csv"));
		File newPreCsvFile2 = new File(preCsvFile);
		newPreCsvFile1.renameTo(newPreCsvFile2);

		File oldPostCsvFile1 = new File(postCsvFile);
		File oldPostCsvFile2 = new File(postCsvFile.replaceAll(".csv", "(old).csv"));
		oldPostCsvFile1.renameTo(oldPostCsvFile2);

		File newPostCsvFile1 = new File(postCsvFile.replaceAll(".csv", "(new).csv"));
		File newPostCsvFile2 = new File(postCsvFile);
		newPostCsvFile1.renameTo(newPostCsvFile2);
		
	}
	
	private static void ConvertToMismatchCSV2(ComparisonDataDTO compDTO, List <ExtractTemplate> lstTemplate) throws IOException, InterruptedException {		
		String outputLocation = compDTO.getStrFolderPath()+"\\Mismatch(new2).csv";
		String preMismatchFile = compDTO.getStrFolderPath()+"\\Mismatch_TGT.csv";
		String postMismatchFile = compDTO.getStrFolderPath()+"\\Mismatch_SRC.csv";
		File file = new File(outputLocation);
		FileWriter fileWriter = new FileWriter(file, true);
		int releasePoint = 0;
		String key = "";

		ArrayList<String> columnNames = new ArrayList<String>();
		ArrayList<String> columnNames_from_template = new ArrayList<String>();
		columnNames_from_template.add("");
		
		for (int z = 0; z<lstTemplate.size(); z++) {
			if (!lstTemplate.get(z).getKeyColumns() && !lstTemplate.get(z).getSkipColumns()) {
				columnNames_from_template.add(lstTemplate.get(z).getColumnNames());
			}
		}
		fileWriter.write("Key Column Value~Column Name / Index~Source Data~Target Data");
		fileWriter.write("\r\n");
		
		
		
		BufferedReader PreBr_test = new BufferedReader(new InputStreamReader( new FileInputStream(preMismatchFile), "JISAutoDetect"));
		String line = "";
		String outputLocation_test = compDTO.getStrFolderPath()+"\\Mismatch_TGT(test).csv";

		while ((line = PreBr_test.readLine()) != null) {
			
		}
		
		
		try (BufferedReader PreBr = new BufferedReader(new InputStreamReader( new FileInputStream(preMismatchFile), "JISAutoDetect"));  BufferedReader PostBr = new BufferedReader(new InputStreamReader( new FileInputStream(postMismatchFile), "JISAutoDetect"))){
			String PreLine, PostLine;
			while ((PreLine = PreBr.readLine())!=null && (PostLine = PostBr.readLine())!=null) {
				boolean PreinQuotes = false;
				StringBuilder Presb = new StringBuilder();
				boolean PostinQuotes = false;
				StringBuilder Postsb = new StringBuilder();



				PreLine += "~\"dummy\"";
				PostLine += "~\"dummy\"";


//				String[] PreLine_split = PreLine.replaceAll("\"\"", "\" \"").split("~");
//				String[] PostLine_split = PostLine.replaceAll("\"\"", "\" \"").split("~");
				String[] PreLine_split = PreLine.split("~");
				String[] PostLine_split = PostLine.split("~");
//				System.out.println("Size: " + PreLine_split.length +" PreLine2: " + PreLine);
//				System.out.println("Size: " + PostLine_split.length + " PostLine2: " + PostLine);
				
				key = PreLine_split[0];
				for (int i = 1; i<PreLine_split.length-1; i++) {
					if (!PreLine_split[i].equals(PostLine_split[i])) {
//						System.out.println(PreLine_split[i] + " / " + PostLine_split[i] + " / " +PreLine_split[i].equals(PostLine_split[i]));
						fileWriter.write(key+"~"+ columnNames_from_template.get(i)+"~"+ PreLine_split[i]+"~"+PostLine_split[i]+"\r\n");	
						releasePoint++;
					}
				} 
//				System.out.println(columnNames_from_template);
				
				if (releasePoint>=1000) {
					releasePoint=0;
					fileWriter.close();
					try {
						fileWriter = new FileWriter(file, true);
					}catch(FileNotFoundException e ) {
						System.out.println("File is used by another process. Sleep 6 sec and open file again..");
						Thread.sleep(6000);
						try {
							fileWriter = new FileWriter(file, true);
						}catch(Exception error) {
							System.out.println("File is used by another process. Sleep 3 sec and open file again..");
							Thread.sleep(3000);
							fileWriter = new FileWriter(file, true);
						}
					}catch(Exception e) {
						System.out.println("Sleep 6 sec and open file again..");
						Thread.sleep(6000);
						try {
							fileWriter = new FileWriter(file, true);
						}catch(Exception error) {
							System.out.println("Sleep 3 sec and open file again..");
							Thread.sleep(3000);
							fileWriter = new FileWriter(file, true);
						}
					}

				}
				releasePoint++;

			

			}
			fileWriter.close();
//			System.out.println("Colunmn size: " + columnNames_from_template);

			
		}catch (Exception e) {

		}
	
	}

	
	private static void ConvertToMismatchCSV(ComparisonDataDTO compDTO, List <ExtractTemplate> lstTemplate) throws IOException, InterruptedException {		
//		Path oldFile = Paths.get(compDTO.getStrFolderPath()+"\\Mismatch.csv");
//		File newFile = new File(compDTO.getStrFolderPath()+"\\Mismatch(old file dont use).csv");


		
		String outputLocation = compDTO.getStrFolderPath()+"\\Mismatch(new).csv";
		String line = "";
		File file = new File(outputLocation);
		FileWriter fileWriter = new FileWriter(file, true);
		int releasePoint = 0;
		String key = "";
		ArrayList<String> preValues = new ArrayList<String>();
		ArrayList<String> postValues = new ArrayList<String>();
		ArrayList<String> columnNames = new ArrayList<String>();
		ArrayList<String> columnNames_from_template = new ArrayList<String>();
		columnNames_from_template.add("");
		for (int z = 0; z<lstTemplate.size(); z++) {
			if (!lstTemplate.get(z).getKeyColumns() && !lstTemplate.get(z).getSkipColumns()) {
				columnNames_from_template.add(lstTemplate.get(z).getColumnNames());
				//fileWriter.write(lstTemplate.get(z).getColumnNames() + ",");
//				System.out.println(lstTemplate.get(z).getColumnNames());
			}
		}

		
		Boolean isPre = true; 
//		BufferedReader br = new BufferedReader(new InputStreamReader( new FileInputStream(compDTO.getStrFolderPath()+"\\CompleteMismatch.csv"), "JISAutoDetect"));  
		BufferedReader br = new BufferedReader(new InputStreamReader( new FileInputStream(compDTO.getStrFolderPath()+"\\CompleteMismatch.csv"), "UTF-8"));  

//		BufferedReader br = new BufferedReader(new FileReader(compDTO.getStrFolderPath()+"\\CompleteMismatch.csv"));
		fileWriter.write("Key Column Value~Column Name / Index~Source Data~Target Data");
		fileWriter.write("\r\n");

		while ((line = br.readLine()) != null) {   //returns a Boolean value  

			String[] split_str = line.split("~");
			for (int i = 0; i<split_str.length; i++) {

				if (i == 0 && !split_str[0].equals("")) {
					key = split_str[0];
					if (isPre == true) {
						isPre = false;
					}else {
						isPre = true;
					}
				}
				if (split_str[i].contains("#")) {
					if (isPre==true) {
						preValues.add(split_str[i].replaceAll("#",""));
						try {
							columnNames.add(columnNames_from_template.get(i));
						}catch (Exception e) {
							columnNames.add(" ");
						}
					}else {
						postValues.add(split_str[i].replaceAll("#",""));
					}
				}
			}
			if (!preValues.isEmpty() && !postValues.isEmpty()) {
//				System.out.println(preValues.size() + " " + preValues);
//				System.out.println(postValues.size() + " " + postValues);
				for (int z = 0; z<preValues.size(); z++) {
					fileWriter.write(key+"~"+ columnNames.get(z)+"~"+ preValues.get(z)+"~"+postValues.get(z)+"\r\n");	
//					System.out.println(key+" ~ "+ columnNames.get(z)+" ~ "+ preValues.get(z)+" ~ "+postValues.get(z));
					releasePoint++;
				}
//				System.out.println(columnNames);
//				System.out.println(columnNames_from_template);
				key = "";
				columnNames = new ArrayList<String>();
				preValues = new ArrayList<String>();
				postValues = new ArrayList<String>();
			}
			if (releasePoint>=1000) {
				releasePoint=0;
				fileWriter.close();
				try {
					fileWriter = new FileWriter(file, true);
				}catch(FileNotFoundException e ) {
					System.out.println("File is used by another process. Sleep 6 sec and open file again..");
					Thread.sleep(6000);
					try {
						fileWriter = new FileWriter(file, true);
					}catch(Exception error) {
						System.out.println("File is used by another process. Sleep 3 sec and open file again..");
						Thread.sleep(3000);
						fileWriter = new FileWriter(file, true);
					}
				}catch(Exception e) {
					System.out.println("Sleep 6 sec and open file again..");
					Thread.sleep(6000);
					try {
						fileWriter = new FileWriter(file, true);
					}catch(Exception error) {
						System.out.println("Sleep 3 sec and open file again..");
						Thread.sleep(3000);
						fileWriter = new FileWriter(file, true);
					}
				}

			}
			releasePoint++;
		}
		fileWriter.close();
		
		//testing
//		String url = compDTO.getStrFolderPath()+"CompleteMismatch.csv";
////		try (CSVReader csvReader= new CSVReader(new InputStreamReader( new FileInputStream(compDTO.getStrFolderPath()+"\\Mismatch.csv"), "JISAutoDetect"))) {
//		try (CSVReader csvReader= new CSVReader(new InputStreamReader( new FileInputStream("C:\\Users\\chakinc\\Desktop\\Extracts_Comparison\\TestCase\\Extracts\\Commission-Extracts\\output_02_02_2024_12_31_30\\CompleteMismatch.csv"), "UTF-8"))) {
//
////		try (CSVReader csvReader= new CSVReader(new FileReader(url))) {
//			String[] nextRecord;
//			
//			while ((nextRecord = csvReader.readNext())!=null) {
//				for (String field : nextRecord) {
//					System.out.println(field+"\t");
//				}
//				System.out.println();
//			}
//		}catch (Exception e) {
//			e.printStackTrace(); 
//		}
//		
	}
	
	private static ComparisonDataDTO getDTO(String strExtractName, boolean BlnFetchExtractUsingSQL) throws IOException {
		String currentDirectory = System.getProperty("user.dir");
		ParseTemplate extractTemplate = new ParseTemplate();
		File file = new File("./Templates/"+strExtractName+".csv");  
		List <ExtractTemplate> lstTemplate = extractTemplate.getTemplateList(file);	
		ComparisonDataDTO compDTO = new ComparisonDataDTO();
		compDTO.setProjectId(1);
		compDTO.setProjectName("ING");
		compDTO.setTestSuiteName("Extracts");
		compDTO.setTestCaseName("Commission-Extracts");
		compDTO.setStepName(strExtractName);
		compDTO.setFilePath(currentDirectory+"\\TestCase\\"+compDTO.getTestSuiteName()+"\\");
		compDTO.setCompareFolderPath(currentDirectory+"\\ING_EXTRACT\\RepComp\\");
		compDTO.setStrFolderPath(currentDirectory+"\\TestCase\\"+compDTO.getTestSuiteName()+"\\"+compDTO.getTestCaseName());
		
		compDTO.setRequestType("TASQ");  
		compDTO.setStrCompType("FF");
		compDTO.setOperation("Comparison");
		
		Object strSrcValue = new String();
		Object strTgtValue = new String();
		
		if(BlnFetchExtractUsingSQL){
			compDTO.setStrSrcFileName(currentDirectory+"\\AppGenExtractFiles\\ExtractCSVFiles\\"+strExtractName+".csv");
			compDTO.setStrTgtFileName(currentDirectory+"\\ToolGenExtractFiles\\"+strExtractName+".csv");
			compDTO.setStrSrcFileName(currentDirectory+"\\AppGenExtractFiles\\ExtractCSVFiles\\"+strExtractName+".csv");
			compDTO.setStrTgtFileName(currentDirectory+"\\ToolGenExtractFiles\\"+strExtractName+".csv");			
			strSrcValue = currentDirectory+"\\AppGenExtractFiles\\ExtractCSVFiles\\"+strExtractName+".csv"; 
			strTgtValue = currentDirectory+"\\ToolGenExtractFiles\\"+strExtractName+".csv";
		}else{
			compDTO.setSrcInputFile(currentDirectory+"\\AppGenExtractFiles\\PRE\\CSV_Files\\"+strExtractName+".csv");
			compDTO.setTgtInputFile(currentDirectory+"\\AppGenExtractFiles\\POST\\CSV_Files\\"+strExtractName+".csv");

			compDTO.setStrSrcFileName(currentDirectory+"\\AppGenExtractFiles\\PRE\\CSV_Files\\"+strExtractName+".csv");
			compDTO.setStrTgtFileName(currentDirectory+"\\AppGenExtractFiles\\POST\\CSV_Files\\"+strExtractName+".csv");
			
			strSrcValue = currentDirectory+"\\AppGenExtractFiles\\PRE\\CSV_Files\\"+strExtractName+".csv"; 
			strTgtValue = currentDirectory+"\\AppGenExtractFiles\\POST\\CSV_Files\\"+strExtractName+".csv";
		}
		
		compDTO.setStrSrcValue(strSrcValue);
		compDTO.setStrTgtValue(strTgtValue);		
		
		compDTO.setStatusExcelFilepath(new File(currentDirectory+"\\TestCase\\Extracts\\"));
		compDTO.setStrSrcDelimeter(",");
		compDTO.setStrTgtDelimeter(",");
		String keyColumnsIndex = "";
		String skipColumnsIndex = "";	
	
		ArrayList<String> alActualSrcColumn = new ArrayList<String>();
		for (int i = 0;i<lstTemplate.size();i++) {				
			alActualSrcColumn.add(lstTemplate.get(i).getColumnNames());
			if(lstTemplate.get(i).getKeyColumns())
				keyColumnsIndex = keyColumnsIndex + (i+1) +",";
			if(!lstTemplate.get(i).getSkipColumns()){
				skipColumnsIndex = skipColumnsIndex + (i+1) +",";
			}
		}		
				
		compDTO.setStrSrcKeyClm(keyColumnsIndex);		
		compDTO.setStrTgtKeyClm(keyColumnsIndex);
		compDTO.setStrSrcColumn(skipColumnsIndex);
		compDTO.setStrTgtColumn(skipColumnsIndex);
		
		compDTO.setAlSrcColumns(alActualSrcColumn);
		compDTO.setAlTgtColumns(alActualSrcColumn);
		compDTO.setiSrcHeader(1);		
		compDTO.setiSrcFooter(0);
		compDTO.setiTgtHeader(1);
		compDTO.setiTgtFooter(0);
		Date date1 = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
		String formattedDate = sdf.format(date1);		
		long currentTimeMillisStart = date1.getTime();
		compDTO.setStrStartTime(formattedDate);
		compDTO.setStarttime(currentTimeMillisStart);	
		return compDTO;
	}
}
