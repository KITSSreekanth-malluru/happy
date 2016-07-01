package com.castorama.integration.stock;

import java.io.IOException;
import java.io.EOFException;
import java.io.InputStream;
import java.io.FilterInputStream;

/**
 * This class decompresses an input stream containing data compressed with the
 * unix "compress" utility (LZC, a LZW variant).
 */
public class UncompressInputStream extends FilterInputStream {
    // string table stuff
    /** TBL_CLEAR property */
    private static final int TBL_CLEAR = 0x100;

    /** TBL_FIRST property */
    private static final int TBL_FIRST = TBL_CLEAR + 1;

    /** EXTRA property */
    private static final int EXTRA = 64;

    /** LZW_MAGIC property */
    private static final int LZW_MAGIC = 0x1f9d;

    /** MAX_BITS property */
    private static final int MAX_BITS = 16;

    /** INIT_BITS property */
    private static final int INIT_BITS = 9;

    /** HDR_MAXBITS property */
    private static final int HDR_MAXBITS = 0x1f;

    /** HDR_EXTENDED property */
    private static final int HDR_EXTENDED = 0x20;

    /** HDR_FREE property */
    private static final int HDR_FREE = 0x40;

    /** HDR_BLOCK_MODE property */
    private static final int HDR_BLOCK_MODE = 0x80;

    /** one property */
    byte[] one = new byte[1];

    /** tab_prefix property */
    private int[] tab_prefix;

    /** tab_suffix property */
    private byte[] tab_suffix;

    /** zeros property */
    private int[] zeros = new int[256];

    /** stack property */
    private byte[] stack;

    // various state
    /** block_mode property */
    private boolean block_mode;

    /** n_bits property */
    private int n_bits;

    /** maxbits property */
    private int maxbits;

    /** maxmaxcode property */
    private int maxmaxcode;

    /** maxcode property */
    private int maxcode;

    /** bitmask property */
    private int bitmask;

    /** oldcode property */
    private int oldcode;

    /** finchar property */
    private byte finchar;

    /** stackp property */
    private int stackp;

    /** free_ent property */
    private int free_ent;

    // input buffer
    /** data property */
    private byte[] data = new byte[10000];

    /** bit_pos property */
    private int bit_pos = 0, end = 0, got = 0;

    /** eof property */
    private boolean eof = false;

