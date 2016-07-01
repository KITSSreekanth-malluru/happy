package com.castorama.integration.stock;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * ToDo: DOCUMENT ME!
 *
 * @author  EPAM team
 * @version ToDo: INSERT VERSION NUMBER
 */
public class SizeLimiterInputStream extends FilterInputStream {
    /** maxSize property */
    final long maxSize;

    /** base property */
    final InputStream base;

    /** alreadyRead property */
    long alreadyRead;

    /**
     * Creates a new SizeLimiterInputStream object. ToDo: DOCUMENT ME!
     *
     * @param in      ToDo: DOCUMENT ME!
     * @param maxSize ToDo: DOCUMENT ME!
     */
    public SizeLimiterInputStream(InputStream in, long maxSize) {
        super(in);
        this.maxSize = maxSize;
        alreadyRead = 0;
        base = in;
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @return ToDo: DOCUMENT ME!
     *
     * @throws IOException ToDo: DOCUMENT ME!
     */
    @Override public synchronized int available() throws IOException {
        long a = base.available();
        if ((alreadyRead + a) > maxSize) {
            a = maxSize - alreadyRead;
        }
        return (int) a;
    }

    /**
     * ToDo: DOCUMENT ME!
     */
    @Override public void close() {
        // do nothing
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @return ToDo: DOCUMENT ME!
     */
    @Override public boolean markSupported() {
        return false;
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param readlimit ToDo: DOCUMENT ME!
     */
    @Override public void mark(int readlimit) {
        // do nothing
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @throws IOException ToDo: DOCUMENT ME!
     */
    @Override public void reset() throws IOException {
        // do nothing
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @return ToDo: DOCUMENT ME!
     *
     * @throws IOException  ToDo: DOCUMENT ME!
     * @throws EOFException ToDo: DOCUMENT ME!
     */
    @Override public synchronized int read() throws IOException {
        if (alreadyRead >= maxSize) {
            throw new EOFException();
        }
        int r = base.read();
        alreadyRead += 1;
        return r;
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param  b ToDo: DOCUMENT ME!
     *
     * @return ToDo: DOCUMENT ME!
     *
     * @throws IOException ToDo: DOCUMENT ME!
     */
    @Override public synchronized int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param  b   ToDo: DOCUMENT ME!
     * @param  off ToDo: DOCUMENT ME!
     * @param  len ToDo: DOCUMENT ME!
     *
     * @return ToDo: DOCUMENT ME!
     *
     * @throws IOException ToDo: DOCUMENT ME!
     */
    @Override public synchronized int read(byte[] b, int off, int len) throws IOException {
        if (alreadyRead >= maxSize) {
            return -1;
        }
        if ((alreadyRead + len) > maxSize) {
            len = (int) (maxSize - alreadyRead);
        }
        int r = base.read(b, off, len);
        alreadyRead += r;
        return r;
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param  n ToDo: DOCUMENT ME!
     *
     * @return ToDo: DOCUMENT ME!
     *
     * @throws IOException ToDo: DOCUMENT ME!
     */
    @Override public synchronized long skip(long n) throws IOException {
        if (n < 0) {
            return 0;
        }
        if (alreadyRead >= maxSize) {
            return 0;
        }
        if ((alreadyRead + n) > maxSize) {
            n = maxSize - alreadyRead;
        }
        long r = base.skip(n);
        alreadyRead += r;
        return r;
    }

}
