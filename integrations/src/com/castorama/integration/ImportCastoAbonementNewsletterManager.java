package com.castorama.integration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.castorama.integration.experian.ExperianRequestHelper;
import com.castorama.integration.experian.ExperianUtils;
import com.castorama.integration.util.MiscUtils;

import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

public class ImportCastoAbonementNewsletterManager extends IntegrationBase {

    /**
     * 
     */
    private static final String QUOTES = "\"";
    /**
     * 
     */
    private static final String CIVILITY_EMPTY_CODE = "";
    /**
     * 
     */
    private static final String CIVILITY_MADEMOISELLE_CODE = "2";
    /**
     * 
     */
    private static final String CIVILITY_MADAME_CODE = "1";
    /**
     * 
     */
    private static final String CIVILITY_MONSIEUR_CODE = "0";
    /**
     * 
     */
    private static final String CIVILITY_SOCIETE_CODE = "5";
    /**
     * 
     */
    private static final String FILE_NAME_SEPARATOR = "_";
    /**
     * 
     */
    private static final String VALUE_SEPARATOR = ";";
    private static final String PROPERTY_NAME_DATE_OF_BIRTH = "dateOfBirth";
    private static final String PROPERTY_NAME_DATE_DERNIERE_MODIFICATION = "dateDerniereModification";
    private static final String PROPERTY_NAME_DATE_OFFERS_DESINCRPTION = "dateOffersDesincrption";
    private static final String PROPERTY_NAME_DATE_OFFERS_INSCRIPTION = "dateOffersInscription";
    private static final String PROPERTY_NAME_DATE_DESINCRPTION = "dateDesincrption";
    private static final String PROPERTY_NAME_DATE_INSCRIPTION = "dateInscription";
    private static final String PROPERTY_NAME_DATE_CREATION = "dateCreation";
    private static final String PROPERTY_NAME_RESEIVE_OFFERS = "reseiveOffers";
    private static final String PROPERTY_NAME_RECEIVE_EMAIL = "receiveEmail";
    private static final String PROPERTY_NAME_GAMES = "games";
    private static final String PROPERTY_NAME_VILLE = "ville";
    private static final String PROPERTY_NAME_CODE_POSTAL = "CodePostal";
    private static final String PROPERTY_NAME_EMAIL = "email";
    private static final String PROPERTY_NAME_NOM = "nom";
    private static final String PROPERTY_NAME_PRENOM = "prenom";
    private static final String PROPERTY_NAME_CIVILITE = "civilite";
    private static final String PROPERTY_NAME_SOURCE_INSCRIPTION = "sourceInscription";
    private static final String CIVILITY_MADEMOISELLE2 = "Mademoiselle";
    private static final String CIVILITY_MADEMOISELLE1 = "Mlle";
    private static final String CIVILITY_MADAME1 = "Madame";
    private static final String CIVILITY_MADAME2 = "Mme";
    private static final String CIVILITY_MONSIEUR1 = "Monsieur";
    private static final String CIVILITY_MONSIEUR2 = "Mr";
    private static final String CIVILITY_MONSIEUR3 = "M";
    private static final String CIVILITY_SOCIETE = "Société";
    private static final String SUBSCRIPTION_TRUE = "1";
    private static final String SUBSCRIPTION_FALSE = "2";
    private static final String SUBSCRIPTION_EMPTY = "0";
    private static final String SUBSCRIPTION_EXPERIAN_TRUE = "0";
    private static final String SUBSCRIPTION_EXPERIAN_FALSE = "1";
    private static final String SUBSCRIPTION_EXPERIAN_EMPTY = "8";
    private static final String TRUE = "true";
    private static final String FALSE = "false";
    private static final String EMPTY = "";
    private static final String SOURCE_MASS = "mass";
    private static final String EXPERIAN_REQUEST_ITEM_DESCRIPTOR = "experianRequest";

    private File inputDir;
    private File workingDir;
    private File archiveDir;
    private File errorDir;

    private String mItemDescriptorName;
    private String mDatePattern;
    private String mType;
    private boolean create;
    private ExperianRequestHelper mExperianRequestHelper;

