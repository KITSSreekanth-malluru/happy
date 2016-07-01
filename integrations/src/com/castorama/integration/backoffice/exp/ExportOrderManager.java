package com.castorama.integration.backoffice.exp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;
import javax.transaction.TransactionManager;

import com.castorama.commerce.pricing.CastVATManager;
import com.castorama.commerce.states.BOOrderStates;
import com.castorama.integration.journal.JournalItem;
import com.castorama.integration.journal.ProcessingJournalService;

import com.castorama.payment.PayboxPaymentStates;
import com.castorama.payment.PaymentStates;

import atg.commerce.pricing.priceLists.PriceListManager;
import atg.commerce.states.OrderStates;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.service.scheduler.Schedulable;
import atg.service.scheduler.Schedule;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;

/**
 * Export orders service.
 *
 * @author Andrew_Logvinov
 */
public class ExportOrderManager extends GenericService implements Schedulable {

    /**
     * 
     */
    private static final String USER_DESCRIPTOR_NAME = "user";
    /**
     * 
     */
    private static final String PROFILE_ID_PROPERTY_NAME = "profileId";
    /**
     * 
     */
    private static final String STORE_ID_PROPERTY_NAME = "storeId";
    /**
     * 
     */
    private static final String NOM_PROPERTY_NAME = "nom";
    /**
     * 
     */
    private static final String GET_MAGASIN_BY_ID_STATEMENT = " id = ?0 ";
    /**
     * 
     */
    private static final String MAGASIN_DESCRIPTOR_NAME = "magasin";
    /**
     * FILE_RECORD_DATETIME_PATTERN constant.
     */
    private static final String FILE_RECORD_DATETIME_PATTERN = "dd/MM/yyyy HH:mm:ss";
    /**
     * FILE_NAME_DATE_PATTERN constant.
     */
    private static final String FILE_NAME_DATE_PATTERN = "ddMMyyyy_HHmmss";
    /**
     * TXT_FILE_TYPE constant.
     */
    private static final String TXT_FILE_TYPE = ".txt";
    /**
     * FILE_NAME_SEPARATOR constant.
     */
    private static final String FILE_NAME_SEPARATOR = "_";
    /**
     * ALL_RANGE constant.
     */
    private static final String ALL_RANGE = "ALL";
    /**
     * COMMANDE constant.
     */
    private static final String COMMANDE = "COMMANDE";
    /**
     * Prefix for exported file
     */
    private String filePrefix;

    private CastVATManager mCastVATManager;
    private PayboxDirectPlusManager payboxDirectPlusManager;

    public PayboxDirectPlusManager getPayboxDirectPlusManager() {
        return payboxDirectPlusManager;
    }

    public void setPayboxDirectPlusManager(PayboxDirectPlusManager payboxDirectPlusManager) {
        this.payboxDirectPlusManager = payboxDirectPlusManager;
    }

    /**
     * @return the filePrefix
     */
    public String getFilePrefix() {
        return filePrefix;
    }

    public CastVATManager getCastVATManager() {
        return mCastVATManager;
    }

    public void setCastVATManager(CastVATManager mCastVATManager) {
        this.mCastVATManager = mCastVATManager;
    }

    /**
     * @param filePrefix the filePrefix to set
     */
    public void setFilePrefix(String filePrefix) {
        this.filePrefix = filePrefix;
    }

    /**
     * Order repository
     */
    private Repository repository;

    /**
     * @return the repository
     */
    public Repository getRepository() {
        return repository;
    }

    /**
     * @param repository the repository to set
     */
    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    /**
     * Magasin repository
     */
    private Repository magasinRepository;
    
    /**
     * @return the repository
     */
    public Repository getMagasinRepository() {
        return magasinRepository;
    }
    
    /**
     * @param magasinRepository the repository to set
     */
    public void setMagasinRepository(Repository magasinRepository) {
        this.magasinRepository = magasinRepository;
    }
    
    /**
     * SIPS repository.
     */
    private Repository sipsRepository;

