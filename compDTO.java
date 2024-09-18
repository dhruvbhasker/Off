private static ComparisonDataDTO getDTO(String strExtractName, boolean BlnFetchExtractUsingSQL) throws IOException {
    String currentDirectory = System.getProperty("user.dir");
    ParseTemplate extractTemplate = new ParseTemplate();
    
    // File pointing to the template CSV file
    File templateFile = new File("./Templates/" + strExtractName + ".csv");
    
    // Get the template list using the ParseTemplate method
    List<ExtractTemplate> lstTemplate = extractTemplate.getTemplateList(templateFile); // Ensure the type matches
    
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

    // Handling the columns, key columns, and skipped columns
    ArrayList<String> alActualSrcColumn = new ArrayList<>();
    StringBuilder keyColumnsIndex = new StringBuilder();
    StringBuilder skipColumnsIndex = new StringBuilder();

    for (int i = 0; i < lstTemplate.size(); i++) {
        ExtractTemplate template = lstTemplate.get(i);
        alActualSrcColumn.add(template.getColumnNames());
        
        if (template.getKeyColumns()) {
            keyColumnsIndex.append(i + 1).append(",");
        }

        if (!template.getSkipColumns()) {
            skipColumnsIndex.append(i + 1).append(",");
        }
    }

    compDTO.setStrSrcKeyClm(keyColumnsIndex.toString());
    compDTO.setStrTgtKeyClm(keyColumnsIndex.toString());
    compDTO.setStrSrcColumn(skipColumnsIndex.toString());
    compDTO.setStrTgtColumn(skipColumnsIndex.toString());

    compDTO.setAlSrcColumns(alActualSrcColumn);
    compDTO.setAlTgtColumns(alActualSrcColumn);
    compDTO.setiSrcHeader(1);
    compDTO.setiSrcFooter(0);
    compDTO.setiTgtHeader(1);
    compDTO.setiTgtFooter(0);

    Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
    String formattedDate = sdf.format(date);
    long currentTimeMillisStart = date.getTime();

    compDTO.setStrStartTime(formattedDate);
    compDTO.setStarttime(currentTimeMillisStart);

    return compDTO;
}
