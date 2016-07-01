/**
 *
 */
package com.castorama.mobile.tools;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import java.util.Iterator;

import atg.core.util.StringUtils;

import atg.json.JSONException;
import atg.json.JSONObject;
import atg.json.JSONTokener;

import atg.nucleus.GenericService;

import atg.servlet.DynamoHttpServletRequest;

import static com.castorama.mobile.tools.CastoramaConstants.*;

/**
 * Helper class for Castorama  Mobile App.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class CastoramaTools extends GenericService {
    /** mBarcodeConverterServiceURL property. */
    private String mBarcodeConverterServiceURL;

    /**
     * Sets the value of the barcodeConverterServiceURL property.
     *
     * @param pBarcodeConverterServiceURL parameter to set.
     */
    public void setBarcodeConverterServiceURL(String pBarcodeConverterServiceURL) {
        mBarcodeConverterServiceURL = pBarcodeConverterServiceURL;
    }

    /**
     * Returns barcodeConverterServiceURL property.
     *
     * @return barcodeConverterServiceURL property.
     */
    public String getBarcodeConverterServiceURL() {
        return mBarcodeConverterServiceURL;
    }
    
    /** connectTimeout property */
    private int connectTimeout;
    /** readTimeout property */
    private int readTimeout;

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    /**
     * Converts input data to string.
     *
     * @param  reader parameter
     *
     * @return converted to string data.
     *
     * @throws IOException exception
     */
    public static String inputToString(Reader reader) throws IOException {
        StringBuffer buff = new StringBuffer();
        int i;

        while ((i = reader.read()) != -1) {
            char c = (char) i;
            buff.append(c);
        }

        return buff.toString();
    }

    /**
     * Converts String value to JSON objects.
     *
     * @param jsonObject parameter
     * @param prefix     parameter
     * @param request    parameter
     */
    public void flattenJSON(JSONObject jsonObject, String prefix, DynamoHttpServletRequest request) {
        Iterator it = jsonObject.keys();

        while (it.hasNext()) {
            String key = (String) it.next();

            try {
                Object value = jsonObject.get(key);

                String fullKey = "";

                if (prefix != "") {
                    fullKey = prefix + key;
                } else {
                    fullKey = key;
                }

                if ((value instanceof String) || (value instanceof Integer) || (value instanceof Double) ||
                        (value instanceof Boolean)) {
                    if (isLoggingDebug()) {
                        logDebug("adding to request: key=" + fullKey + ", value=" + value.toString());
                    }

                    request.setAttribute(fullKey, value);
                }

                if (value instanceof JSONObject) {
                    if (isLoggingDebug()) {
                        logDebug("found JSONObject with key=" + fullKey);
                    }

                    // use an underscore "_" here instead of "." because in JSTL it uses dot notation to
                    // mean properties of objects
                    flattenJSON((JSONObject) value, fullKey + "_", request);
                }
            } catch (JSONException e) {
                if (isLoggingError()) {
                    logError(e.getMessage());
                }
            }  // end try-catch
        }
    }

    /**
     * Utility method for converting barcode to code article (sends requests to
     * barcode converter service).
     *
     * @param  pBarcode     parameter
     *
     * @return converted barcode to codeArticle.
     */
    public JSONObject convertBarcodeToCodeArticle(String pBarcode) {
        JSONObject jsonObject = null;

        String barcodeConverterServiceURL = getBarcodeConverterServiceURL();

        if (!StringUtils.isBlank(pBarcode) && !StringUtils.isBlank(barcodeConverterServiceURL)) {
            String urlToQjuery =
                    barcodeConverterServiceURL.trim().concat((barcodeConverterServiceURL.contains(QUERY) ? AND_SIGN
                                                                                                         : QUERY))
                    .concat(BARCODE.concat(EQUAL_SIGN)).concat(pBarcode);
          
            try {
                URL url = new URL(urlToQjuery);

                URLConnection connection = url.openConnection();
                connection.setConnectTimeout(getConnectTimeout());
                connection.setReadTimeout(getReadTimeout());
                connection.connect();

                Reader reader = new InputStreamReader(connection.getInputStream());
                String s = CastoramaTools.inputToString(reader);
                JSONTokener jsonTokener = new JSONTokener(s);

                jsonObject = new JSONObject(jsonTokener);

            } catch (MalformedURLException e) {
                if (isLoggingError()) {
                    logError(e.getMessage());
                }
            } catch (JSONException e) {
                if (isLoggingError()) {
                    logError(e.getMessage());
                }
            } catch (IOException e) {
                if (isLoggingError()) {
                    logError(e.getMessage());
                }
            }  // end try-catch
        }  // end if
        return jsonObject;
    }

}