    /**
     * @return the sipsRepository
     */
    public Repository getSipsRepository() {
        return sipsRepository;
    }

    /**
     * @param sipsRepository the sipsRepository to set
     */
    public void setSipsRepository(Repository sipsRepository) {
        this.sipsRepository = sipsRepository;
    }

    /**
     * Product repository.
     */
    private Repository productRepository;

    /**
     * @return the productRepository
     */
    public Repository getProductRepository() {
        return productRepository;
    }

    /**
     * @param productRepository the productRepository to set
     */
    public void setProductRepository(Repository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Product repository.
     */
    private Repository profileRepository;

    /**
     * @return the profileRepository
     */
    public Repository getProfileRepository() {
        return profileRepository;
    }

    /**
     * @param profileRepository the profileRepository to set
     */
    public void setProfileRepository(Repository profileRepository) {
        this.profileRepository = profileRepository;
    }

    /**
     * Coupons repository.
     */
    private Repository couponsRepository;

    /**
     * @return the couponsRepository
     */
    public Repository getCouponsRepository() {
        return couponsRepository;
    }

    /**
     * @param couponsRepository the couponsRepository to set
     */
    public void setCouponsRepository(Repository couponsRepository) {
        this.couponsRepository = couponsRepository;
    }

    private File rootDir;

    /**
     * @param rootFolder the rootFolder to set
     */
    public void setRootDir(String rootFolder) {
        this.rootDir = new File(rootFolder);
    }

    /**
     * @return
     */
    public String getRootDir() {
        return getRootDirFile().getPath();
    }

    private ProcessingJournalService journalService;

    /**
     * @return the journalService
     */
    public ProcessingJournalService getJournalService() {
        return journalService;
    }

    /**
     * @param journalService the journalService to set
     */
    public void setJournalService(ProcessingJournalService journalService) {
        this.journalService = journalService;
    }

    // Scheduler property
    private Scheduler scheduler;

    /**
     * This service's scheduler
     *
     * @return this service's scheduler
     */
    public Scheduler getScheduler() {
        return this.scheduler;
    }

    /**
     * @param scheduler this service's scheduler
     */
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    // Schedule property
    private Schedule schedule;

    /**
     * The schedule this service will run on
     *
     * @return the schedule this service will run on
     */
    public Schedule getSchedule() {
        return this.schedule;
    }

    /**
     * @param schedule the schedule this service will run on
     */
    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    private TransactionManager transactionManager;

    /**
     * @return the transactionManager
     */
    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    /**
     * @param transactionManager the transactionManager to set
     */
    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    /**
     * Order state resolver
     */
    private OrderStates orderStates;

    /**
     * @return the orderStates
     */
    public OrderStates getOrderStates() {
        return orderStates;
    }

    /**
     * @param orderStates the orderStates to set
     */
    public void setOrderStates(OrderStates orderStates) {
        this.orderStates = orderStates;
    }

    /**
     * BO Order state resolver
     */
    private BOOrderStates boOrderStates;

    /**
     * @return the boOrderStates
     */
    public BOOrderStates getBoOrderStates() {
        return boOrderStates;
    }

    /**
     * @param boOrderStates the boOrderStates to set
     */
    public void setBoOrderStates(BOOrderStates boOrderStates) {
        this.boOrderStates = boOrderStates;
    }

    private String originalMagazin;
    private int jobId;

    /**
     * @return the originalMagazin
     */
    public String getOriginalMagazin() {
        return originalMagazin;
    }

    /**
     * @param originalMagazin the originalMagazin to set
     */
    public void setOriginalMagazin(String originalMagazin) {
        this.originalMagazin = originalMagazin;
    }

    private PayboxPaymentStates payboxPaymentStates;


    /**
     * @return the payboxPaymentStates
     */
    public PayboxPaymentStates getPayboxPaymentStates() {
        return payboxPaymentStates;
    }

    /**
     * @param payboxPaymentStates the payboxPaymentStates to set
     */
    public void setPayboxPaymentStates(PayboxPaymentStates payboxPaymentStates) {
        this.payboxPaymentStates = payboxPaymentStates;
    }

    private PaymentStates paymentStates;

    /**
     * @return the paymentStates
     */
    public PaymentStates getPaymentStates() {
        return paymentStates;
    }

    /**
     * @param paymentStates the paymentStates to set
     */
    public void setPaymentStates(PaymentStates paymentStates) {
        this.paymentStates = paymentStates;
    }

    private PriceListManager mPriceListManager;

    /**
     * @param pPriceListManager the mPriceListManager to set
     */
    public void setPriceListManager(PriceListManager pPriceListManager) {
        mPriceListManager = pPriceListManager;
    }

    /**
     * @return the mPriceListManager
     */
    public PriceListManager getPriceListManager() {
        return mPriceListManager;
    }

    private boolean isTwoPhoneNumbersSend;

    public boolean isTwoPhoneNumbersSend() {
        return isTwoPhoneNumbersSend;
    }

    public void setTwoPhoneNumbersSend(boolean twoPhoneNumbersSend) {
        isTwoPhoneNumbersSend = twoPhoneNumbersSend;
    }

    /**
     * SELECT_STATUS_SQL property
     */
    private static String SELECT_STATUS_SQL =
            "SELECT ID FROM MS_ORDERS_SYNC_JOURNAL WHERE TO_CHAR(LAST_UPDATE, 'DD/MM/YYYY') = TO_CHAR(SYSDATE, 'DD/MM/YYYY') AND STORE_ID = ?";

    /**
     * INSERT_STATUS_SQL property
     */
    private static String INSERT_STATUS_SQL =
            "INSERT INTO MS_ORDERS_SYNC_JOURNAL (LAST_UPDATE, QUANTITY, STORE_ID, STATUS) VALUES(SYSDATE, ?, ?, ?)";

    /**
     * UPDATE_STATUS_SQL property
     */
    private static String UPDATE_STATUS_SQL =
            "UPDATE MS_ORDERS_SYNC_JOURNAL SET QUANTITY = QUANTITY + ?, STATUS = ?, LAST_UPDATE = SYSDATE WHERE TO_CHAR(LAST_UPDATE, 'DD/MM/YYYY') = TO_CHAR(SYSDATE, 'DD/MM/YYYY') AND STORE_ID = ?";

    private DataSource mStockDataSource;

    /**
     * @throws FileNotFoundException
     */
    public void exportOrders() throws IOException {
        OrderLoader loader = new OrderLoader(getRepository());
        loader.setBoOrderStates(getBoOrderStates());
        loader.setOriginalMagazin(getOriginalMagazin());
        loader.setOrderStates(getOrderStates());

        Date startDate = new Date();
        try {
            Iterator<String> magasinIterator = loader.storesIterator();
            while (magasinIterator.hasNext()){
                String magasinId = magasinIterator.next();
                exportStoreOrders(loader, magasinId, startDate);
            }
        } catch (RepositoryException e2) {
            String errorLogFileName = getNextFileName(startDate, ALL_RANGE).replaceAll("\\.txt", ".log");
            PrintWriter log = new PrintWriter(new File(getErrorFolder(), errorLogFileName));
            logFails(log, e2.getMessage(), e2, startDate);
            
            logError(e2.getMessage(), e2);
        }
    }

    /**
     * Export orders for store
     * 
     * @param loader
     * @param magasinId
     * @param startDate
     * @throws FileNotFoundException
     */
    private void exportStoreOrders(OrderLoader loader, String magasinId, Date startDate) throws FileNotFoundException {
        TransactionDemarcation trd = new TransactionDemarcation();
        
        File file = null;
        try {
            trd.begin(getTransactionManager());

            Repository repo =  getMagasinRepository();
            RepositoryView view = repo.getView(MAGASIN_DESCRIPTOR_NAME);
            RqlStatement statement = RqlStatement.parseRqlStatement(GET_MAGASIN_BY_ID_STATEMENT);
            RepositoryItem[] items = statement.executeQuery(view, new Object[] {magasinId});
            String magasinName = null;
            String storeId = null;
            if (items != null & items.length > 0 ){
                magasinName = (String) items[0].getPropertyValue(NOM_PROPERTY_NAME);
                storeId = (String) items[0].getPropertyValue(STORE_ID_PROPERTY_NAME);
            }

            LogSipsLoader sipsLoader = new LogSipsLoader(getSipsRepository());

            RecordFactory rf = new RecordFactory();
            rf.setPayboxPaymentStates(getPayboxPaymentStates());
            rf.setPaymentStates(getPaymentStates());
            rf.setPriceListManager(getPriceListManager());
            rf.setCastVATManager(getCastVATManager());

            int ordersCount = 0;

            List<String> success = new ArrayList<String>();
            List<String> failures = new ArrayList<String>();

            JournalItem jitm = getJournalService().registerStarting(COMMANDE);
            file = new File(getWorkFolder(), getNextFileName(startDate, storeId));

            BeanStringWriter writer = new BeanStringWriter(file);
            writer.write(new CCARecord(jitm.getSequence(), storeId, magasinName));
            int num = 1;

            Iterator<RepositoryItem> it = loader.ordersIterator(magasinId);

            while (it.hasNext()) {
                RepositoryItem itm = it.next();
                ordersCount++;
                if (itm != null) {
                    if (!payboxDirectPlusManager.isPayboxDirectPlusIsActive() ||
                            payboxDirectPlusManager.isOrderTransactionsValid(itm.getRepositoryId())) {
                        num += exportOrder(loader, sipsLoader, rf, success, failures, jitm, writer, itm);
                    }
                }
            }

            writer.write(new CCZRecord(jitm.getSequence(), num + 1));
            writer.close();

            File result = new File(getOutputFolder(), file.getName());
            file.renameTo(result);

            PrintWriter log = new PrintWriter(new File(getArchiveFolder(), file.getName().replaceAll("\\.txt", ".log")));
            logSuccess(log, success, failures, result.getName(), startDate);

            int exportOrdersStatus = failures.isEmpty()?0:1;
            createStatus(ordersCount, storeId, exportOrdersStatus);

        } catch (FileNotFoundException e) {
            if (null != file) {
                PrintWriter log = new PrintWriter(new File(getErrorFolder(), file.getName().replaceAll("\\.txt", ".log")));
                logFails(log, e.getMessage(), e, startDate);
            }

            logError(e.getMessage(), e);
            try {
                trd.getTransaction().setRollbackOnly();
            } catch (Throwable e1) {
            }
        } catch (IOException e) {
            if (null != file) {
                PrintWriter log = new PrintWriter(new File(getErrorFolder(), file.getName().replaceAll("\\.txt", ".log")));
                logFails(log, e.getMessage(), e, startDate);
            }

            logError(e.getMessage(), e);

            if (null != file) {
                file.renameTo(new File(getErrorFolder(), file.getName()));
            }
            try {
                trd.getTransaction().setRollbackOnly();
            } catch (Throwable e1) {
            }
        } catch (RepositoryException e) {
            if (null != file) {
                PrintWriter log = new PrintWriter(new File(getErrorFolder(), file.getName().replaceAll("\\.txt", ".log")));
                logFails(log, e.getMessage(), e, startDate);
            }

            logError(e.getMessage(), e);

            if (null != file) {
                file.renameTo(new File(getErrorFolder(), file.getName()));
            }
            try {
                trd.getTransaction().setRollbackOnly();
            } catch (Throwable e1) {
            }
        } catch (TransactionDemarcationException e) {
            try {
                trd.getTransaction().setRollbackOnly();
            } catch (Throwable e1) {
            }
        } finally {
            try {
                trd.end();
            } catch (Throwable e) {
                logError(e.getMessage(), e);
            }
        }
    }

    /**
     * One order export
     * 
     * @param loader
     * @param sipsLoader
     * @param rf
     * @param success
     * @param failures
     * @param jitm
     * @param writer
     * @param itm
     * @return number of line for exported order
     * @throws RepositoryException
     * @throws IOException
     */
    private int exportOrder(OrderLoader loader, LogSipsLoader sipsLoader, RecordFactory rf, List<String> success, List<String> failures, JournalItem jitm,
            BeanStringWriter writer, RepositoryItem itm) throws RepositoryException, IOException {
        int num = 0;
        CCERecord cce = null;
        try {
            List<CCLRecord> CCLs = new ArrayList<CCLRecord>();
            OrderDetailsLoader detailsLoader = new OrderDetailsLoader(itm);
            Iterator<RepositoryItem> itdet = detailsLoader.iterator();
            long totalPFT = 0, totalPFL = 0;
            while (itdet.hasNext()) {
                RepositoryItem itmdet = itdet.next();
                if (null != itmdet) {
                    List<CCLRecord> ccl = rf.getCCLRecords(itmdet, getProductRepository());
                    for (Iterator<CCLRecord> cclListIt = ccl.iterator(); cclListIt.hasNext(); ) {
                        CCLRecord cclItem = cclListIt.next();
                        totalPFT = totalPFT + cclItem.getPoidsPFT() * cclItem.getQuantity();
                        totalPFL = totalPFL + cclItem.getPoidsPFL() * cclItem.getQuantity();
                    }
                    CCLs.addAll(ccl);
                }
            }

            cce = new CCERecord();

            List<RepositoryItem> sipsitems = new ArrayList<RepositoryItem>();
            Iterator<RepositoryItem> sipsit = sipsLoader.iterator(itm);
            while (sipsit.hasNext()) {
                sipsitems.add(sipsit.next());
            }
            RepositoryItem sipsitem = (0 < sipsitems.size()) ? sipsitems.get(0) : null;

            cce = rf.getCCERecord(itm, sipsitem, totalPFT, totalPFL);

            cce.setIsTwoPhoneNumbersSend(isTwoPhoneNumbersSend);
            if (null != itm.getPropertyValue(PROFILE_ID_PROPERTY_NAME)) {
                RepositoryItem profile =
                        getProfileRepository().getItem((String) itm.getPropertyValue(PROFILE_ID_PROPERTY_NAME), USER_DESCRIPTOR_NAME);

                if (null != profile) {
                    rf.getProfileProperties(cce, profile);
                }
            }

            rf.getPromotionProperties(cce, itm);

            OrderPaymentLoader paymentLoader = new OrderPaymentLoader(itm);
            Iterator<RepositoryItem> itPayment = paymentLoader.iterator();
            List<RepositoryItem> payments = new ArrayList<RepositoryItem>();
            while (itPayment.hasNext()) {
                payments.add(itPayment.next());
            }

            List<CCPRecord> CCPs = rf.getCCPRecords(payments, cce, sipsitems);
            CCRRecord ccr = new CCRRecord(cce.getOrderId(), CCLs.size());

            // Parsing successful. save to file

            writer.write(cce);

            for (CCPRecord ccp : CCPs) {
                writer.write(ccp);
            }

            for (CCLRecord ccl : CCLs) {
                writer.write(ccl);
            }

            writer.write(ccr);

            num = (1 + CCPs.size() + CCLs.size() + 1);

            loader.registerExported(itm, "" + jitm.getSequence());

            success.add(cce.getOrderId());
        } catch (RecordParsingException e) {
            failures.add("order:" + (cce==null ? "null" : cce.getOrderId()) + " " + e.getMessage());
        }
        
        return num;
    }

    /* (non-Javadoc)
      * @see atg.nucleus.GenericService#doStartService()
      */
    @Override
    public void doStartService() throws ServiceException {
        ScheduledJob job = new ScheduledJob(getClass().getSimpleName(),
                "Export orders",
                getAbsoluteName(),
                getSchedule(),
                this,
                ScheduledJob.SCHEDULER_THREAD);
        jobId = getScheduler().addScheduledJob(job);

        super.doStartService();
    }

    /* (non-Javadoc)
      * @see atg.nucleus.GenericService#doStopService()
      */
    @Override
    public void doStopService() throws ServiceException {
        if (jobId > 0) {
            getScheduler().removeScheduledJob(jobId);
        }

        super.doStopService();
    }

    public void performScheduledTask(Scheduler arg0, ScheduledJob arg1) {
        try {
            exportOrders();
        } catch (Throwable e) {
            logError(e);
        }
    }

    private File getRootDirFile() {
        if (!rootDir.exists()) {
            rootDir.mkdir();
        }

        return rootDir;
    }

    private File getErrorFolder() {
        File result = new File(getRootDirFile(), "error");

        if (!result.exists()) {
            result.mkdir();
        }

        return result;
    }

    private File getWorkFolder() {
        File result = new File(getRootDirFile(), "working");

        if (!result.exists()) {
            result.mkdir();
        }

        return result;
    }

    private File getOutputFolder() {
        File result = new File(getRootDirFile(), "output");

        if (!result.exists()) {
            result.mkdir();
        }

        return result;
    }

    private File getArchiveFolder() {
        File result = new File(getRootDirFile(), "archive");

        if (!result.exists()) {
            result.mkdir();
        }

        return result;
    }

    private String getNextFileName(Date date, String magasinId) {
        DateFormat df = new SimpleDateFormat(FILE_NAME_DATE_PATTERN);

        return getFilePrefix() + magasinId + FILE_NAME_SEPARATOR + df.format(date) + TXT_FILE_TYPE;
    }

    public static void logFails(PrintWriter log, String message, Throwable t, Date failsDate) {
        DateFormat df = new SimpleDateFormat(FILE_RECORD_DATETIME_PATTERN);

        log.println("Export Date: " + df.format(failsDate));
        log.println("Status: Failed");
        log.println("Successfully exported messages: 0");
        log.println(message);
        t.printStackTrace(log);

        log.flush();
        log.close();
    }

    private static void logSuccess(PrintWriter log, List<String> success, List<String> failures, String resultFile, Date succesDate) {
        DateFormat df = new SimpleDateFormat(FILE_RECORD_DATETIME_PATTERN);

        log.println("Export Date: " + df.format(succesDate));
        if (failures.size() > 0) {
            log.println("Status: Failed");
        } else {
            log.println("Status: Success");
        }

        log.println("Successfully exported orders: " + success.size());
        if (failures.size() > 0) {
            log.println("Exported id:");
            for (String string : success) {
                log.println("" + string);
            }

            log.println("Number of not exported orders: " + failures.size());

            log.println("Failures:");
            for (String string : failures) {
                log.println("" + string);
            }
        }

        log.println("File to Export: " + resultFile);

        log.flush();
        log.close();
    }

    private void createStatus(int quantity, String storeId, int status) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        if (isLoggingDebug()) {
            logDebug("Creating local prices import status.");
        }
        try {
            con = getStockDataSource().getConnection();
            ps = con.prepareStatement(SELECT_STATUS_SQL);
            ps.setString(1,storeId);
            rs = ps.executeQuery();
            if (rs.next()){
                rs.close();
                ps.close();
                ps = con.prepareStatement(UPDATE_STATUS_SQL);
                ps.setInt(1,quantity);
                ps.setInt(2,status);
                ps.setString(3,storeId);
            } else {
                rs.close();
                ps.close();
                ps = con.prepareStatement(INSERT_STATUS_SQL);
                ps.setInt(1,quantity);
                ps.setString(2,storeId);
                ps.setInt(3,status);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            if (isLoggingError()) {
                logError(e);
            }
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    if (isLoggingError()) {
                        logError(e);
                    }
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    if (isLoggingError()) {
                        logError(e);
                    }
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    if (isLoggingError()) {
                        logError(e);
                    }
                }
            }
        }  // end try-catch-finally

        if (isLoggingDebug()) {
            logDebug("Order status created.");
        }
    }

    public DataSource getStockDataSource() {
        return mStockDataSource;
    }

    public void setStockDataSource(DataSource mStockDataSource) {
        this.mStockDataSource = mStockDataSource;
    }
}
