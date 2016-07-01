/**
 * 
 */
package com.castorama.integration.experian;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.ParseException;

import com.castorama.integration.Constants;
import com.castorama.integration.IntegrationBase;

import atg.nucleus.ServiceException;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

/**
 * @author MK
 * 
 */
public class UnsubscribeBadEmailManager extends IntegrationBase {

    /** repository property. */
    private Repository mRepository;

    /** inProgress flag property */
    private boolean    inProgress = false;

    private File       inputDir;
    private File       workingDir;
    private File       archiveDir;
    private File       errorDir;
    
    private PrintWriter mLogWriter = null;
    
    private boolean status = true;

    /**
     * Returns repository property.
     * 
     * @return repository property.
     */
    public Repository getRepository() {
        return mRepository;
    }

    /**
     * Sets the value of the repository property.
     * 
     * @param pRepository parameter to set.
     */
    public void setRepository(Repository pRepository) {
        mRepository = pRepository;
    }

    /**
     * Connects to the queue and starts listening for messages.
     * 
     * @throws ServiceException If a service exception occurs.
     */
    public void doStartService() throws ServiceException {
        try {
            checkFolders();
        } catch (FileNotFoundException e) {
            if (isLoggingError()){
                logError("Creating folders error.", e);
            }
        }
    }

    /** */
    public void unsubscribeBadEmails() throws ServiceException {
        if (inProgress) {
            throw new ServiceException(
                    "Unable to start unsubscribing... The service is already started. Please wait...");
        }

        try {
            checkFolders();
        } catch (FileNotFoundException e) {
            if (isLoggingError()){
                logError("Creating folders error.", e);
            }
        }
        
        if (isLoggingDebug()) {
            logDebug("start - UnsubscribeBadEmailManager - unsubscribeBadEmails");
        }

        inProgress = true;

        try {
            long currentTimeMillis = System.currentTimeMillis();
            PrintWriter logWriter = null;
            try {
                File workingDir = getWorkingDir();
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
                setLogWriter(logWriter);
                parseCsv(workingFile);
                logWriter.println("Operation end. Status is " + (status?"OK":"KO"));
            } catch (Exception e) {
                if (isLoggingError()) {
                    logError("Exception occured: ", e);
                }
            } finally {
                if (logWriter != null) {
                    logWriter.close();
                }
            }

            if (isLoggingDebug()) {
                logDebug("finish - " + UnsubscribeBadEmailManager.class.getName()
                        + " - importPostalCodes");
                logDebug("unsubscribe bad emails  complete : "
                        + (System.currentTimeMillis() - currentTimeMillis));
            }

        } finally {
            inProgress = false;
        }

        if (isLoggingDebug()) {
            logDebug("finish - UnsubscribeBadEmailManager - unsubscribeBadEmails");
        }
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
        File[] inpFiles = getInputDir().listFiles();
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
        return getWorkingFile(inpFile);
    }
    
    private void parseCsv(File workingFile) {
        if (workingFile == null) {
            if (isLoggingDebug()) {
                logDebug("Source file is null.");
            }
            return;
        }
        String tmp = null;
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            fis = new FileInputStream(workingFile);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            int count = 0;
            while ((tmp = br.readLine()) != null) {
                try {
                    getRepositoryItemsAndUnsubscribe(tmp);
                    count++;
                } catch (RepositoryException e) {
                    status = false;
                    if (isLoggingError()) {
                        logError("Can't import item from : " + tmp, e);
                        getLogWriter().println("Can't import item from : " + tmp + "\n"
                                + e);
                    }
                } catch (ParseException e) {
                    status = false;
                    if (isLoggingError()) {
                        logError("Can't parse item from : " + tmp, e);
                        getLogWriter().println("Can't parse item from : " + tmp
                                        + "\n" + e);
                    }
                } catch (IndexOutOfBoundsException e) {
                    status = false;
                    if (isLoggingError()) {
                        logError("Can't create item from :" + tmp, e);
                        getLogWriter().println("Can't create item from :" + tmp+ "\n"
                                + e);
                    }
                }
            }
            if (isLoggingDebug()) {
                logDebug("parsed " + count + " lines");
            }
            getLogWriter().println("parsed " + count + " lines");
        } catch (IOException e) {
            status = false;
            if (isLoggingError()) {
                logError(e);
                getLogWriter().println(e);
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
    
    private void getRepositoryItemsAndUnsubscribe(String str) throws RepositoryException, ParseException, IndexOutOfBoundsException {
        if (isLoggingDebug()) {
            logDebug("Try to update repository item from :" + str);
        }
        if (str == null || str.length() == 0)
            return;
        MutableRepositoryItem mri = null;
        MutableRepository mr = (MutableRepository) getRepository();
        RqlStatement newsletterAccountsRql = RqlStatement.parseRqlStatement("email = ?0");
        RepositoryView newsletterAccountsView = mr.getView("abonnementNewsletter");
        Object rqlparams[] = new Object[]{str};
        RepositoryItem[] newsletterAccountslList = newsletterAccountsRql.executeQuery(newsletterAccountsView, rqlparams);
        if (newsletterAccountslList != null && newsletterAccountslList.length > 0) {
            if (isLoggingDebug()) {
                logDebug("Repository item from : " + str + " has been created successfully.");
            }
            for (int i = 0; i < newsletterAccountslList.length; i++){
                mri = (MutableRepositoryItem) newsletterAccountslList[0];
                mri.setPropertyValue("receiveEmail", "false");
                mri.setPropertyValue("reseiveOffers", "false");
                mr.updateItem(mri);
            }
         } else {
            if (isLoggingDebug()) {
                logDebug("Repository item from : " + str + " not found.");
            }
        }
    }

    public File getInputDir() {
        return inputDir;
    }

    public PrintWriter getLogWriter() {
        return mLogWriter;
    }

    public void setLogWriter(PrintWriter logWriter) {
        this.mLogWriter = logWriter;
    }
    
    

}
