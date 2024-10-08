
package main;

import java.util.*;
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
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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
            Convert2CSV appGeneratedCSV = new Convert2CSV();
            boolean BlnFetchExtractUsingSQL = extractFileDetails.isBlnFetchExtractUsingSQL();
            
            // Find files in PRE and POST subdirectories
            List<File> preFiles = findAllFiles("./AppGenExtractFiles/PRE/CSV_Files");
            List<File> postFiles = findAllFiles("./AppGenExtractFiles/POST/CSV_Files");

            for (File preFile : preFiles) {
                String fileName = preFile.getName();
                
                
                
                
                	
                
                	
                
                
                // Find corresponding POST file by name
                File postFile = postFiles.stream()
                        .filter(file -> file.getName().equals(fileName))
                        .findFirst()
                        .orElse(null);
                String beforeFirstDot = fileName.split("\\.")[0];
                String finalFileName;
                if(beforeFirstDot != null && !beforeFirstDot.isEmpty()) {
                	finalFileName = beforeFirstDot;
                } else {
                	finalFileName = fileName;
                }
                	
                
                if (postFile != null) {
                    System.out.println("Comparing files: " + preFile.getPath() + " with " + postFile.getPath());
                    
                    // Fetch DTO for comparison
                    ComparisonDataDTO compDTO = getDTO(finalFileName, BlnFetchExtractUsingSQL);
                    

                    // Set the file paths for comparison
                    compDTO.setSrcInputFile(preFile.getAbsolutePath());
                    compDTO.setTgtInputFile(postFile.getAbsolutePath());

                    ComparisonLogic comp = new ComparisonLogic(System.getProperty("user.dir") + "\\TestCase\\" + compDTO.getTestSuiteName() + "\\" + compDTO.getTestCaseName());
                    comp.compareData(compDTO);

                    // Generate mismatch CSV
                    ParseTemplate extractTemplate = new ParseTemplate();
                    ConvertToMismatchCSV(compDTO, extractTemplate.getTemplateList(new File("./Templates/" + finalFileName + ".csv")));
                } else {
                    System.out.println("No matching POST file for: " + preFile.getPath());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	 private static List<File> findAllFiles(String directoryPath) throws IOException {
	        try (Stream<Path> paths = Files.walk(Paths.get(directoryPath), FileVisitOption.FOLLOW_LINKS)) {
	            return paths.filter(Files::isRegularFile)
	                        .map(Path::toFile)
	                        .collect(Collectors.toList());
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
			compDTO.setSrcInputFile(currentDirectory+"\\AppGenExtractFiles\\PRE\\CSV_Files\\dh\\"+strExtractName+".csv");
			compDTO.setTgtInputFile(currentDirectory+"\\AppGenExtractFiles\\POST\\CSV_Files\\dh\\"+strExtractName+".csv");

			compDTO.setStrSrcFileName(currentDirectory+"\\AppGenExtractFiles\\PRE\\CSV_Files\\dh\\"+strExtractName+".csv");
			compDTO.setStrTgtFileName(currentDirectory+"\\AppGenExtractFiles\\POST\\CSV_Files\\dh\\"+strExtractName+".csv");
			
			strSrcValue = currentDirectory+"\\AppGenExtractFiles\\PRE\\CSV_Files\\dh\\"+strExtractName+".csv"; 
			strTgtValue = currentDirectory+"\\AppGenExtractFiles\\POST\\CSV_Files\\dh\\"+strExtractName+".csv";
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
