package com.castorama.integration.order.exp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

public class ObjectStringWriter {
	
	private static final String ENCODING = "UTF-8";
	
	private final BufferedOutputStream os;

	public ObjectStringWriter(BufferedOutputStream os) {
		this.os = os;
	}
	
	public ObjectStringWriter(File file) throws FileNotFoundException {
		this.os = new BufferedOutputStream(new FileOutputStream(file));
	}
	
	public void write(Object objectToWrite) throws IOException {
		if(objectToWrite instanceof Collection) {
			for(Object info : (Collection) objectToWrite) {
				os.write(info.toString().getBytes(ENCODING));
			}
		} else {
			os.write(objectToWrite.toString().getBytes(ENCODING));
		}
	}
	
	public void closeWriter() throws IOException {
		if(os != null) {
			os.close();
		}
	}
	
}