    /**
     * Creates a new UncompressInputStream object. ToDo: DOCUMENT ME!
     *
     * @param     is the input stream to decompress
     *
     * @exception IOException if the header is malformed
     */
    public UncompressInputStream(InputStream is) throws IOException {
        super(is);
        parse_header();
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @return ToDo: DOCUMENT ME!
     *
     * @throws IOException ToDo: DOCUMENT ME!
     */
    public synchronized int read() throws IOException {
        int b = in.read(one, 0, 1);
        if (b == 1) {
            return (one[0] & 0xff);
        } else {
            return -1;
        }
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param  buf ToDo: DOCUMENT ME!
     * @param  off ToDo: DOCUMENT ME!
     * @param  len ToDo: DOCUMENT ME!
     *
     * @return ToDo: DOCUMENT ME!
     *
     * @throws IOException ToDo: DOCUMENT ME!
     */
    public synchronized int read(byte[] buf, int off, int len) throws IOException {
        if (eof) {
            return -1;
        }
        int start = off;

        /* Using local copies of various variables speeds things up by as
         * much as 30% !
         */
        int[] l_tab_prefix = tab_prefix;
        byte[] l_tab_suffix = tab_suffix;
        byte[] l_stack = stack;
        int l_n_bits = n_bits;
        int l_maxcode = maxcode;
        int l_maxmaxcode = maxmaxcode;
        int l_bitmask = bitmask;
        int l_oldcode = oldcode;
        byte l_finchar = finchar;
        int l_stackp = stackp;
        int l_free_ent = free_ent;
        byte[] l_data = data;
        int l_bit_pos = bit_pos;

        // empty stack if stuff still left

        int s_size = l_stack.length - l_stackp;
        if (s_size > 0) {
            int num = (s_size >= len) ? len : s_size;
            System.arraycopy(l_stack, l_stackp, buf, off, num);
            off += num;
            len -= num;
            l_stackp += num;
        }

        if (len == 0) {
            stackp = l_stackp;
            return off - start;
        }

// loop, filling local buffer until enough data has been decompressed

main_loop:
        do {
            if (end < EXTRA) {
                fill();
            }

            int bit_in = (got > 0) ? ((end - (end % l_n_bits)) << 3) : ((end << 3) - (l_n_bits - 1));

            while (l_bit_pos < bit_in) {
                // check for code-width expansion

                if (l_free_ent > l_maxcode) {
                    int n_bytes = l_n_bits << 3;
                    l_bit_pos = (l_bit_pos - 1) + n_bytes - ((l_bit_pos - 1 + n_bytes) % n_bytes);

                    l_n_bits++;
                    l_maxcode = (l_n_bits == maxbits) ? l_maxmaxcode : ((1 << l_n_bits) - 1);

                    l_bitmask = (1 << l_n_bits) - 1;
                    l_bit_pos = resetbuf(l_bit_pos);
                    continue main_loop;
                }

                // read next code

                int pos = l_bit_pos >> 3;
                int code =
                    (((l_data[pos] & 0xFF) | ((l_data[pos + 1] & 0xFF) << 8) | ((l_data[pos + 2] & 0xFF) << 16)) >>
                     (l_bit_pos & 0x7)) & l_bitmask;
                l_bit_pos += l_n_bits;

                // handle first iteration

                if (l_oldcode == -1) {
                    if (code >= 256) {
                        throw new IOException("corrupt input: " + code + " > 255");
                    }
                    l_finchar = (byte) (l_oldcode = code);
                    buf[off++] = l_finchar;
                    len--;
                    continue;
                }

                // handle CLEAR code

                if ((code == TBL_CLEAR) && block_mode) {
                    System.arraycopy(zeros, 0, l_tab_prefix, 0, zeros.length);
                    l_free_ent = TBL_FIRST - 1;

                    int n_bytes = l_n_bits << 3;
                    l_bit_pos = (l_bit_pos - 1) + n_bytes - ((l_bit_pos - 1 + n_bytes) % n_bytes);
                    l_n_bits = INIT_BITS;
                    l_maxcode = (1 << l_n_bits) - 1;
                    l_bitmask = l_maxcode;

                    l_bit_pos = resetbuf(l_bit_pos);
                    continue main_loop;
                }

                // setup

                int incode = code;
                l_stackp = l_stack.length;

                // Handle KwK case

                if (code >= l_free_ent) {
                    if (code > l_free_ent) {
                        throw new IOException("corrupt input: code=" + code + ", free_ent=" + l_free_ent);
                    }

                    l_stack[--l_stackp] = l_finchar;
                    code = l_oldcode;
                }

                // Generate output characters in reverse order

                while (code >= 256) {
                    l_stack[--l_stackp] = l_tab_suffix[code];
                    code = l_tab_prefix[code];
                }
                l_finchar = l_tab_suffix[code];
                buf[off++] = l_finchar;
                len--;

                // And put them out in forward order

                s_size = l_stack.length - l_stackp;
                int num = (s_size >= len) ? len : s_size;
                System.arraycopy(l_stack, l_stackp, buf, off, num);
                off += num;
                len -= num;
                l_stackp += num;

                // generate new entry in table

                if (l_free_ent < l_maxmaxcode) {
                    l_tab_prefix[l_free_ent] = l_oldcode;
                    l_tab_suffix[l_free_ent] = l_finchar;
                    l_free_ent++;
                }

                // Remember previous code

                l_oldcode = incode;

                // if output buffer full, then return

                if (len == 0) {
                    n_bits = l_n_bits;
                    maxcode = l_maxcode;
                    bitmask = l_bitmask;
                    oldcode = l_oldcode;
                    finchar = l_finchar;
                    stackp = l_stackp;
                    free_ent = l_free_ent;
                    bit_pos = l_bit_pos;

                    return off - start;
                }
            }

            l_bit_pos = resetbuf(l_bit_pos);
        } while (got > 0);

        n_bits = l_n_bits;
        maxcode = l_maxcode;
        bitmask = l_bitmask;
        oldcode = l_oldcode;
        finchar = l_finchar;
        stackp = l_stackp;
        free_ent = l_free_ent;
        bit_pos = l_bit_pos;

        eof = true;
        return off - start;
    }

    /**
     * Moves the unread data in the buffer to the beginning and resets the
     * pointers.
     *
     * @param  bit_pos ToDo: DOCUMENT ME!
     *
     * @return ToDo: DOCUMENT ME!
     */
    private final int resetbuf(int bit_pos) {
        int pos = bit_pos >> 3;
        System.arraycopy(data, pos, data, 0, end - pos);
        end -= pos;
        return 0;
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @throws IOException ToDo: DOCUMENT ME!
     */
    private final void fill() throws IOException {
        got = in.read(data, end, data.length - 1 - end);
        if (got > 0) {
            end += got;
        }
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param  num ToDo: DOCUMENT ME!
     *
     * @return ToDo: DOCUMENT ME!
     *
     * @throws IOException ToDo: DOCUMENT ME!
     */
    public synchronized long skip(long num) throws IOException {
        byte[] tmp = new byte[(int) num];
        int got = read(tmp, 0, (int) num);

        if (got > 0) {
            return (long) got;
        } else {
            return 0L;
        }
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @return ToDo: DOCUMENT ME!
     *
     * @throws IOException ToDo: DOCUMENT ME!
     */
    public synchronized int available() throws IOException {
        if (eof) {
            return 0;
        }

        return in.available();
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @throws IOException  ToDo: DOCUMENT ME!
     * @throws EOFException ToDo: DOCUMENT ME!
     */
    private void parse_header() throws IOException {
        // read in and check magic number

        int t = in.read();
        if (t < 0) {
            throw new EOFException("Failed to read magic number");
        }
        int magic = (t & 0xff) << 8;
        t = in.read();
        if (t < 0) {
            throw new EOFException("Failed to read magic number");
        }
        magic += t & 0xff;
        if (magic != LZW_MAGIC) {
            throw new IOException("Input not in compress format (read " + "magic number 0x" +
                                  Integer.toHexString(magic) + ")");
        }

        // read in header byte

        int header = in.read();
        if (header < 0) {
            throw new EOFException("Failed to read header");
        }

        block_mode = (header & HDR_BLOCK_MODE) > 0;
        maxbits = header & HDR_MAXBITS;

        if (maxbits > MAX_BITS) {
            throw new IOException("Stream compressed with " + maxbits + " bits, but can only handle " + MAX_BITS +
                                  " bits");
        }

        if ((header & HDR_EXTENDED) > 0) {
            throw new IOException("Header extension bit set");
        }

        if ((header & HDR_FREE) > 0) {
            throw new IOException("Header bit 6 set");
        }

        // initialize stuff

        maxmaxcode = 1 << maxbits;
        n_bits = INIT_BITS;
        maxcode = (1 << n_bits) - 1;
        bitmask = maxcode;
        oldcode = -1;
        finchar = 0;
        free_ent = block_mode ? TBL_FIRST : 256;

        tab_prefix = new int[1 << maxbits];
        tab_suffix = new byte[1 << maxbits];
        stack = new byte[1 << maxbits];
        stackp = stack.length;

        for (int idx = 255; idx >= 0; idx--) {
            tab_suffix[idx] = (byte) idx;
        }
    }
}