    public void importAbonements() {
        long currentTimeMillis = System.currentTimeMillis();
        if (isLoggingDebug()) {
            logDebug("start - ImportCastoAbonementNewsletterManager - importAbonements");
        }
        PrintWriter logWriter = null;
        try {
            File workingDir = getWorkingDir();
            checkFolders();

            File workingFile = getWorkingFile();
            if (workingFile == null) {
                if (isLoggingError()) {
                    logError("input file is not found in folder: " + inputDir.getAbsolutePath());
                }
                return;
            }
            if (isLoggingDebug()) {
                logDebug("processing: " + workingFile);
            }
            File statusFile = new File(workingDir, getFileName(workingFile) + Constants.LOG_FILE_EXTENSION);
            logWriter = new PrintWriter(statusFile);
            logWriter.println("Data file: " + workingFile.getName());
            parseCsv(workingFile, logWriter);
            // don't generate scv file like for ExportNewsletterManager
            // generateOutputCSV();
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
            logDebug("finish - ImportCastoAbonementNewsletterManager - importAbonements");
        }
        logWarning("import CastoAbonementNewsletter complete : " + (System.currentTimeMillis() - currentTimeMillis));

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
        String workName = name + FILE_NAME_SEPARATOR + Constants.DATA_FILE_NAME_DATE_FORMAT.format(new Date())
                + inpFile.getName().substring(name.length(), inpFile.getName().length());
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
        String tmp = null;
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        File csvFile = null;
        PrintWriter out = null;
        try {
            fis = new FileInputStream(workingFile);
            isr = new InputStreamReader(fis,"ISO-8859-1");
            br = new BufferedReader(isr);
            MutableRepository mr = (MutableRepository) getRepository();
            File sendDir = getDir(Constants.OUTPUT_FOLDER);
            Date currentDate = new Date();
            csvFile = new File(sendDir, Constants.MASS_DATA_FILE_NAME_PREFIX
                    + Constants.DATA_FILE_NAME_DATE_FORMAT.format(currentDate) + Constants.DATA_FILE_EXTENSION);
            out = new PrintWriter(csvFile);

            int count = 0;
            String outLine = null;

            while ((tmp = br.readLine()) != null) {
                try {
                    if (count != 0) {
                        MutableRepositoryItem ri = getRepositoryItem(mr, tmp);
                        if (isCreate()) {
                            mr.addItem(ri);
                            // don't add experian request to repository
                            // notifyExperianSubscrible((String)ri.getPropertyValue(PROPERTY_NAME_EMAIL), SOURCE_MASS);
                            outLine = generateOutputLine(tmp, ri, log);
                            out.println(outLine);
                        } else {
                            if (ri != null) {
                                mr.updateItem(ri);
                                // don't add experian request to repository
                                // notifyExperianUpdate((String)ri.getPropertyValue(PROPERTY_NAME_EMAIL), SOURCE_MASS);
                                outLine = generateOutputLine(tmp, ri, log);
                                out.println(outLine);
                            }
                        }
                    } else {
                        out.println(tmp);
                    }
                    count++;
                } catch (RepositoryException e) {
                    if (isLoggingError()) {
                        logError("Can't import item from : " + tmp, e);
                        log.println("Can't import item from : " + tmp + "\n" + e);
                    }
                } catch (ParseException e) {
                    if (isLoggingError()) {
                        logError("Can't parse item from : " + tmp, e);
                        log.println("Can't parse item from : " + tmp + "\n" + e);
                    }
                } catch (IndexOutOfBoundsException e) {
                    if (isLoggingError()) {
                        logError("Can't create item from :" + tmp, e);
                        log.println("Can't create item from :" + tmp + "\n" + e);
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
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    if (isLoggingError()) {
                        logError(e);
                    }
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    if (isLoggingError()) {
                        logError(e);
                    }
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    if (isLoggingError()) {
                        logError(e);
                    }
                }
            }
            if (log != null) {
                log.close();
            }
            if (out != null) {
                out.close();
            }
        }

    }

    /**
     * @param inputLine
     * @return
     */
    private String generateOutputLine(String inputLine, RepositoryItem ri, PrintWriter log) {
        String[] cells = parseValuesString(inputLine);
        if (cells != null && cells.length >= 11) {
            // replace full civilite by integer code
            if (cells[0] != null) {
                String val = cells[0].trim();
                if (val.equalsIgnoreCase(CIVILITY_MONSIEUR1) 
                        || val.equalsIgnoreCase(CIVILITY_MONSIEUR2) 
                        || val.equalsIgnoreCase(CIVILITY_MONSIEUR3))
                    cells[0] = CIVILITY_MONSIEUR_CODE;
                else if (val.equalsIgnoreCase(CIVILITY_MADAME1)
                        || val.equalsIgnoreCase(CIVILITY_MADAME2))
                    cells[0] = CIVILITY_MADAME_CODE;
                else if (val.equalsIgnoreCase(CIVILITY_MADEMOISELLE1)
                        || val.equalsIgnoreCase(CIVILITY_MADEMOISELLE2))
                    cells[0] = CIVILITY_MADEMOISELLE_CODE;
                else if (val.equalsIgnoreCase(CIVILITY_SOCIETE))
                    cells[0] = CIVILITY_SOCIETE_CODE;
                else
                    cells[0] = CIVILITY_EMPTY_CODE;
            }
            // replace receiveCastorama code by code for experian
            //                ATG Experian
            // subscription   1   0
            // unsubscription 2   1
            // unknown        0   8
            if (cells[7] != null) {
                String val = cells[7].trim();
                if (val.equalsIgnoreCase(SUBSCRIPTION_TRUE))
                    cells[7] = SUBSCRIPTION_EXPERIAN_TRUE;
                else if (val.equalsIgnoreCase(SUBSCRIPTION_EMPTY))
                    cells[7] = SUBSCRIPTION_EXPERIAN_EMPTY;
                else if (val.equalsIgnoreCase(SUBSCRIPTION_FALSE))
                    cells[7] = SUBSCRIPTION_EXPERIAN_FALSE;
                else {
                    log.println("Can't parse line \"" + inputLine + "\"\n"
                            + "receive email must be one of \"1\", \"2\" or \"0\"(empty), but not a \"" + 
                            cells[7] + "\"");
                    log.println("Receive email seted as \"0\" (empty)");
                }
            }
            // replace receiveOffers code by code for experian
            //                ATG Experian
            // subscription   1   0
            // unsubscription 2   1
            // unknown        0   8
            if (cells[8] != null) {
                String val = cells[8].trim();

                if (val.equalsIgnoreCase(SUBSCRIPTION_TRUE))
                    cells[8] = SUBSCRIPTION_EXPERIAN_TRUE;
                else if (val.equalsIgnoreCase(SUBSCRIPTION_EMPTY))
                    cells[8] = SUBSCRIPTION_EXPERIAN_EMPTY;
                else if (val.equalsIgnoreCase(SUBSCRIPTION_FALSE))
                    cells[8] = SUBSCRIPTION_EXPERIAN_FALSE;
                else {
                    log.println("Can't parse line \"" + inputLine + "\"\n"
                            + "receive offers must be one of \"1\", \"2\" or \"0\"(empty), but not a \"" + 
                            cells[8] + "\"");
                    log.println("Receive offers seted as \"0\" (empty)");
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cells.length; i++) {
            if (i != 0) {
                sb.append(VALUE_SEPARATOR);
            }
            sb.append(cells[i]);
        }
        return sb.toString();
    }

   private void generateOutputCSV() {
        MutableRepository mr = (MutableRepository) getRepository();

        int counter = 0;
        File csvFile = null;
        PrintWriter log = null;
        try {
            TransactionDemarcation trd = new TransactionDemarcation();
            try {
                trd.begin(getTransactionManager(), TransactionDemarcation.REQUIRES_NEW);
                File sendDir = getDir(Constants.OUTPUT_FOLDER);
                RqlStatement requestsRql = RqlStatement.parseRqlStatement("requestSource = 2");
                RepositoryView requestsView = mr.getView(EXPERIAN_REQUEST_ITEM_DESCRIPTOR);
                RepositoryItem[] requestsList = requestsRql.executeQuery(requestsView, new Object[] {});

                Date current = new Date();
                csvFile = new File(sendDir, Constants.MASS_DATA_FILE_NAME_PREFIX + Constants.DATA_FILE_NAME_DATE_FORMAT.format(current) 
                        + Constants.DATA_FILE_EXTENSION);

                File logFile = new File(sendDir, Constants.MASS_DATA_FILE_NAME_PREFIX + Constants.DATA_FILE_NAME_DATE_FORMAT.format(current) 
                        + Constants.LOG_FILE_EXTENSION);
                log = new PrintWriter(logFile);
                if (requestsList == null) {
                    log.println("Experian requests list for mass processing is empty.");
                    return;
                }

                if (csvFile.createNewFile()) {
                    log.println("Data file: " + csvFile.getName());
                    counter = ExperianUtils.printToDataFile(csvFile, requestsList);
                }

            } catch (RepositoryException e) {
                if (isLoggingError()) {
                    logError(e);
                }
                log.println("Repository exception: " + "\n" + e);
            } catch (FileNotFoundException e) {
                if (isLoggingError()) {
                    logError("input file " + csvFile.getName() + " is not found in folder: "
                            + inputDir.getAbsolutePath());
                }
                log.println("input file " + csvFile.getName() + " is not found in folder: "
                        + inputDir.getAbsolutePath());
            } catch (IOException e) {
                if (isLoggingError()) {
                    logError(e);
                }
                log.println("Repository exception: " + "\n" + e);
            } catch (TransactionDemarcationException e) {
                if (isLoggingError()) {
                    logError(e);
                }
                log.println("Transaction demarcation exception: " + "\n" + e);
            } finally {
                if (isLoggingDebug()) {
                    logDebug("Were processed " + counter + " experian requests.");
                }
                log.println("Were processed " + counter + " experian requests.");
                if (log != null) {
                    log.close();
                    log = null;
                }
                trd.end();
            }
        } catch (TransactionDemarcationException e) {
            if (isLoggingError()) {
                logError(e);
            }
        }
    }

    private MutableRepositoryItem getRepositoryItem(MutableRepository mr, String str) throws RepositoryException,
            ParseException, IndexOutOfBoundsException {
        if (isLoggingDebug()) {
            logDebug("Try to create/update repository item from :" + str);
        }
        if (str == null || str.length() == 0)
            return null;
        String[] values = parseValuesString(str);
        MutableRepositoryItem mri = null;
        RepositoryItem ri = mr.getItem(values[3], getItemDescriptorName());

        if (ri == null) {
            setCreate(true);
            mri = createNewItem(mr, values);
            if (isLoggingDebug()) {
                logDebug("Repository item from : " + str + " has been created successfully.");
            }
        } else {
            setCreate(false);
            if (getType().equalsIgnoreCase(PROPERTY_NAME_GAMES)) {
                mri = getItemForUpdate(mr, values);
                if (isLoggingDebug() && mri != null) {
                    logDebug("Repository item from : " + str + " has been changed successfully.");
                }
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

    private String getSpecificValue(String str) {
        if (str.equalsIgnoreCase(SUBSCRIPTION_FALSE)) {
            return FALSE;
        } else {
            if (str.equalsIgnoreCase(SUBSCRIPTION_TRUE)) {
                return TRUE;
            } else {
                return EMPTY;
            }
        }
    }

    private String parseCivility(String val) {
        String result = null;
        if (val != null) {
            if (val.equalsIgnoreCase(CIVILITY_MONSIEUR1) 
                    || val.equalsIgnoreCase(CIVILITY_MONSIEUR2) 
                    || val.equalsIgnoreCase(CIVILITY_MONSIEUR3))
                result = CIVILITY_MONSIEUR1;
            else if (val.equalsIgnoreCase(CIVILITY_MADAME1)
                    || val.equalsIgnoreCase(CIVILITY_MADAME2))
                result = CIVILITY_MADAME1;
            else if (val.equalsIgnoreCase(CIVILITY_MADEMOISELLE1) 
                    || val.equalsIgnoreCase(CIVILITY_MADEMOISELLE2))
                result = CIVILITY_MADEMOISELLE1;
            else if (val.equalsIgnoreCase(CIVILITY_SOCIETE))
                result = CIVILITY_SOCIETE;
        }
        return result;
    }

    private MutableRepositoryItem createNewItem(MutableRepository mr, String[] values) throws ParseException,
            RepositoryException {
        MutableRepositoryItem mri = mr.createItem(getItemDescriptorName());
        mri.setPropertyValue(PROPERTY_NAME_CIVILITE, parseCivility(values[0]));
        mri.setPropertyValue(PROPERTY_NAME_PRENOM, values[1]);
        mri.setPropertyValue(PROPERTY_NAME_NOM, values[2]);
        mri.setPropertyValue(PROPERTY_NAME_EMAIL, values[3]);
        mri.setPropertyValue(PROPERTY_NAME_SOURCE_INSCRIPTION, values[10]);
        mri.setPropertyValue(PROPERTY_NAME_CODE_POSTAL, values[5]);
        mri.setPropertyValue(PROPERTY_NAME_VILLE, values[6]);
        String specRecieveEmail = getSpecificValue(values[7]);
        String specRecieveOffers = getSpecificValue(values[8]);
        if (getType().equalsIgnoreCase(PROPERTY_NAME_GAMES)) {
            mri.setPropertyValue(PROPERTY_NAME_RECEIVE_EMAIL, specRecieveEmail);
            mri.setPropertyValue(PROPERTY_NAME_RESEIVE_OFFERS, specRecieveOffers);
        } else {
            mri.setPropertyValue(PROPERTY_NAME_RECEIVE_EMAIL, TRUE);
            mri.setPropertyValue(PROPERTY_NAME_RESEIVE_OFFERS, FALSE);
        }
        Date csvDate = getTimestamp(values);
        mri.setPropertyValue(PROPERTY_NAME_DATE_CREATION, csvDate);
        if (specRecieveEmail.equalsIgnoreCase(TRUE)) {
            mri.setPropertyValue(PROPERTY_NAME_DATE_INSCRIPTION, csvDate);
        } else {
            mri.setPropertyValue(PROPERTY_NAME_DATE_DESINCRPTION, csvDate);
        }
        if (specRecieveOffers.equalsIgnoreCase(TRUE)) {
            mri.setPropertyValue(PROPERTY_NAME_DATE_OFFERS_INSCRIPTION, csvDate);
        } else {
            mri.setPropertyValue(PROPERTY_NAME_DATE_OFFERS_DESINCRPTION, csvDate);
        }
        mri.setPropertyValue(PROPERTY_NAME_DATE_DERNIERE_MODIFICATION, csvDate);
        
        Date csvDateOfBirth = getTimestampOfBirth(values);
        mri.setPropertyValue(PROPERTY_NAME_DATE_OF_BIRTH, csvDateOfBirth);
        
        return mri;
    }

    private MutableRepositoryItem getItemForUpdate(MutableRepository mr, String[] values) throws ParseException,
            RepositoryException {
        MutableRepositoryItem mri = mr.getItemForUpdate(values[3], getItemDescriptorName());
        Date csvDate = getTimestamp(values);
        if (csvDate == null)
            return mri;
        // date of cancel email subscription
        Date receiveEmailDateDesincrption = (Date) mri.getPropertyValue(PROPERTY_NAME_DATE_DESINCRPTION);
        // date of start email subscription
        Date receiveEmailDateIncrption = (Date) mri.getPropertyValue(PROPERTY_NAME_DATE_INSCRIPTION);
        // date of start offers subscription
        Date receiveOffersDateDesincrption = (Date) mri.getPropertyValue(PROPERTY_NAME_DATE_OFFERS_DESINCRPTION);
        // date of start offers subscription
        Date receiveOffersDateIncrption = (Date) mri.getPropertyValue(PROPERTY_NAME_DATE_OFFERS_INSCRIPTION);
        // date of the last modification
        Date lastModification = (Date) mri.getPropertyValue(PROPERTY_NAME_DATE_DERNIERE_MODIFICATION);
        
        Date last1 = getLastDate(receiveEmailDateIncrption, receiveEmailDateDesincrption);
        String latest_recive_email = (String) mri.getPropertyValue(PROPERTY_NAME_RECEIVE_EMAIL);
        boolean isUpdated = false;
        if (last1 != null && last1.compareTo(csvDate) < 0) {
            String value = getSpecificValue(values[7]);
            mri.setPropertyValue(PROPERTY_NAME_RECEIVE_EMAIL, value);
            if (value.equalsIgnoreCase(TRUE)) {
                mri.setPropertyValue(PROPERTY_NAME_DATE_INSCRIPTION, csvDate);
                if (getType().equalsIgnoreCase(PROPERTY_NAME_GAMES) && !latest_recive_email.equalsIgnoreCase(TRUE)) {
                    mri.setPropertyValue(PROPERTY_NAME_SOURCE_INSCRIPTION, values[10]);
                }
            } else {
                mri.setPropertyValue(PROPERTY_NAME_DATE_DESINCRPTION, csvDate);
            }
            
            if (lastModification != null && lastModification.compareTo(csvDate) < 0) {
                Date csvDateOfBirth = getTimestampOfBirth(values);
                if (csvDateOfBirth != null) {
                    mri.setPropertyValue(PROPERTY_NAME_DATE_OF_BIRTH, csvDateOfBirth);
                }
            }
            
            isUpdated = true;
        }
        last1 = getLastDate(receiveOffersDateIncrption, receiveOffersDateDesincrption);
        if (last1 != null && last1.compareTo(csvDate) < 0) {
            String value = getSpecificValue(values[8]);
            mri.setPropertyValue(PROPERTY_NAME_RESEIVE_OFFERS, value);
            if (value.equalsIgnoreCase(TRUE)) {
                mri.setPropertyValue(PROPERTY_NAME_DATE_OFFERS_INSCRIPTION, csvDate);
            } else {
                mri.setPropertyValue(PROPERTY_NAME_DATE_OFFERS_DESINCRPTION, csvDate);
            }
            
            if (lastModification != null && lastModification.compareTo(csvDate) < 0) {
                Date csvDateOfBirth = getTimestampOfBirth(values);
                if (csvDateOfBirth != null) {
                    mri.setPropertyValue(PROPERTY_NAME_DATE_OF_BIRTH, csvDateOfBirth);
                }
            }
            
            isUpdated = true;
        }
        if (isUpdated) {
            mri.setPropertyValue(PROPERTY_NAME_DATE_DERNIERE_MODIFICATION, csvDate);
            return mri;
        }
        return null;
    }

    private String[] parseValuesString(String s) {
        String[] temp = s.split(VALUE_SEPARATOR);
        String[] newValues = new String[temp.length];
        int count = temp.length;
        int n = 0;
        int i = 0;
        StringBuilder res = new StringBuilder();
        while (count - n > 0) {
            boolean sfl = false;
            while (true) {
                String t = temp[n];
                if (t.startsWith(QUOTES)) {
                    res.append(t);
                    if (t.endsWith(QUOTES)) {
                        removeQuotes(res);
                        n++;
                        break;
                    }
                    res.append(',');
                    n++;
                    sfl = true;
                    continue;
                } else {
                    if (t.endsWith(QUOTES)) {
                        res.append(t);
                        removeQuotes(res);
                        sfl = false;
                        n++;
                        break;
                    } else {
                        res.append(t);
                        if (sfl) {
                            res.append(',');
                        } else {
                            n++;
                            break;
                        }
                    }
                }
                n++;
            }
            newValues[i] = res.toString();
            i++;
            res.delete(0, res.length());
        }
        return newValues;
    }

    private void removeQuotes(StringBuilder res) {
        while (res.indexOf(QUOTES) != -1) {
            res.deleteCharAt(res.indexOf(QUOTES));
        }
    }

    private Timestamp getTimestamp(String[] values) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(getDatePattern());
        Date csvDate = sdf.parse(values[9]);
        if (csvDate == null)
            return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(csvDate);
        return new Timestamp(cal.getTimeInMillis());
    }
    
    private Timestamp getTimestampOfBirth(String[] values) throws ParseException {
        if (values.length < 12) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(getDatePattern());
        Date csvDate = sdf.parse(values[11]);
        if (csvDate == null)
            return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(csvDate);
        return new Timestamp(cal.getTimeInMillis());
    }

    /**
     * Notify experian system for remove subscription.
     */
    private void notifyExperianUnsubscrible(String email) {
        ExperianRequestHelper service = getExperianRequestHelper();

        try {
            service.addRemoveSubscriptionRequest(email, SOURCE_MASS);
        } catch (Throwable e) {
            logError(e);
        }
    }

    /**
     * Notify experian system for update subscription.
     */
    private void notifyExperianUpdate(String email, String source) {
        ExperianRequestHelper service = getExperianRequestHelper();

        try {
            service.addChangeSubscriptionRequest(email, source);
        } catch (Throwable e) {
            logError(e);
        }
    }

    /**
     * Notify experian system for create subscription.
     */
    private void notifyExperianSubscrible(String email, String source) {
        ExperianRequestHelper service = getExperianRequestHelper();

        try {
            service.addCreateSubscriptionRequest(email, source);
        } catch (Throwable e) {
            logError(e);
        }
    }

    public String getItemDescriptorName() {
        return mItemDescriptorName;
    }

    public void setItemDescriptorName(String itemDescriptorName) {
        mItemDescriptorName = itemDescriptorName;
    }

    public String getDatePattern() {
        return mDatePattern;
    }

    public void setDatePattern(String datePatterns) {
        mDatePattern = datePatterns;
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

    public ExperianRequestHelper getExperianRequestHelper() {
        return mExperianRequestHelper;
    }

    public void setExperianRequestHelper(ExperianRequestHelper pExperianRequestHelper) {
        mExperianRequestHelper = pExperianRequestHelper;
    }
}
