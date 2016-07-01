package com.castorama.integration;

import java.io.File;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.transaction.TransactionManager;

import atg.core.util.StringUtils;
import atg.nucleus.GenericService;
import atg.repository.MutableRepository;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

public class ExportManagerBase extends GenericService {

	protected static final int DEFAULT_START_FROM = 0;
	protected static final int DEFAULT_CHUNK_SIZE = 5000;
	protected static final String PREFIX_CAST0 = "Casto";
	
	protected static final String LOG_EXPORT_DATE = "Export Date: ";
	protected static final String LOG_FILE_LIST_EXPORT = "File List to Export:";
	protected static final String LOG_NUMBER_SKUS_SUCCESS = "Number of product skus exported successfully: ";
	protected static final String LOG_NUMBER_SKUS_FAIL = "Number of failed product skus: ";
	protected static final String LOG_LIST_SKUS_FAIL = "List of failed product skus: ";

	protected static final String SUCCESS = "succes";

	protected File workingDir;
	protected File outputDir;
	protected File errorDir;
	protected File archiveDir;
	
	//ProductCatalog repository
	private Repository repository = null;
	//root folder
	private String rootDir;
	
	/**
	 * The ProductCatalog repository that holds the information
	 * 
	 * @return the repository that holds the information
	 */
	public Repository getRepository() {
		return repository;
	}

	/**
	 * @param repository
	 *            set the ProductCatalog repository that holds the information
	 */
	public void setRepository(Repository repository) {
		this.repository = repository;
	}

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
	
	protected RepositoryItem[] getChunkProducts(String filter, int startPosition, int portion) throws RepositoryException {
		if (isLoggingDebug()) {
			logDebug("getChunkProducts: startPosition=" + startPosition + "; portion=" + portion + "; filter=" + filter);
		}
		MutableRepository mutableRepos = (MutableRepository) getRepository();
		String rqlQuery = filter + " RANGE ?0+";

		RepositoryView view = mutableRepos.getView(Constants.ITEM_CASTO_SKU);
		Object[] rqlParams;
		RqlStatement rqlStatement;
		if (portion > 0) {
			rqlParams = new Object[2];
			rqlParams[0] = startPosition;
			rqlParams[1] = portion;
			rqlStatement = RqlStatement.parseRqlStatement(rqlQuery + "?1");
		} else {
			rqlParams = new Object[1];
			rqlParams[0] = startPosition;
			rqlStatement = RqlStatement.parseRqlStatement(rqlQuery);
		}

		return rqlStatement.executeQueryUncached(view, rqlParams);
	}
	
	protected int countCastoSku(String filter) throws RepositoryException {
		MutableRepository mutableRepos = (MutableRepository) getRepository();
		RepositoryView view = mutableRepos.getView(Constants.ITEM_CASTO_SKU);
		RqlStatement requestRQLCount = RqlStatement.parseRqlStatement(filter);
		return requestRQLCount.executeCountQuery(view, null);
	}
	
	protected String removeLineTerminators(String inputStr) {
		if (inputStr == null) {
			return "";
		}
		String patternStr = "(\r\n|\r|\n|\n\r)";
		String replaceStr = " ";
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(inputStr);
		return matcher.replaceAll(replaceStr);
	}

	protected String genFileName(String prefix, String suffix) {
		return prefix + Constants.DATE_FORMAT_YYYYMMDD.format(new Date()) + suffix;
	}
	
	protected void checkFolders() {
		File rootDir = new File(getRootDir());
		if (!rootDir.exists()) {
			rootDir.mkdir();
		}
		workingDir = new File(rootDir, Constants.WORKING_FOLDER);
		if (!workingDir.exists()) {
			workingDir.mkdir();
		}

		errorDir = new File(rootDir, Constants.ERROR_FOLDER);
		if (!errorDir.exists()) {
			errorDir.mkdir();
		}

		archiveDir = new File(rootDir, Constants.ARCHIVE_FOLDER);
		if (!archiveDir.exists()) {
			archiveDir.mkdir();
		}
	}
	
	protected String getMarqueUrl(RepositoryItem skuItem) throws RepositoryException {
		String marqueUrl = null;
		String propMarcue = (String) skuItem.getPropertyValue(Constants.PROPERTY_MARQUE_COMMERCIALE);
		if (!StringUtils.isEmpty(propMarcue)) {
			RepositoryItem itemMarque = getRepository().getItem(propMarcue, Constants.ITEM_MARQUE);
			if (itemMarque != null) {
				RepositoryItem itemMedia = (RepositoryItem) itemMarque.getPropertyValue(Constants.PROPERTY_MEDIA_MARQUE);
				if (itemMedia != null) {
					marqueUrl = (String) itemMedia.getPropertyValue(Constants.PROPERTY_URL);
				}
			}
		}
		return marqueUrl;
	}

}
