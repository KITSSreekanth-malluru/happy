package com.castorama.integration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.TransactionManager;

import atg.adapter.gsa.xml.TemplateParser;
import atg.nucleus.GenericService;
import atg.repository.Repository;
import atg.xml.XMLFileEntityResolver;
import atg.xml.tools.DefaultXMLToolsFactory;
import atg.xml.tools.XMLToDOMParser;
import atg.xml.tools.XMLToolsFactory;

import com.castorama.integration.util.MiscUtils;

public class IntegrationBase extends GenericService {

  public static final int DEFAULT_TRANSACTION_TIMEOUT = 10800;
  
  protected static final String PREFIX_TEMP_FILE = "tmp_file_";
  protected static final String ENCODING = "UTF-8";
  
  // repository
  private Repository repository = null;

  /**
   * The repository that holds the information
   * 
   * @return the repository that holds the information
   */
  public Repository getRepository() {
    return repository;
  }

  /**
   * @param repository
   *            set the repository that holds the information
   */
  public void setRepository(Repository repository) {
    this.repository = repository;
  }

  // Transaction manager
  private TransactionManager transactionManager = null;

  /**
   * The transaction manager
   * 
   * @return transaction manager
   */
  public TransactionManager getTransactionManager() {
    return this.transactionManager;
  }

  /**
   * @param transactionManager
   */
  public void setTransactionManager(TransactionManager transactionManager) {
    this.transactionManager = transactionManager;
  }

  // timeout
  private Integer transactionTimeout;

  /**
   * The timeout for current transaction
   * 
   * @return timeout
   */
  public Integer getTransactionTimeout() {
    return transactionTimeout;
  }

  /**
   * Set timeout of transaction
   * 
   * @param transactionTimeout
   */
  public void setTransactionTimeout(Integer transactionTimeout) {
    this.transactionTimeout = transactionTimeout;
  }

  private String rootDir;

  /**
   * The home directory where files will be processed
   * 
   * @return home directory where files will be processed
   */
  public String getRootDir() {
    return rootDir;
  }

  /**
   * @param pathRootDir
   *            set home directory where files will be processed
   */
  public void setRootDir(String rootDir) {
    this.rootDir = rootDir;
  }

  protected void moveFileIn(File file, File dir) throws Exception {
    File archFile = new File(dir, file.getName());
    MiscUtils.copyFile(file, archFile);

    file.delete();
  }

  /**
   * get root folder
   * 
   * @return File is root folder
   * @throws FileNotFoundException
   *             if root folder not exists or not folder
   */
  protected File getRootFolder() throws FileNotFoundException {
    String rootFolder = getRootDir();
    File rootDir = new File(rootFolder);
    checkDirectory(rootDir);

    return rootDir;
  }

  protected String getFileName(File file) {
    String name = file.getName();
    if (name.lastIndexOf(".") == -1) {
      return name;
    }
    return name.substring(0, name.lastIndexOf("."));
  }

  protected void checkDirectory(File dir) throws FileNotFoundException {
    if (!dir.isDirectory() || !dir.exists()) {
      throw new FileNotFoundException(dir.getAbsolutePath() + " not exist.");
    }
  }

  /**
   * Check mandatory folders - root and input
   * 
   * @throws FileNotFoundException
   *             if mandatory folders not exists
   */
  protected void checkMandatoryFolders() throws FileNotFoundException {
    File rootDir = getRootFolder();

    File inputDir = new File(rootDir, Constants.INPUT_FOLDER);
    checkDirectory(inputDir);
  }


  /**
   * Get mandatory folders - input, working, error.
   * 
   */
  protected Map<String, File> getMandatoryFolders() {
    Map<String, File> result = new HashMap<String, File>();
    File rootDir;
    try {
      rootDir = getRootFolder();
    } catch (FileNotFoundException e) {
      rootDir = null;
    }
    if ( null != rootDir ){
      result = getFoldersMap(rootDir, Constants.INPUT_FOLDER, result);
      if ( null != result ) {
        result = getFoldersMap(rootDir, Constants.WORKING_FOLDER, result);
      }
      if ( null != result ) {
        result = getFoldersMap(rootDir, Constants.ERROR_FOLDER, result);
      }
    } else {
      result = null;
    }
    return result;
  }

  /**
   * Get folder if folder is folder and exist.
   */
  protected Map<String, File> getFoldersMap(File root, String folder, Map<String, File> folders) {
    Map<String, File> result;
    File dir = new File(root, folder);
    try {
      checkDirectory(dir);
      folders.put(folder, dir);
      result = folders;
    } catch (FileNotFoundException e) {
      result = null;
    }
    return result;
  }

  protected File getWorkingDir() throws FileNotFoundException {
    File workingDir = new File(getRootFolder(), Constants.WORKING_FOLDER);
    if (!workingDir.exists()) {
      workingDir.mkdir();
    }
    checkDirectory(workingDir);

    return workingDir;
  }

  protected File getErrorDir() throws FileNotFoundException {
    File errorDir = new File(getRootFolder(), Constants.ERROR_FOLDER);
    if (!errorDir.exists()) {
      errorDir.mkdir();
    }
    checkDirectory(errorDir);

    return errorDir;
  }
  
  protected File getArchiveDir() throws FileNotFoundException {
    File archDir = new File(getRootFolder(), Constants.ARCHIVE_FOLDER);
    if (!archDir.exists()) {
      archDir.mkdir();
    }
    checkDirectory(archDir);

    return archDir;
  }

  protected File getDir(String dirName) throws FileNotFoundException {
    File dir = new File(getRootFolder(), dirName);
    if (!dir.exists()) {
      dir.mkdir();
    }
    checkDirectory(dir);

    return dir;
  }

