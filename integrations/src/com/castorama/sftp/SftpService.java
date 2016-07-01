package com.castorama.sftp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import atg.nucleus.GenericService;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class SftpService extends GenericService {
	
	private int bufferSize = 2048;

	/**
	 * @return the bufferSize
	 */
	public int getBufferSize() {
		return bufferSize;
	}

	/**
	 * @param bufferSize the bufferSize to set
	 */
	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}
	
	private int connectionTimeout = 20000;
	
	/**
	 * @return the connectionTimeout
	 */
	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	/**
	 * @param connectionTimeout the connectionTimeout to set
	 */
	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	/**
	 * @param host
	 * @param user
	 * @param password
	 * @param port
	 * @param srcFile
	 * @param dest
	 * @throws IOException
	 */
	public File getFile(String host, int port, String user, final String password, String srcFile, File dest) throws IOException {
		try {
			JSch jsch = new JSch();
			Session session = jsch.getSession(user, host, port);
			session.setTimeout(getConnectionTimeout());
			
			session.setPassword(password);

			UserInfo ui = new UserInfo() {

				public String getPassphrase() {
					return "";
				}

				public String getPassword() {
					return password;
				}

				public boolean promptPassphrase(String arg0) {
					return false;
				}

				public boolean promptPassword(String arg0) {
					return true;
				}

				public boolean promptYesNo(String arg0) {
					return true;
				}

				public void showMessage(String message) {
					logDebug(message);
				}
			};

			session.setUserInfo(ui);
			session.connect();
			try {
				ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
				channel.connect();
				try {
					InputStream is = channel.get(srcFile);
					
					try {
						if (dest.exists() && dest.isDirectory()) {
							String fileName = (new File(srcFile)).getName();
							dest = new File(dest, fileName);
						}
						
						OutputStream os = new FileOutputStream(dest);
						
						try {
							byte[] buffer = new byte[getBufferSize()];
							
							int av;
							do {
								av = is.read(buffer);
								if (av > 0) {
									os.write(buffer, 0, av);
								}
							} while (av > 0);
							
							os.flush();
						} finally {
							os.close();
						}
					} finally {
						is.close();
					}
				} finally {
					channel.disconnect();
				}			
			} finally {
				session.disconnect();
			}
		} catch (Throwable e) {
			throw new IOException(e.getMessage());
		}
		
		return dest;
	}

	public File getFile(String host, String user, String password, String srcFile, File dest) throws IOException {
		return getFile(host, 22, user, password, srcFile, dest);
	}

	
	/**
	 * @param host
	 * @param user
	 * @param password
	 * @param port
	 * @param srcFile
	 * @param dest
	 * @throws IOException
	 */
	public void putFile(String host, int port, String user, final String password, File srcFile, String dest) throws IOException {
		try {
			JSch jsch = new JSch();
			Session session = jsch.getSession(user, host, port);
			session.setTimeout(getConnectionTimeout());
			
			session.setPassword(password);

			UserInfo ui = new UserInfo() {

				public String getPassphrase() {
					return "";
				}

				public String getPassword() {
					return password;
				}

				public boolean promptPassphrase(String arg0) {
					return false;
				}

				public boolean promptPassword(String arg0) {
					return true;
				}

				public boolean promptYesNo(String arg0) {
					return true;
				}

				public void showMessage(String message) {
					logDebug(message);
				}
			};

			session.setUserInfo(ui);
			session.connect();
			try {
				ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
				channel.connect();
				try {
					InputStream is = new FileInputStream(srcFile);
					
					try {
						channel.put(is, dest);
					} finally {
						is.close();
					}
				} finally {
					channel.disconnect();
				}			
			} finally {
				session.disconnect();
			}
		} catch (Throwable e) {
			throw new IOException(e.getMessage());
		}
	}
	
	public void putFile(String host, String user, final String password, File srcFile, String dest) throws IOException {
		putFile(host, 22, user, password, srcFile, dest);
	}

	public void putSSHFile(String host, String user, final String pathPrivateKey, File srcFile, String dest) throws IOException {
		putFile(host, 22, user, pathPrivateKey, srcFile, dest);
	}
	
	public void putSSHFile(String host, int port, String user, final String pathPrivateKey, File srcFile, String dest) throws IOException {
		try {
			JSch jsch = new JSch();
			final byte[] prvkey = readPrivateKey(pathPrivateKey); 
			final byte[] emptyPassPhrase = new byte[0];
			
			jsch.addIdentity(user, prvkey, null, emptyPassPhrase);
			
			Session session = jsch.getSession(user, host, port);
			session.setTimeout(getConnectionTimeout());
			
			UserInfo ui = new UserInfo() {

				public String getPassphrase() {
					return "";
				}

				public String getPassword() {
					return null;
				}

				public boolean promptPassphrase(String arg0) {
					return false;
				}

				public boolean promptPassword(String arg0) {
					return true;
				}

				public boolean promptYesNo(String arg0) {
					return true;
				}

				public void showMessage(String message) {
					logDebug(message);
				}
			};

			session.setUserInfo(ui);
			session.connect();
			try {
				ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
				channel.connect();
				try {
					InputStream is = new FileInputStream(srcFile);
					
					try {
						channel.put(is, dest);
					} finally {
						is.close();
					}
				} finally {
					channel.disconnect();
				}			
			} finally {
				session.disconnect();
			}
		} catch (Throwable e) {
			throw new IOException(e.getMessage());
		}
	}
	
	public void testConnection(String host, int port, String user, final String password) throws IOException {
		try {
			JSch jsch = new JSch();
			Session session = jsch.getSession(user, host, port);
			session.setTimeout(getConnectionTimeout());
			
			session.setPassword(password);

			UserInfo ui = new UserInfo() {

				public String getPassphrase() {
					return "";
				}

				public String getPassword() {
					return password;
				}

				public boolean promptPassphrase(String arg0) {
					return false;
				}

				public boolean promptPassword(String arg0) {
					return true;
				}

				public boolean promptYesNo(String arg0) {
					return true;
				}

				public void showMessage(String message) {
					logDebug(message);
				}
			};

			session.setUserInfo(ui);
			session.connect();
			try {
				ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
				channel.connect();
				channel.disconnect();
			} finally {
				session.disconnect();
			}
		} catch (Throwable e) {
			throw new IOException(e.getMessage());
		}
	}

	public void testConnection(String host, String user, final String password) throws IOException {
		testConnection(host, 22, user, password);
	}
	
	public void testSSHCoonection(String host, int port, String user, final String pathPrivateKey) throws IOException {
		try {
			JSch jsch = new JSch();
			final byte[] prvkey = readPrivateKey(pathPrivateKey); 
			final byte[] emptyPassPhrase = new byte[0];
			
			jsch.addIdentity(user, prvkey, null, emptyPassPhrase);
			
			Session session = jsch.getSession(user, host, port);
			session.setTimeout(getConnectionTimeout());
			
			UserInfo ui = new UserInfo() {

				public String getPassphrase() {
					return "";
				}

				public String getPassword() {
					return null;
				}

				public boolean promptPassphrase(String arg0) {
					return false;
				}

				public boolean promptPassword(String arg0) {
					return true;
				}

				public boolean promptYesNo(String arg0) {
					return true;
				}

				public void showMessage(String message) {
					logDebug(message);
				}
			};

			session.setUserInfo(ui);
			session.connect();
			try {
				ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
				channel.connect();
				try {
					final java.util.Vector files = channel.ls(".");
			        for (Object obj : files) {
			            System.out.println("obj: " + obj);			        }
		        } finally {
					channel.disconnect();
				}			
			} finally {
				session.disconnect();
			}
		} catch (Throwable e) {
			throw new IOException(e.getMessage());
		}
	}
	
	private byte[] readPrivateKey(String pathPrimaryKey) throws IOException {
		FileInputStream fr = new FileInputStream(pathPrimaryKey);

		ByteArrayOutputStream bw = new ByteArrayOutputStream();
		try {
			byte buf[] = new byte[32768];
			int bytesRead;
			while ((bytesRead = fr.read(buf, 0, buf.length)) > 0) {
				bw.write(buf, 0, bytesRead);
			}
		} finally {
			bw.close();
			fr.close();
		}
		return bw.toByteArray();
	}
	
	public static void main(String[] args) throws Exception {
		SftpService service = new SftpService();
		service.testSSHCoonection("92.43.252.26", 22, "castorama", "d:\\CASTORAMA\\trunk\\id_rsa.txt");
	}
	
}
