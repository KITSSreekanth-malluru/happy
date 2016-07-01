package com.castorama.servlet;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import org.bouncycastle.openssl.PEMReader;

import org.bouncycastle.util.encoders.Base64;

import com.castorama.commerce.order.CastOrderManager;
import com.castorama.payment.PayboxParametersConfiguration;

import atg.nucleus.ServiceException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * Extends DynamoServlet to work with Paybox.
 *
 * @author Vasili Ivus
 */
public class PayboxServlet extends DynamoServlet {

    /**
     * bouncyCastleProvider property.
     */
    private static Provider bouncyCastleProvider;
    /**
     * algorithm property.
     */
    private String mAlgorithm;
    /**
     * keyPath property.
     */
    private String mKeyPath;
    /**
     * sign property.
     */
    private String mSign;
    /**
     * allowedIPs property.
     */
    private List<String> mAllowedIPs;
    /**
     * doIPFilter property.
     */
    private boolean mDoIPFilter;
    /**
     * codeOk property.
     */
    private String codeOk;
    /**
     * orderManager property
     */
    private CastOrderManager mOrderManager;
    private PayboxParametersConfiguration payboxConfiguration;

    /**
     * @throws ServiceException exception
     */
    @Override
    public void doStartService() throws ServiceException {
        if (null == bouncyCastleProvider) {
            bouncyCastleProvider = new BouncyCastleProvider();
            Security.addProvider(bouncyCastleProvider);
        }
        super.doStartService();
    }

    public PayboxParametersConfiguration getPayboxConfiguration() {
        return payboxConfiguration;
    }

    public void setPayboxConfiguration(PayboxParametersConfiguration payboxConfiguration) {
        this.payboxConfiguration = payboxConfiguration;
    }

    /**
     * Services a DynamoHttpServletRequest/Response pair
     *
     * @param request  parameter
     * @param response parameter
     * @throws ServletException if an error occurred while processing the
     *                          servlet request
     * @throws IOException      if an error occurred while reading or writing
     *                          the servlet request
     */
    @Override
    public void service(DynamoHttpServletRequest request, DynamoHttpServletResponse response)
            throws IOException, ServletException {
        serviceAction(request, response);
    }

    /**
     * Template method to call from service
     *
     * @param request  request
     * @param response response
     * @throws ServletException if an error occurred while processing the
     *                          servlet request
     * @throws IOException      if an error occurred while reading or writing
     *                          the servlet request
     */
    protected void serviceAction(DynamoHttpServletRequest request, DynamoHttpServletResponse response)
            throws IOException, ServletException {
    }

    /**
     * Returns PublicKey
     *
     * @return PublicKey
     */
    protected PublicKey getPublicKey() {
        PublicKey result = null;
        try {
            String pemKey = payboxConfiguration.getPublicKey();
            PEMReader l_pemReader = new PEMReader(new StringReader(pemKey));
            result = (PublicKey) l_pemReader.readObject();
        } catch (IOException e) {
            if (isLoggingError()) {
                logError(e);
            }
        }
        return result;
    }

    /**
     * Checks when sign is correct
     *
     * @param pSign    sign
     * @param pMessage message
     * @return true when sign correct, otherwise false
     */
    protected boolean isCorrectSign(String pSign, String pMessage) {
        boolean result = false;

        if (isLoggingInfo()) {
            logInfo("isCorrectSign() - start");
            logInfo("isCorrectSign(): " + pSign);
            logInfo("isCorrectSign(): " + pMessage);
        }

        PublicKey key = getPublicKey();
        if (null != key) {
            try {
                Signature sig = Signature.getInstance(getAlgorithm());
                sig.initVerify(key);
                byte[] l_dataBytes = pMessage.getBytes();
                sig.update(l_dataBytes, 0, l_dataBytes.length);
                result = sig.verify(Base64.decode(pSign));
            } catch (Exception e) {
                if (isLoggingError()) {
                    logError(e);
                }
            }
        }

        if (isLoggingInfo()) {
            logInfo("isCorrectSign() - finish: " + result);
        }

        return result;
    }

