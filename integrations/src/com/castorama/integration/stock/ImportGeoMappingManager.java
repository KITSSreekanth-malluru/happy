/*
 * Copyright (c) 2010 EPAM Systems.
 * All rights reserved.
 */

package com.castorama.integration.stock;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import javax.sql.DataSource;

import atg.nucleus.ServiceException;

import com.castorama.integration.Constants;
import com.castorama.integration.IntegrationBase;
import com.castorama.integration.util.MiscUtils;
import com.castorama.stockvisualization.RemoteStockCacheAccessor;

/**
 * ToDo: DOCUMENT ME!
 * 
 * @author	EPAM team
 */
public class ImportGeoMappingManager extends IntegrationBase {
	/**
	 * ToDo: DOCUMENT ME!
	 */
	
	private static final String SELECT_GEO_SQL = 
		"SELECT 1 FROM MS_PCODE_TO_STORES WHERE POSTAL_CODE = ?";
	private static final String INSERT_GEO_SQL = 
		"INSERT INTO MS_PCODE_TO_STORES (POSTAL_CODE, STORE_1, STORE_2, STORE_3, STORE_4, " +
		"STORE_5, STORE_6, STORE_7, STORE_8, STORE_9, STORE_10) " +
		"VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String UPDATE_GEO_SQL = 
		"UPDATE MS_PCODE_TO_STORES SET STORE_1 = ?, STORE_2 = ?, STORE_3 = ?, STORE_4 = ?, " +
		"STORE_5 = ?, STORE_6 = ?, STORE_7 = ?, STORE_8 = ?, STORE_9 = ?, STORE_10 = ? "
		+ "WHERE POSTAL_CODE = ?";
	
	/** mDataSource property */
	private DataSource mDataSource;

	/** mKeepInArchiveDays property */
	private int mKeepInArchiveDays = 20;
	
	/** remoteStockCacheAccessor property */
    private RemoteStockCacheAccessor mRemoteStockCacheAccessor;
    
    /** mInProgress property */
    private boolean mInProgress = false;


	/**
	 * Returns dataSource property.
	 * 
	 * @return dataSource property.
	 */
	public DataSource getDataSource() {
		return mDataSource;
	}

	/**
	 * Sets the value of the dataSource property.
	 * 
	 * @param dataSource
	 *            parameter to set.
	 */
	public void setDataSource(final DataSource dataSource) {
		mDataSource = dataSource;
	}

	/**
	 * Returns keepInArchiveDays property.
	 * 
	 * @return keepInArchiveDays property.
	 */
	public int getKeepInArchiveDays() {
		return mKeepInArchiveDays;
	}

	/**
	 * Sets the value of the keepInArchiveDays property.
	 * 
	 * @param keepInArchiveDays
	 *            parameter to set.
	 */
	public void setKeepInArchiveDays(final int keepInArchiveDays) {
		mKeepInArchiveDays = keepInArchiveDays;
	}
	
	/**
     * Gets the remoteStockCacheAccessor property. 
     *
     * @return the remoteStockCacheAccessor
     */
    public final RemoteStockCacheAccessor getRemoteStockCacheAccessor() {
    	return mRemoteStockCacheAccessor;
    }

    /**
     * Sets the remoteStockCacheAccessor property.
     *
     * @param pRemoteStockCacheAccessor the remoteStockCacheAccessor to set
     */
    public final void setRemoteStockCacheAccessor(RemoteStockCacheAccessor pRemoteStockCacheAccessor) {
    	mRemoteStockCacheAccessor = pRemoteStockCacheAccessor;
    }
	
	
	/**
	 * ToDo: DOCUMENT ME!
	 */
	public void importGeoMapping() throws ServiceException {
		if (mInProgress) {
    		throw new ServiceException("Unable to start geo mapping import... It's already running. Please wait...");
    	}
		
		if (isLoggingDebug()) {
			logDebug("ImportGeoMapping....");
		}

		try {
			mInProgress = true;
			
			checkMandatoryFolders();

			File workingFile = null;

			while ((workingFile = getWorkingFile()) != null) {
				if (isLoggingDebug()) {
					logDebug("processing: " + workingFile);
				}

				long startTimeMillis = System.currentTimeMillis();

				boolean success = true;

				if (isLoggingInfo()) {
					logInfo("Data file: " + workingFile.getName());
				}

				processFile(workingFile);

				if (isLoggingInfo()) {
					logInfo("Done. File: " + workingFile.getName() + " Time (seconds): "
							+ ((System.currentTimeMillis() - startTimeMillis) / 1000));
				}

				File destDir = null;

				if (success) {
					destDir = getArchiveDir();
				} else {
					destDir = getErrorDir();
				}

				MiscUtils.copyFile(workingFile, new File(destDir.getAbsolutePath()
						+ File.separator + workingFile.getName()));
				workingFile.delete();
			}
			deleteOldFiles(getArchiveDir());
			deleteOldFiles(getErrorDir());
		} catch (FileNotFoundException e) {
			if (isLoggingError()) {
				logError(e);
			}
		} catch (IOException e) {
			if (isLoggingError()) {
				logError(e);
			}
		} finally {
			mInProgress = false;
		}

		if (isLoggingDebug()) {
			logDebug("finish - ImportGeoMapping");
		}
	}

