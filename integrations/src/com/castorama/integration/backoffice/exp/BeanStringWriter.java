/**
 * 
 */
package com.castorama.integration.backoffice.exp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Andrew_Logvinov
 *
 */
class BeanStringWriter {
	
	private final String charset = "ISO8859_1";
	
	private final OutputStream os;

	public BeanStringWriter(File file) throws FileNotFoundException {
		os = new BufferedOutputStream(new FileOutputStream(file));
	}

	public void write(Object bean) throws IOException {
		os.write(bean.toString().getBytes(charset));
	}

	public void close() throws IOException {
		os.flush();
		os.close();
	}
}
