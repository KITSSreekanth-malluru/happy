package com.castorama.integration.codepostal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import com.castorama.integration.Constants;
import com.castorama.integration.IntegrationBase;
import com.castorama.integration.util.MiscUtils;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

public class ImportCodePostalManager extends IntegrationBase {

    /** SPLITTER_SEPARATOR constant */
    private static final String SPLITTER_SEPARATOR = ";";
    private static final String PROPERTY_NAME_CODE_POSTAL = "code_postal";
    private static final String PROPERTY_NAME_VILLE = "ville";
    private File inputDir;
    private File workingDir;
    private File archiveDir;
    private File errorDir;

    private String mItemDescriptorName;
    private String mType;
    private boolean create;

    public void importPostalCodes() {
        long currentTimeMillis = System.currentTimeMillis();
        if (isLoggingDebug()) {
            logDebug("start - "+ImportCodePostalManager.class.getName()+ " - importPostalCodes");
        }
        PrintWriter logWriter = null;
        try {
            File workingDir = getWorkingDir();
            checkFolders();

            File workingFile = getWorkingFile();
            if (workingFile == null) {
                if (isLoggingError()) {
                    logError("input file is not found in folder: "
                            + inputDir.getAbsolutePath());
                }
                return;
            }
            if (isLoggingDebug()) {
                logDebug("processing: " + workingFile);
            }
            File statusFile = new File(workingDir, getFileName(workingFile)
                    + ".log");
            logWriter = new PrintWriter(statusFile);
            logWriter.println("Data file: " + workingFile.getName());
            parseCsv(workingFile, logWriter);
        } catch (Exception e) {
            if (isLoggingError()) {
                logError("Exception occured: ", e);
            }
        } finally {
            if (logWriter != null){
                logWriter.close();
            }
        }
        
        if (isLoggingDebug()) {
            logDebug("finish - " + ImportCodePostalManager.class.getName() + " - importPostalCodes");
        }
        logWarning("import " + getItemDescriptorName() + " complete : "
                + (System.currentTimeMillis() - currentTimeMillis));

    }

    private void checkFolders() throws FileNotFoundException {
        File rootDir = getRootFolder();

        inputDir = new File(rootDir, Constants.INPUT_FOLDER);
        checkDirectory(inputDir);

        workingDir = new File(rootDir, Constants.WORKING_FOLDER);
        if (!workingDir.exists()) {
            workingDir.mkdir();
        }
        checkDirectory(workingDir);

        archiveDir = new File(rootDir, Constants.ARCHIVE_FOLDER);
        if (!archiveDir.exists()) {
            archiveDir.mkdir();
        }
        checkDirectory(archiveDir);

        errorDir = new File(rootDir, Constants.ERROR_FOLDER);
        if (!errorDir.exists()) {
            errorDir.mkdir();
        }
        checkDirectory(errorDir);
    }

    //
    private File getWorkingFile() throws Exception {
        File[] inpFiles = inputDir.listFiles();

        File inpFile = null;
        if (inpFiles == null || inpFiles.length == 0) {
            return null;
        } else {
            for (File f : inpFiles) {
                if (f.isFile() && f.exists()) {
                    inpFile = f;
                    break;
                }
            }
        }

        String name = getFileName(inpFile);
        String workName = name
                + "_"
                + Constants.DATE_FORMAT_ARCHIVE.format(new Date())
                + inpFile.getName().substring(name.length(),
                        inpFile.getName().length());
        File workFile = new File(workingDir, workName);

        MiscUtils.copyFile(inpFile, workFile);

        inpFile.delete();

        return workFile;
    }

