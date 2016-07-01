package com.castorama.integration.bazaarvoice;

import java.io.IOException;

import com.castorama.sftp.SftpService;

import atg.nucleus.GenericService;

/**
 * @author Andrew_Logvinov
 */
public class AccessCredentials extends GenericService {
	
	private String sftpHost;

	/**
	 * @return the sftpHost
	 */
	public String getSftpHost() {
		return sftpHost;
	}

	/**
	 * @param sftpHost the sftpHost to set
	 */
	public void setSftpHost(String sftpHost) {
		this.sftpHost = sftpHost;
	}

	private String sftpUser;

	/**
	 * @return the sftpUser
	 */
	public String getSftpUser() {
		return sftpUser;
	}

	/**
	 * @param sftpUser the sftpUser to set
	 */
	public void setSftpUser(String sftpUser) {
		this.sftpUser = sftpUser;
	}

	private String sftpPassword;
	
	/**
	 * @return the sftpPassword
	 */
	public String getSftpPassword() {
		return sftpPassword;
	}

	/**
	 * @param sftpPassword the sftpPassword to set
	 */
	public void setSftpPassword(String sftpPassword) {
		this.sftpPassword = sftpPassword;
	}
	
	/**
	 * @throws IOException
	 */
	public void testConnection() throws IOException{
		SftpService service = new SftpService();
		
		service.testConnection(getSftpHost(), getSftpUser(), getSftpPassword());
	}
}
