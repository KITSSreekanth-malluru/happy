/*
 * Copyright (c) 2010 EPAM Systems.
 * All rights reserved.
 */

package com.castorama.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * Helper class to encode/decode data.
 *
 * @author Aliaksandr Surma
  */
public class HexEncoder {
  protected final byte[] encodingTable = {
      (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5', (byte) '6', (byte) '7', (byte) '8',
      (byte) '9', (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f'
    };

  /*
   * set up the decoding table.
   */
  protected final byte[] decodingTable = new byte[128];

  /**
   * ToDo: DOCUMENT ME!
   */
  protected void initialiseDecodingTable() {
    for(int i = 0; i < encodingTable.length; i++) {
      decodingTable[encodingTable[i]] = (byte) i;
    }

    decodingTable['A'] = decodingTable['a'];
    decodingTable['B'] = decodingTable['b'];
    decodingTable['C'] = decodingTable['c'];
    decodingTable['D'] = decodingTable['d'];
    decodingTable['E'] = decodingTable['e'];
    decodingTable['F'] = decodingTable['f'];
  }

  /**
   * Creates a new HexEncoder object.
   */
  public HexEncoder() {
    initialiseDecodingTable();
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @param data ToDo: DOCUMENT ME!
   * @param off ToDo: DOCUMENT ME!
   * @param length ToDo: DOCUMENT ME!
   *
   * @return ToDo: DOCUMENT ME!
   *
   * @throws IOException ToDo: DOCUMENT ME!
   */
  public String encode(final byte[] data, final int off, final int length)
    throws IOException {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    encode(data, off, length, os);
    os.close();

    return new String(os.toByteArray());
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @param data ToDo: DOCUMENT ME!
   *
   * @return ToDo: DOCUMENT ME!
   *
   * @throws IOException ToDo: DOCUMENT ME!
   */
  public String encode(final byte[] data) throws IOException {
    return encode(data, 0, data.length);
  }

  /**
   * encode the input data producing a Hex output stream.
   *
   * @return the number of bytes produced.
   */
  public int encode(final byte[] data, final int off, final int length, final OutputStream out)
    throws IOException {
    for(int i = off; i < (off + length); i++) {
      int v = data[i] & 0xff;

      out.write(encodingTable[(v >>> 4)]);
      out.write(encodingTable[v & 0xf]);
    }

    return length * 2;
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @param c ToDo: DOCUMENT ME!
   *
   * @return ToDo: DOCUMENT ME!
   */
  private boolean ignore(final char c) {
    return ((c == '\n') || (c == '\r') || (c == '\t') || (c == ' '));
  }

  /**
   * decode the Hex encoded byte data writing it to the given output stream, whitespace characters will be ignored.
   *
   * @return the number of bytes produced.
   */
  public int decode(final byte[] data, final int off, final int length, final OutputStream out)
    throws IOException {
    byte b1;
    byte b2;
    int outLen = 0;

    int end = off + length;

    while(end > off) {
      if(!ignore((char) data[end - 1])) {
        break;
      }

      end--;
    }

    int i = off;

    while(i < end) {
      while((i < end) && ignore((char) data[i])) {
        i++;
      }

      b1 = decodingTable[data[i++]];

      while((i < end) && ignore((char) data[i])) {
        i++;
      }

      b2 = decodingTable[data[i++]];

      out.write((b1 << 4) | b2);

      outLen++;
    }

    return outLen;
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @param data ToDo: DOCUMENT ME!
   *
   * @return ToDo: DOCUMENT ME!
   *
   * @throws IOException ToDo: DOCUMENT ME!
   */
  public byte[] decode(final String data) throws IOException {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    decode(data.getBytes("UTF8"), 0, data.getBytes("UTF8").length, os);

    byte[] bytes = os.toByteArray();

    return bytes;
  }

  /**
   * decode the Hex encoded String data writing it to the given output stream, whitespace characters will be ignored.
   *
   * @return the number of bytes produced.
   */
  public int decode(final String data, final OutputStream out)
    throws IOException {
    byte b1;
    byte b2;
    int length = 0;

    int end = data.length();

    while(end > 0) {
      if(!ignore(data.charAt(end - 1))) {
        break;
      }

      end--;
    }

    int i = 0;

    while(i < end) {
      while((i < end) && ignore(data.charAt(i))) {
        i++;
      }

      b1 = decodingTable[data.charAt(i++)];

      while((i < end) && ignore(data.charAt(i))) {
        i++;
      }

      b2 = decodingTable[data.charAt(i++)];

      out.write((b1 << 4) | b2);

      length++;
    }

    return length;
  }
}
