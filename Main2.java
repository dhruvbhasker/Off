package main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cognizant.platinum.core.util.comparsionUtility.ComparisonLogic;
import com.cognizant.platinum.core.util.comparsionUtility.ComparisonPreparer;
import com.cognizant.platinum.core.util.comparsionUtility.DTO.ComparisonDataDTO;
import com.mlj.util.property.FilePropertiesDetail;

import fileutil.Convert2CSV;
import fileutil.ExtractTemplate;
import fileutil.ParseTemplate;

public class Main2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		dbutil.DB2Connect dbConnect = new dbutil.DB2Connect();
		FilePropertiesDetail extractFileDetails = new FilePropertiesDetail();		
//		Connection con = null;		
		String[] strExtractName = extractFileDetails.getStrExtractName().split(";");		
	      
		try {
//				con = dbConnect.connectDB2DB(extractFileDetails.getStrDBHostName(),extractFileDetails.getStrDBPortNumber(),extractFileDetails.getStrDBSchema(),extractFileDetails.getStrDBUserName(),extractFileDetails.getStrDBPassword());
				Convert2CSV appGeneratedCSV = new Convert2CSV();
				boolean BlnFetchExtractUsingSQL = extractFileDetails.isBlnFetchExtractUsingSQL();
				for(int index=0;index<strExtractName.length;index++){				
					
//					if(BlnFetchExtractUsingSQL){ // To check whether using SQL query or not
//						String sql = new String(Files.readAllBytes(Paths.get("./Templates/"+strExtractName[index]+".sql")));  
//						sql = sql.replace("PROCESSING~DATE", extractFileDetails.getStrprocessingDate());
//						boolean fileGen = 	dbConnect.extractGenerationFromQuery(con, strExtractName[index], sql);
//						if(fileGen)
//						{
//							System.out.println("Tool Generated Extract File >> "+strExtractName[index]);
//						}else{
//							System.out.println("Tool Not Generated >> "+strExtractName[index]);
//						}
//						appGeneratedCSV.convertCSVFromFile(strExtractName[index]);
//					}else{
////						appGeneratedCSV.convertCSVFromFile(strExtractName[index],"PRE");
////						appGeneratedCSV.convertCSVFromFile(strExtractName[index],"POST");						
//					}						
				ComparisonDataDTO compDTO = getDTO(strExtractName[index], BlnFetchExtractUsingSQL);
				ComparisonLogic comp = new ComparisonLogic(System.getProperty("user.dir")+"\\TestCase\\"+compDTO.getTestSuiteName()+"\\"+compDTO.getTestCaseName());		
				comp.compareData(compDTO);
				}
//				con.close();
				
				
				
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SQLException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} // UAT
		}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
		compDTO.setCompareFolderPath("C:\\Users\\tpandia\\workspace_Luna\\ING_EXTRACT\\RepComp\\");
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
//		compDTO.setiSrcHeader(0);	
		compDTO.setiSrcFooter(0);
//        compDTO.setiTgtHeader(0);
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
