package com.castorama.integration.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.ice.tar.TarEntry;
import com.ice.tar.TarOutputStream;

public class MiscUtils {

	/**
	 * Copy file
	 * 
	 * @param src
	 *            the source file
	 * @param dst
	 *            the destination file
	 * @throws IOException
	 */
	public static void copyFile(File src, File dst) throws IOException {
		FileInputStream fr = new FileInputStream(src);
		String dstPath = dst.getAbsolutePath();
		if (dst.exists() && !dst.canWrite()) {
			dst.delete();
		}
		FileOutputStream fw = new FileOutputStream(dstPath);
		try {
			byte buf[] = new byte[32768];
			int bytesRead;
			while ((bytesRead = fr.read(buf, 0, buf.length)) > 0) {
				fw.write(buf, 0, bytesRead);
			}
		} finally {
			fw.close();
			fr.close();
		}
	}

	/**
	 * Archiving file
	 * 
	 * @param source
	 *            the file
	 * @param dstZip
	 *            name of archive
	 * @throws Exception
	 */
	public static void zippingFile(File source, String dstZip) throws Exception {
		FileInputStream in = null;
		ZipOutputStream out = null;
		try {
			in = new FileInputStream(source);
			out = new ZipOutputStream(new FileOutputStream(dstZip));

			out.setMethod(ZipOutputStream.DEFLATED);
			out.setLevel(Deflater.DEFAULT_COMPRESSION);

			ZipEntry zipEntry = new ZipEntry(source.getName());
			out.putNextEntry(zipEntry);
			zipEntry.setTime(source.lastModified());

			byte buf[] = new byte[32768];
			int bytesRead;
			while ((bytesRead = in.read(buf, 0, buf.length)) > 0) {
				out.write(buf, 0, bytesRead);
			}

		} finally {
			if (in != null)
				in.close();
			if (out != null) {
				out.closeEntry();
				out.close();
			}
		}
	}

	/**
	 * Archiving file in directory
	 * 
	 * @param dir
	 *            the source directory
	 * @param dstZip
	 *            name of archive file
	 * @throws Exception
	 */
	public static void zippingFiles(File dir, String dstZip) throws Exception {
		ZipOutputStream out = null;
		File[] files = dir.listFiles();

		if (files == null || files.length == 0) {
			return;
		}

		try {
			out = new ZipOutputStream(new FileOutputStream(dstZip));
			out.setMethod(ZipOutputStream.DEFLATED);
			out.setLevel(Deflater.BEST_COMPRESSION);
			FileInputStream in = null;
			for (File file : files) {
				try {
					in = new FileInputStream(file);
					ZipEntry zipEntry = new ZipEntry(file.getName());
					out.putNextEntry(zipEntry);
					zipEntry.setTime(file.lastModified());

					byte buf[] = new byte[32768];
					int bytesRead;
					while ((bytesRead = in.read(buf, 0, buf.length)) > 0) {
						out.write(buf, 0, bytesRead);
					}
				} finally {
					if (in != null)
						in.close();
					out.closeEntry();
				}
			}

		} finally {
			if (out != null)
				out.close();
		}
	}

	/**
	 * Remove folder
	 * 
	 * @param folder
	 * @return
	 */
	public static boolean deleteFolder(File folder) {
		boolean bRes = true;
		try {
			File[] list = folder.listFiles();
			if (list != null) {
				for (int i = 0; i < list.length; i++) {
					if (list[i].isDirectory())
						bRes = deleteFolder(list[i]) && bRes;
					else
						bRes = list[i].delete() && bRes;
				}
			}
			bRes = folder.delete() && bRes;
		} catch (Exception exc) {
			bRes = false;
			exc.printStackTrace();
		}
		return bRes;
	}
	
