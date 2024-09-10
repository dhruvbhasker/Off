package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.cognizant.platinum.core.util.comparsionUtility.ComparisonLogic;
import com.cognizant.platinum.core.util.comparsionUtility.DTO.ComparisonDataDTO;

import fileutil.Convert2CSV;
import fileutil.ExtractTemplate;
import fileutil.ParseTemplate;

public class Main4 {

    public static void main(String[] args) throws InterruptedException {
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
                
                if (postFile != null) {
                    System.out.println("Comparing files: " + preFile.getPath() + " with " + postFile.getPath());
                    
                    // Fetch DTO for comparison
                    ComparisonDataDTO compDTO = getDTO(fileName, BlnFetchExtractUsingSQL);

                    // Set the file paths for comparison
                    compDTO.setSrcInputFile(preFile.getAbsolutePath());
                    compDTO.setTgtInputFile(postFile.getAbsolutePath());

                    ComparisonLogic comp = new ComparisonLogic(System.getProperty("user.dir") + "\\TestCase\\" + compDTO.getTestSuiteName() + "\\" + compDTO.getTestCaseName());
                    comp.compareData(compDTO);

                    // Generate mismatch CSV
                    ParseTemplate extractTemplate = new ParseTemplate();
                    ConvertToMismatchCSV(compDTO, extractTemplate.getTemplateList(new File("./Templates/" + fileName + ".csv")));
                } else {
                    System.out.println("No matching POST file for: " + preFile.getPath());
                }
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to recursively find all files in a directory
    private static List<File> findAllFiles(String directoryPath) throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(directoryPath), FileVisitOption.FOLLOW_LINKS)) {
            return paths.filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .collect(Collectors.toList());
        }
    }

    private static void ConvertToMismatchCSV(ComparisonDataDTO compDTO, List<ExtractTemplate> lstTemplate) throws IOException, InterruptedException {
        String outputLocation = compDTO.getStrFolderPath() + "\\Mismatch(new).csv";
        String line;
        File file = new File(outputLocation);
        FileWriter fileWriter = new FileWriter(file, true);
        int releasePoint = 0;
        String key = "";
        ArrayList<String> preValues = new ArrayList<>();
        ArrayList<String> postValues = new ArrayList<>();
        ArrayList<String> columnNames = new ArrayList<>();
        ArrayList<String> columnNames_from_template = new ArrayList<>();
        columnNames_from_template.add("");
        
        for (ExtractTemplate template : lstTemplate) {
            if (!template.getKeyColumns() && !template.getSkipColumns()) {
                columnNames_from_template.add(template.getColumnNames());
            }
        }

        Boolean isPre = true;
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(compDTO.getStrFolderPath() + "\\CompleteMismatch.csv"), StandardCharsets.UTF_8));
        fileWriter.write("Key Column Value~Column Name / Index~Source Data~Target Data");
        fileWriter.write("\r\n");

        while ((line = br.readLine()) != null) {
            String[] split_str = line.split("~");
            for (int i = 0; i < split_str.length; i++) {
                if (i == 0 && !split_str[0].equals("")) {
                    key = split_str[0];
                    isPre = !isPre;
                }
                if (split_str[i].contains("#")) {
                    if (isPre) {
                        preValues.add(split_str[i].replaceAll("#", ""));
                        try {
                            columnNames.add(columnNames_from_template.get(i));
                        } catch (Exception e) {
                            columnNames.add(" ");
                        }
                    } else {
                        postValues.add(split_str[i].replaceAll("#", ""));
                    }
                }
            }
            if (!preValues.isEmpty() && !postValues.isEmpty()) {
                for (int z = 0; z < preValues.size(); z++) {
                    fileWriter.write(key + "~" + columnNames.get(z) + "~" + preValues.get(z) + "~" + postValues.get(z) + "\r\n");
                    releasePoint++;
                }
                key = "";
                columnNames = new ArrayList<>();
                preValues = new ArrayList<>();
                postValues = new ArrayList<>();
            }
            if (releasePoint >= 1000) {
                fileWriter.close();
                Thread.sleep(6000);
                fileWriter = new FileWriter(file, true);
                releasePoint = 0;
            }
            releasePoint++;
        }
        fileWriter.close();
    }

    private static ComparisonDataDTO getDTO(String strExtractName, boolean BlnFetchExtractUsingSQL) throws IOException {
        String currentDirectory = System.getProperty("user.dir");
        ParseTemplate extractTemplate = new ParseTemplate();
        File file = new File("./Templates/" + strExtractName + ".csv");
        List<ExtractTemplate> lstTemplate = extractTemplate.getTemplateList(file);
        ComparisonDataDTO compDTO = new ComparisonDataDTO();
        
        compDTO.setProjectId(1);
        compDTO.setProjectName("ING");
        compDTO.setTestSuiteName("Extracts");
        compDTO.setTestCaseName("Commission-Extracts");
        compDTO.setStepName(strExtractName);
        compDTO.setFilePath(currentDirectory + "\\TestCase\\" + compDTO.getTestSuiteName() + "\\");
        compDTO.setCompareFolderPath(currentDirectory + "\\ING_EXTRACT\\RepComp\\");
        compDTO.setStrFolderPath(currentDirectory + "\\TestCase\\" + compDTO.getTestSuiteName() + "\\" + compDTO.getTestCaseName());

        if (BlnFetchExtractUsingSQL) {
            compDTO.setSrcInputFile(currentDirectory + "\\AppGenExtractFiles\\ExtractCSVFiles\\" + strExtractName + ".csv");
            compDTO.setTgtInputFile(currentDirectory + "\\ToolGenExtractFiles\\" + strExtractName + ".csv");
        } else {
            compDTO.setSrcInputFile(currentDirectory + "\\AppGenExtractFiles\\PRE\\CSV_Files\\" + strExtractName + ".csv");
            compDTO.setTgtInputFile(currentDirectory + "\\AppGenExtractFiles\\POST\\CSV_Files\\" + strExtractName + ".csv");
        }

        String keyColumnsIndex = "";
        String skipColumnsIndex = "";
        ArrayList<String> alActualSrcColumn = new ArrayList<>();

        for (int i = 0; i < lstTemplate.size(); i++) {
            alActualSrcColumn.add(lstTemplate.get(i).getColumnNames());
            if (lstTemplate.get(i).getKeyColumns())
                keyColumnsIndex = keyColumnsIndex + (i + 1) + ",";
            if (!lstTemplate.get(i).getSkipColumns())
                skipColumnsIndex = skipColumnsIndex + (i + 1) + ",";
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
        
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        String formattedDate = sdf.format(new Date());
        compDTO.setStrStartTime(formattedDate);
        compDTO.setStarttime(System.currentTimeMillis());

        return compDTO;
    }
}
