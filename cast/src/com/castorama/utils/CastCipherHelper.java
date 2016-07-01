/*
 * Copyright (c) 2010 EPAM Systems.
 * All rights reserved.
 */

package com.castorama.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import atg.core.util.Base64;


/**
 * Helper class to encode/decode URL parameters.
 *
 * @author Aliaksandr Surma
  */
public class CastCipherHelper {
  private String mKeyMaterial = "973677f046fe237161ee1f570655f1a7";
  private String mKeyAlgorithm = "AES";
  private Encrypter mEncrypter;

  /**
   * ToDo: DOCUMENT ME!
   *
   * @return ToDo: DOCUMENT ME!
   */
  public String getKeyMaterial() {
    return mKeyMaterial;
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @param pKeyMaterial ToDo: DOCUMENT ME!
   */
  public void setKeyMaterial(final String pKeyMaterial) {
    mKeyMaterial = pKeyMaterial;
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @return ToDo: DOCUMENT ME!
   */
  public String getKeyAlgorithm() {
    return mKeyAlgorithm;
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @param pKeyAlgorithm ToDo: DOCUMENT ME!
   */
  public void setKeyAlgorithm(final String pKeyAlgorithm) {
    mKeyAlgorithm = pKeyAlgorithm;
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @return ToDo: DOCUMENT ME!
   *
   * @throws IOException ToDo: DOCUMENT ME!
   */
  private Encrypter getEncrypter() throws IOException {
    if(mEncrypter == null) {
      HexEncoder hex = new HexEncoder();
      SecretKey secretKey = new SecretKeySpec(hex.decode(mKeyMaterial), mKeyAlgorithm);
      mEncrypter = new Encrypter(secretKey);
    }

    return mEncrypter;
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @param bytes ToDo: DOCUMENT ME!
   *
   * @return ToDo: DOCUMENT ME!
   *
   * @throws IOException ToDo: DOCUMENT ME!
   * @throws IllegalBlockSizeException ToDo: DOCUMENT ME!
   * @throws BadPaddingException ToDo: DOCUMENT ME!
   */
  public byte[] encrypt(final byte[] bytes) throws IOException, IllegalBlockSizeException, BadPaddingException {
    return getEncrypter().encrypt(bytes);
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @param str ToDo: DOCUMENT ME!
   *
   * @return ToDo: DOCUMENT ME!
   *
   * @throws IllegalBlockSizeException ToDo: DOCUMENT ME!
   * @throws BadPaddingException ToDo: DOCUMENT ME!
   * @throws IOException ToDo: DOCUMENT ME!
   */
  public String decrypt(final String str) throws IllegalBlockSizeException, BadPaddingException, IOException {
    return getEncrypter().decrypt(str);
  }

}
/**
 * ToDo: DOCUMENT ME!
 *
 * @author Aliaksandr Surma
  */
class Encrypter {
  private Cipher ecipher;
  private Cipher dcipher;

  /**
   * Creates a new Encrypter object.
   *
   * @param key ToDo: DOCUMENT ME!
   */
  Encrypter(SecretKey key) {
    try {
      ecipher = Cipher.getInstance(key.getAlgorithm());
      dcipher = Cipher.getInstance(key.getAlgorithm());
      ecipher.init(Cipher.ENCRYPT_MODE, key);
      dcipher.init(Cipher.DECRYPT_MODE, key);
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @param bytes ToDo: DOCUMENT ME!
   *
   * @return ToDo: DOCUMENT ME!
   *
   * @throws IllegalBlockSizeException ToDo: DOCUMENT ME!
   * @throws BadPaddingException ToDo: DOCUMENT ME!
   */
  public byte[] encrypt(final byte[] bytes) throws IllegalBlockSizeException, BadPaddingException {
    // Encrypt
    byte[] enc = ecipher.doFinal(bytes);

    // Encode bytes to base64 to get a string
    return Base64.encodeToByteArray(enc);
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @param str ToDo: DOCUMENT ME!
   *
   * @return ToDo: DOCUMENT ME!
   *
   * @throws IllegalBlockSizeException ToDo: DOCUMENT ME!
   * @throws BadPaddingException ToDo: DOCUMENT ME!
   * @throws UnsupportedEncodingException ToDo: DOCUMENT ME!
   */
  public String decrypt(final String str)
    throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
    // Decode base64 to get bytes
    byte[] dec = Base64.decodeToByteArray(str);

    // Decrypt
    byte[] utf8 = dcipher.doFinal(dec);

    // Decode using utf-8
    return new String(utf8, "UTF8");
  }
}