	/**
	 * ToDo: DOCUMENT ME!
	 * 
	 * @param pWorkingFile
	 *            ToDo: DOCUMENT ME!
	 */
	private void processFile(final File pWorkingFile) {
		Connection con = null;
		PreparedStatement selectPs = null;
		PreparedStatement insertPs = null;
		PreparedStatement updatePs = null;
		ResultSet selectRs = null;
		
		boolean autoCommit = true;
		
		try {
			con = getDataSource().getConnection();
			autoCommit = con.getAutoCommit();
			con.setAutoCommit(false);
			
			selectPs = con.prepareStatement(SELECT_GEO_SQL);
			updatePs = con.prepareStatement(UPDATE_GEO_SQL);
			insertPs = con.prepareStatement(INSERT_GEO_SQL);

			BufferedReader reader = new BufferedReader(new FileReader(pWorkingFile));
			String line = null;
			boolean firstRecord = true;
			while ((line = reader.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line.trim(), ";");
				int count = st.countTokens();

				if (count == 21) {
					if (firstRecord) {
						firstRecord = false;
						continue;
					}

					String postalCode = st.nextToken();
					String storeId1 = st.nextToken();
					String storeId2 = st.nextToken();
					String storeId3 = st.nextToken();
					String storeId4 = st.nextToken();
					String storeId5 = st.nextToken();
					String storeId6 = st.nextToken();
					String storeId7 = st.nextToken();
					String storeId8 = st.nextToken();
					String storeId9 = st.nextToken();
					String storeId10 = st.nextToken();

					try {
						selectPs.setString(1, postalCode);
						selectRs = selectPs.executeQuery();
						
						if (selectRs.next()) {
							updatePs.setString(1, storeId1);
							updatePs.setString(2, storeId2);
							updatePs.setString(3, storeId3);
							updatePs.setString(4, storeId4);
							updatePs.setString(5, storeId5);
							updatePs.setString(6, storeId6);
							updatePs.setString(7, storeId7);
							updatePs.setString(8, storeId8);
							updatePs.setString(9, storeId9);
							updatePs.setString(10, storeId10);
							updatePs.setString(11, postalCode);
							
							updatePs.executeUpdate();
						} else {
							insertPs.setString(1, postalCode);
							insertPs.setString(2, storeId1);
							insertPs.setString(3, storeId2);
							insertPs.setString(4, storeId3);
							insertPs.setString(5, storeId4);
							insertPs.setString(6, storeId5);
							insertPs.setString(7, storeId6);
							insertPs.setString(8, storeId7);
							insertPs.setString(9, storeId8);
							insertPs.setString(10, storeId9);
							insertPs.setString(11, storeId10);
							
							insertPs.executeUpdate();
						}
						
					} catch (SQLException sqlException) {
						if (isLoggingWarning()) {
							logWarning("Unable to import data from line: " 
									+ line, sqlException);
						}
					} finally {
						close(selectRs);
					}
				}
			}

			con.commit();
			
		} catch (IOException e) {
			if (isLoggingError()) {
				logError(e);
			}
		} catch (SQLException e) {
			if (isLoggingError()) {
				logError(e);
			}
		} finally {
			close(selectPs);
			close(updatePs);
			close(insertPs);
			
			if (con != null) {
				try {
					con.setAutoCommit(autoCommit);
					con.close();
				} catch (SQLException e) {
					if (isLoggingError()) {
						logError(e);
					}
				}
			}
		} // end try-catch-finally
		
		if (isLoggingInfo()) {
			logInfo("Geomapping import to database finished. Start reloading cache...  ");
		}
		
		getRemoteStockCacheAccessor().reloadPostalCodesCache();
	}
	
	private void close(Statement statement) {
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				if (isLoggingError()) {
					logError(e);
				}
			}
		}
	}
	
	private void close(ResultSet resultSet) {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
				if (isLoggingError()) {
					logError(e);
				}
			}
		}
	}

	/**
	 * Returns workingFile property.
	 * 
	 * @return workingFile property.
	 * 
	 * @throws IOException
	 *             - exception
	 */
	private File getWorkingFile() throws IOException {
		File inputDir = new File(getRootFolder(), Constants.INPUT_FOLDER);
		File[] inpFiles = inputDir.listFiles();

		File inpFile = null;
		File workFile = null;

		if ((inpFiles != null) && (inpFiles.length > 0)) {
			for (File f : inpFiles) {
				if (f.isFile() && f.exists()) {
					//Temporary we take 1st file 
			        //TODO: Change this!  
					inpFile = f;
					break;
				}
			}
		}

		if (inpFile != null) {
			String name = getFileName(inpFile);
			String workName = name + "_" + Constants.DATE_FORMAT_ARCHIVE.format(new Date())
					+ inpFile.getName().substring(name.length(), inpFile.getName().length());
			workFile = new File(getWorkingDir(), workName);

			MiscUtils.copyFile(inpFile, workFile);

			inpFile.delete();
		}

		return workFile;
	}

	/**
	 * ToDo: DOCUMENT ME!
	 * 
	 * @param dir
	 *            ToDo: DOCUMENT ME!
	 */
	private void deleteOldFiles(final File dir) {
		if (isLoggingDebug()) {
			logDebug("Deleting old files from " + dir.getName());
		}

		File[] listFiles = dir.listFiles();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -getKeepInArchiveDays());

		long timeInMillis = calendar.getTimeInMillis();

		if ((listFiles != null) && (listFiles.length > 0)) {
			for (File f : listFiles) {
				if (f.lastModified() < timeInMillis) {
					f.delete();
				}
			}
		}
	}
}