    /**
     * Checks when sign is correct
     *
     * @param pRequest request
     * @return true when sign correct, otherwise false
     */
    protected boolean isCorrectSign(HttpServletRequest pRequest) {
        boolean result = false;

        if (isLoggingInfo()) {
            logInfo("isCorrectSign(request) - start");
        }

        try {
            String sign = getSign();

            if (isLoggingInfo()) {
                logInfo("isCorrectSign(request) - sign: " + sign);
            }

            if (null != sign && 0 < sign.trim().length()) {
                String signValue = pRequest.getParameter(sign);
                String message = pRequest.getQueryString();
                if (isLoggingInfo()) {
                    logInfo("isCorrectSign(request) - check sign: " + signValue + " of " + message);
                }
                if ((null != message) && (0 < message.trim().length())) {
                    int i = message.indexOf("&" + sign);
                    if (0 < i) {
                        message = message.substring(0, i);
                        result = isCorrectSign(signValue, message);
                    }
                }
            } else {
                result = true;
            }
        } catch (Exception e) {
            if (isLoggingError()) {
                logError(e);
            }
        }

        if (isLoggingInfo()) {
            logInfo("isCorrectSign(request) - finish: " + result);
        }

        return result;
    }

    /**
     * Checks when IP correct
     *
     * @param pRequest request
     * @return true when IP correct, otherwise false
     */
    protected boolean isCorrectIP(HttpServletRequest pRequest) {
        boolean result = false;

        if (isLoggingInfo()) {
            logInfo("isCorrectIP(request) - start");
        }

        if (isDoIPFilter()) {
            try {
                String remoteAddr = (null == pRequest) ? "unknown" : pRequest.getRemoteAddr();

                if (isLoggingInfo()) {
                    logInfo("isCorrectIP(request) - remoteAddr: " + remoteAddr);
                }

                result = getAllowedIPs().contains(remoteAddr);
            } catch (Exception e) {
                if (isLoggingError()) {
                    logError(e);
                }
            }
        } else {
            result = true;
        }

        if (isLoggingInfo()) {
            logInfo("isCorrectIP(request) - finish: " + result);
        }

        return result;

    }

    /**
     * Returns algorithm property.
     *
     * @return algorithm property.
     */
    public String getAlgorithm() {
        if (null == mAlgorithm) {
            mAlgorithm = "SHA1withRSA";
        }
        return mAlgorithm;
    }

    /**
     * Sets the value of the algorithm property.
     *
     * @param pAlgorithm parameter to set.
     */
    public void setAlgorithm(String pAlgorithm) {
        this.mAlgorithm = pAlgorithm;
    }

    /**
     * Returns allowedIPs property.
     *
     * @return allowedIPs property.
     */
    public List<String> getAllowedIPs() {
        return mAllowedIPs;
    }

    /**
     * Sets the value of the allowedIPs property.
     *
     * @param allowedIP parameter to set.
     */
    public void setAllowedIPs(List<String> allowedIP) {
        this.mAllowedIPs = allowedIP;
    }

    /**
     * Returns sign property.
     *
     * @return sign property.
     */
    public String getSign() {
        return mSign;
    }

    /**
     * Sets the value of the sign property.
     *
     * @param sign parameter to set.
     */
    public void setSign(String sign) {
        this.mSign = sign;
    }

    /**
     * Returns keyPath property.
     *
     * @return keyPath property.
     */
    public String getKeyPath() {
        return mKeyPath;
    }

    /**
     * Sets the value of the keyPath property.
     *
     * @param keyPath parameter to set.
     */
    public void setKeyPath(String keyPath) {
        this.mKeyPath = keyPath;
    }

    /**
     * Returns doIPFilter property.
     *
     * @return doIPFilter property.
     */
    public boolean isDoIPFilter() {
        return mDoIPFilter;
    }

    /**
     * Sets the value of the doIPFilter property.
     *
     * @param doIPFilter parameter to set.
     */
    public void setDoIPFilter(boolean doIPFilter) {
        mDoIPFilter = doIPFilter;
    }

    /**
     * Returns codeOk property.
     *
     * @return codeOk property.
     */
    public String getCodeOk() {
        if (null == codeOk) {
            codeOk = "00000";
        }
        return codeOk;
    }

    /**
     * Sets the value of the codeOk property.
     *
     * @param codeOk parameter to set.
     */
    public void setCodeOk(String codeOk) {
        this.codeOk = codeOk;
    }

    /**
     * Returns errorCodeOK property.
     *
     * @param pErrorCode parameter to set.
     * @return errorCodeOK property.
     */
    protected boolean isErrorCodeOK(String pErrorCode) {
        if (pErrorCode != null) {
            return getCodeOk().equalsIgnoreCase(pErrorCode);
        }
        return false;
    }

    /**
     * @return the orderManager
     */
    public CastOrderManager getOrderManager() {
        return mOrderManager;
    }

    /**
     * @param pOrderManager the orderManager to set
     */
    public void setOrderManager(CastOrderManager pOrderManager) {
        this.mOrderManager = pOrderManager;
    }

}
