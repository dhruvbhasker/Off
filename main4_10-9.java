import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ComparisonTool {

    // Method to process and compare files
    public static void processFiles(Path preFilePath, Path postFilePath, String templatePath) throws IOException {
        // Load the template and comparison logic
        ComparisonDataDTO compDTO = getDTO(preFilePath.getFileName().toString().replace(".csv", ""), false);

        System.out.println("Processing files:");
        System.out.println("Pre file: " + preFilePath);
        System.out.println("Post file: " + postFilePath);
        System.out.println("Template: " + templatePath);

        // Implement your comparison logic here...
        ConvertToMismatchCSV2(compDTO, new ParseTemplate().getTemplateList(new File(templatePath)));
    }

    // Method to get all files with a specific extension in a directory (including subdirectories)
    public static List<Path> getAll(String directory, String fileExtension) throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(directory))) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(fileExtension))
                    .collect(Collectors.toList());
        }
    }

    // Method to find a matching file in a directory (including subdirectories)
    public static Path findMatchingFile(Path directory, String fileName) throws IOException {
        try (Stream<Path> paths = Files.walk(directory)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().equals(fileName))
                    .findFirst()
                    .orElse(null);
        }
    }

    // Method to fetch the ComparisonDataDTO object based on template and other details
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

        compDTO.setRequestType("TASQ");
        compDTO.setStrCompType("FF");
        compDTO.setOperation("Comparison");

        Object strSrcValue;
        Object strTgtValue;

        if (BlnFetchExtractUsingSQL) {
            compDTO.setStrSrcFileName(currentDirectory + "\\AppGenExtractFiles\\ExtractCSVFiles\\" + strExtractName + ".csv");
            compDTO.setStrTgtFileName(currentDirectory + "\\ToolGenExtractFiles\\" + strExtractName + ".csv");
            strSrcValue = currentDirectory + "\\AppGenExtractFiles\\ExtractCSVFiles\\" + strExtractName + ".csv";
            strTgtValue = currentDirectory + "\\ToolGenExtractFiles\\" + strExtractName + ".csv";
        } else {
            compDTO.setSrcInputFile(currentDirectory + "\\AppGenExtractFiles\\PRE\\CSV_Files\\" + strExtractName + ".csv");
            compDTO.setTgtInputFile(currentDirectory + "\\AppGenExtractFiles\\POST\\CSV_Files\\" + strExtractName + ".csv");

            compDTO.setStrSrcFileName(currentDirectory + "\\AppGenExtractFiles\\PRE\\CSV_Files\\" + strExtractName + ".csv");
            compDTO.setStrTgtFileName(currentDirectory + "\\AppGenExtractFiles\\POST\\CSV_Files\\" + strExtractName + ".csv");

            strSrcValue = currentDirectory + "\\AppGenExtractFiles\\PRE\\CSV_Files\\" + strExtractName + ".csv";
            strTgtValue = currentDirectory + "\\AppGenExtractFiles\\POST\\CSV_Files\\" + strExtractName + ".csv";
        }

        compDTO.setStrSrcValue(strSrcValue);
        compDTO.setStrTgtValue(strTgtValue);

        compDTO.setStatusExcelFilepath(new File(currentDirectory + "\\TestCase\\Extracts\\"));
        compDTO.setStrSrcDelimeter(",");
        compDTO.setStrTgtDelimeter(",");

        String keyColumnsIndex = "";
        String skipColumnsIndex = "";

        ArrayList<String> alActualSrcColumn = new ArrayList<>();
        for (int i = 0; i < lstTemplate.size(); i++) {
            alActualSrcColumn.add(lstTemplate.get(i).getColumnNames());
            if (lstTemplate.get(i).getKeyColumns())
                keyColumnsIndex = keyColumnsIndex + (i + 1) + ",";
            if (!lstTemplate.get(i).getSkipColumns()) {
                skipColumnsIndex = skipColumnsIndex + (i + 1) + ",";
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

    // Comparison logic method
    private static void ConvertToMismatchCSV2(ComparisonDataDTO compDTO, List<ExtractTemplate> lstTemplate) throws IOException, InterruptedException {
        // Placeholder for the detailed comparison logic, this method can include CSV comparison
        // Your logic should go here.
        System.out.println("Comparing files based on the provided template and generating mismatch report...");
        // Implement the comparison logic here based on compDTO and lstTemplate
    }

    public static void main(String[] args) {
        String preDirectory = "./CSV_Files/PRE";
        String postDirectory = "./CSV_Files/POST";
        String templateDirectory = "./Templates";
        String fileExtension = ".csv";

        try {
            List<Path> preFiles = getAll(preDirectory, fileExtension);
            for (Path preFile : preFiles) {
                // Derive the corresponding post file and template file names
                String fileName = preFile.getFileName().toString();
                Path postFile = findMatchingFile(Paths.get(postDirectory), fileName);
                Path templateFile = findMatchingFile(Paths.get(templateDirectory), fileName);

                if (postFile != null && templateFile != null) {
                    processFiles(preFile, postFile, templateFile.toString());
                } else {
                    System.out.println("Matching post or template file not found for: " + fileName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