  protected File getWorkingFile(File inputFile, File workingDir) throws Exception {
    if (inputFile == null || !inputFile.isFile() || !inputFile.exists()) {
      if (isLoggingWarning()) {
        logWarning("Invalid input file: " + inputFile);
      }
      return null;
    }

    String name = getFileName(inputFile);
    String workName = name + "_" + Constants.DATE_FORMAT_ARCHIVE.format(new Date())
        + inputFile.getName().substring(name.length(), inputFile.getName().length());
    File workFile = new File(workingDir, workName);

    MiscUtils.copyFile(inputFile, workFile);

    inputFile.delete();

    return workFile;
  }

  protected File getWorkingFile(File inputFile) throws Exception {
    File workingDir = getWorkingDir();
    return getWorkingFile(inputFile, workingDir);
  }

  /**
   * Get source files for upload according to filter
   * 
   * @param prefix
   *            the filter for select
   * @return array files
   * @throws FileNotFoundException
   *             if root folder not exists
   */
  protected File[] getUploadFiles(final String prefix) throws FileNotFoundException {
    final File[] files;
    final File inputDir = new File(getRootFolder(), Constants.INPUT_FOLDER);
    files = inputDir.listFiles(new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return prefix != null ? name.startsWith(prefix) : true;
      }
    });

    return files;
  }

  /**
   * Get temporary files for upload according to filter
   * 
   * @param prefix
   *            the filter for select
   * @return array files
   * @throws FileNotFoundException
   *             if root folder not exists
   */
  protected File[] getTempFiles(File workingDir, final String prefixTmpFiles) {
    final File[] files;
    files = workingDir.listFiles(new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return prefixTmpFiles != null ? name.startsWith(prefixTmpFiles) : true;
      }
    });

    return files;
  }

  protected XMLToDOMParser getXMLToDOMParser(XMLToolsFactory pFactory) {
    XMLToolsFactory factory = pFactory;
    if (pFactory == null)
      DefaultXMLToolsFactory.getInstance();
    XMLToDOMParser parser = factory.createXMLToDOMParser();
    parser.setEntityResolver(new XMLFileEntityResolver());
    return parser;
  }

  protected static final String TAG_GSA_TEMPLATE = "<gsa-template>";
  protected static final String END_TAG_GSA_TEMPLATE = "</gsa-template>";
  protected static final String SING_NULL = "\0";
  protected static final String END_TAG_ADD_ITEM = "</" + TemplateParser.TAG_ADD_ITEM + ">";

  protected static final String XML_HEADER = new StringBuilder().append(
      MessageFormat.format(TemplateParser.XML_FILE_HEADER, ENCODING)).append("<").append(
      TemplateParser.TAG_IMPORT_ITEMS).append(">").append(TemplateParser.NEW_LINE).toString();

  protected static final String XML_FOOTER = new StringBuilder().append("</").append(TemplateParser.TAG_IMPORT_ITEMS)
      .append(">").append(TemplateParser.XML_FILE_FOOTER).toString();

  protected List<String> parseFluxXml(File workingFile) throws Exception {
    return parseFluxXml(workingFile, PREFIX_TEMP_FILE);
  }

  protected List<String> parseFluxXml(File workingFile, String prefixTmpFile) throws Exception {
    if (workingFile == null) {
      if (isLoggingDebug()) {
        logDebug("Source file is null.");
      }
      return null;
    }

    List<String> tmpFileNames = new ArrayList<String>();
    BufferedReader reader = null;
    File tmpFile = null;
    PrintWriter writer = null;

    int cnt = 10000;
    try {

      reader = new BufferedReader(new FileReader(workingFile));
      String line;
      boolean isItem = false;
      String xmlId = "";
      while ((line = reader.readLine()) != null) {
        if (line.indexOf("<XML") != -1) {
          xmlId = getXmlId(line);
        } else if (line.indexOf(TAG_GSA_TEMPLATE) != -1) {
          // create file
          cnt++;
          if (xmlId.length() > 0) {
            xmlId = xmlId + "_";
          }
          String tmpName = xmlId + prefixTmpFile + Integer.toString(cnt) + "_"
              + Constants.DATE_FORMAT_ARCHIVE.format(new Date()) + ".xml";

          tmpFile = new File(workingFile.getParentFile(), tmpName);

          tmpFileNames.add(tmpName);

          writer = new PrintWriter(tmpFile, "UTF-8");
          writer.write(XML_HEADER);
          isItem = false;
        } else if (line.indexOf(END_TAG_GSA_TEMPLATE) != -1) {
          // close file
          writer.println(XML_FOOTER);
          writer.flush();
          writer.close();
          xmlId = "";
          isItem = false;
        } else if (line.indexOf(TemplateParser.TAG_ADD_ITEM) != -1) {
          isItem = true;
        }

        if (isItem) {
          //line = line.replaceAll(SING_NULL, TemplateParser.NEW_LINE);
          //writer.println(line);
          line = line.replaceAll(SING_NULL, "");
          writer.write(line);
        }

        if (line.indexOf(END_TAG_ADD_ITEM) != -1) {
          isItem = false;
        }
      }
    } finally {
      if (reader != null) {
        reader.close();
      }

      if (writer != null) {
        writer.flush();
        writer.close();
      }
    }

    return tmpFileNames;
  }

    private String getXmlId (String line) {
      if (line.indexOf("Id") != -1) {
            String mLine = line.substring(0, line.indexOf("Repository"));
            return mLine.substring(mLine.indexOf("\"")+1,mLine.lastIndexOf("\""));
      }
      return "";
    }

}