	/**
	 * Replace special characters for Kitchen Cusine
	 * @param s the source string
	 * @return string
	 */
	public static String encodeString(String s) {
		String prefix = "&#";
		String semicolon = ";";
		StringBuilder sb = null;
		if (s != null) {
			sb = new StringBuilder();;
			for (int i=0; i < s.length(); i++) {
				int codePoint = s.codePointAt(i);
				if ((codePoint >= 192 && codePoint <= 255) || (codePoint == 38)
						|| (codePoint == 60) || (codePoint == 62) || (codePoint == 376)) {
					sb.append(prefix).append(codePoint).append(semicolon);
				} else {
					sb.append(s.charAt(i));
				}
			}
		}
		return (sb != null && sb.toString().length() > 0 ? sb.toString() : s);
	}
	/**
	 * Archiving file
	 * 
	 * @param source
	 *            the file
	 * @param dstZip
	 *            name of archive
	 * @throws Exception
	 */
	public static String GZIPpingFile(File source, String dstFileName) throws Exception {
	    TarCompressFile(source, dstFileName);
	    
	    String dstZip = dstFileName + ".Z";
	    
	    FileInputStream in = null;
		GZIPOutputStream out = null;
		try {
			in = new FileInputStream(dstFileName);
			out = new GZIPOutputStream(new FileOutputStream(dstZip));

			byte buf[] = new byte[32768];
			int bytesRead;
			while ((bytesRead = in.read(buf, 0, buf.length)) > 0) {
				out.write(buf, 0, bytesRead);
			}

		} finally {
			if (in != null)
				in.close();
			if (out != null) {
				out.finish();
				out.close();
			}
		}
		return dstZip;
	}
	
	public static String GZIPCompressDir(File dir, String dstFileName) throws Exception {
		TarCompressFiles(dir, dstFileName);
		
		String dstGZip = dstFileName + ".Z";
		
		FileInputStream in = null;
		GZIPOutputStream out = null;
		try {
			in = new FileInputStream(dstFileName);
			out = new GZIPOutputStream(new FileOutputStream(dstGZip));

			byte buf[] = new byte[32768];
			int bytesRead;
			while ((bytesRead = in.read(buf, 0, buf.length)) > 0) {
				out.write(buf, 0, bytesRead);
			}

		} finally {
			if (in != null)
				in.close();
			if (out != null) {
				out.finish();
				out.close();
			}
		}
		return dstGZip;
	}

	public static void TarCompressFiles(File dir, String dstZip) throws Exception {
		TarOutputStream out = null;
		File[] files = dir.listFiles();

		if (files == null || files.length == 0) {
			return;
		}

		try {
			out = new TarOutputStream(new FileOutputStream(dstZip));
			FileInputStream in = null;
			for (File file : files) {
				try {
					in = new FileInputStream(file);
					TarEntry tarEntry = new TarEntry(file.getName());
					tarEntry.setModTime(file.lastModified());
					tarEntry.setSize(file.length());
					out.putNextEntry(tarEntry);

					byte buf[] = new byte[32768];
					int bytesRead;
					while ((bytesRead = in.read(buf, 0, buf.length)) > 0) {
						out.write(buf, 0, bytesRead);
					}
				} finally {
					if (in != null)
						in.close();
					out.closeEntry();
				}
			}

		} finally {
			if (out != null)
				out.close();
		}
	}
	
	public static void TarCompressFile(File source, String dstZip) throws Exception {
		FileInputStream in = null;
		TarOutputStream out = null;
		try {
			in = new FileInputStream(source);
			out = new TarOutputStream(new FileOutputStream(dstZip));

			TarEntry tarEntry = new TarEntry(source.getName());
			tarEntry.setModTime(source.lastModified());
			tarEntry.setSize(source.length());
			out.putNextEntry(tarEntry);

			byte buf[] = new byte[32768];
			int bytesRead;
			while ((bytesRead = in.read(buf, 0, buf.length)) > 0) {
				out.write(buf, 0, bytesRead);
			}

		} finally {
			if (in != null)
				in.close();
			if (out != null) {
				out.closeEntry();
				out.close();
			}
		}
	}
  
  public static String excetionAsString(Exception e) {
    String result;
    try {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw, true);
      e.printStackTrace(pw);
      pw.flush();
      sw.flush();
      result = sw.toString().substring(0, 1024);
    } catch (Exception ex) {
      result = "";
    }
    return result;
  }
}