    private void parseCsv(File workingFile, PrintWriter log) {
        if (workingFile == null) {
            if (isLoggingDebug()) {
                logDebug("Source file is null.");
            }
            return;
        }
        List<RepositoryItem> tempList = new ArrayList<RepositoryItem>();
        String tmp = null;
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            fis = new FileInputStream(workingFile);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            MutableRepository mr = (MutableRepository) getRepository();
            int count = 0;
            while ((tmp = br.readLine()) != null) {
                try {
                    if (count != 0) {
                        MutableRepositoryItem ri = getRepositoryItem(mr, tmp);
                        if (isCreate()) {
                            mr.addItem(ri);
                        }
                    }
                    count++;
                } catch (RepositoryException e) {
                    if (isLoggingError()) {
                        logError("Can't import item from : " + tmp, e);
                        log.println("Can't import item from : " + tmp + "\n"
                                + e);
                    }
                } catch (ParseException e) {
                    if (isLoggingError()) {
                        logError("Can't parse item from : " + tmp, e);
                        log.println("Can't parse item from : " + tmp
                                        + "\n" + e);
                    }
                } catch (IndexOutOfBoundsException e) {
                    if (isLoggingError()) {
                        logError("Can't create item from :" + tmp, e);
                        log.println("Can't create item from :" + tmp+ "\n"
                                + e);
                    }
                }
            }
            if (isLoggingDebug()) {
                logDebug("parsed " + count + " lines");
            }
            log.println("parsed " + count + " lines");
        } catch (IOException e) {
            if (isLoggingError()) {
                logError(e);
                log.println(e);
            }
        } finally {
            if (fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    if (isLoggingError()){
                        logError(e);
                    }
                }
            }
            if (isr != null){
                try {
                    isr.close();
                } catch (IOException e) {
                    if (isLoggingError()){
                        logError(e);
                    }
                }
            }
            if (br != null){
                try {
                    br.close();
                } catch (IOException e) {
                    if (isLoggingError()){
                        logError(e);
                    }
                }
            }
        }

    }

    private MutableRepositoryItem getRepositoryItem(MutableRepository mr,
            String str) throws RepositoryException, ParseException, IndexOutOfBoundsException {
        if (isLoggingDebug()) {
            logDebug("Try to create/update repository item from :" + str);
        }
        if (str == null || str.length() == 0)
            return null;
        String[] values = parseValuesString(str);
        MutableRepositoryItem mri = null;
        
        RqlStatement codePostalRql = RqlStatement.parseRqlStatement("code_postal = ?0 and ville = ?1");
        RepositoryView postalRepositoryView = mr.getView(getItemDescriptorName());
        Object rqlparams[] = new Object[]{values[0], values[1]};
        RepositoryItem[] codepostalList = codePostalRql.executeQuery(postalRepositoryView, rqlparams);
        
        if (codepostalList == null || codepostalList.length == 0) {
            setCreate(true);
            mri = createNewItem(mr, values);
            if (isLoggingDebug()) {
                logDebug("Repository item from : " + str + " has been created successfully.");
            }
         } else {
            setCreate(false);
            mri = null;
            if (isLoggingDebug()) {
                logDebug("Repository item from : " + str + " is present.");
            }
        }
        return mri;
    }

    private Date getLastDate(Date d1, Date d2) {
        if (d1 == null && d2 == null)
            return null;
        if (d1 != null && d2 == null) {
            return d1;
        } else {
            if (d1 != null) {
                if (d1.compareTo(d2) > 0) {
                    return d1;
                } else {
                    return d2;
                }
            }
            return d2;
        }
    }

    private MutableRepositoryItem createNewItem(MutableRepository mr,
            String[] values) throws ParseException, RepositoryException {
        MutableRepositoryItem mri = mr.createItem(getItemDescriptorName());
        mri.setPropertyValue(PROPERTY_NAME_CODE_POSTAL, values[0]);
        mri.setPropertyValue(PROPERTY_NAME_VILLE, values[1]);
        return mri;
    }

    private MutableRepositoryItem getItemForUpdate(MutableRepository mr,
            String[] values) throws ParseException, RepositoryException {
        MutableRepositoryItem mri = mr.getItemForUpdate(values[3],
                getItemDescriptorName());
        return mri;
    }

    private String[] parseValuesString(String s){
        String[] temp = s.split(SPLITTER_SEPARATOR);
        String[] newValues = new String [temp.length];
        int count = temp.length;
        int n = 0;
        int i = 0;
        StringBuilder res = new StringBuilder();
        while (count-n>0){
            boolean sfl = false;
            while (true){
            String t = temp[n];
            if (t.startsWith("\"")){
                res.append(t);
                if(t.endsWith("\"")){
                    removeQuotes(res);
                    n++;
                    break;
                }
                res.append(',');
                n++;
                sfl=true;
                continue;
            } else {
                if(t.endsWith("\"")){
                    res.append(t);
                    removeQuotes(res);
                    sfl=false;
                    n++;
                    break;
                } else {
                    res.append(t);
                    if (sfl){
                        res.append(',');
                    } else {
                        n++;
                        break;
                    }
                }
            }
            n++;
            }
        newValues[i] =res.toString();
        i++;
        res.delete(0, res.length());
        }
        return newValues;
    }
    
    private void removeQuotes(StringBuilder res){
        while(res.indexOf("\"")!=-1){
            res.deleteCharAt(res.indexOf("\""));
        }
    }

    public String getItemDescriptorName() {
        return mItemDescriptorName;
    }

    public void setItemDescriptorName(String itemDescriptorName) {
        mItemDescriptorName = itemDescriptorName;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    boolean isCreate() {
        return create;
    }

    void setCreate(boolean create) {
        this.create = create;
    }

}
