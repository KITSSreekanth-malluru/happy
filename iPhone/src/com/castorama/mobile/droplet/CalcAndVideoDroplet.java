/**
 *
 */
package com.castorama.mobile.droplet;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.droplet.IsEmpty;

import atg.json.JSONArray;
import atg.json.JSONException;
import atg.json.JSONObject;
import atg.json.JSONWriter;
import atg.nucleus.naming.ParameterName;

import atg.repository.RepositoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.castorama.utils.StoreTools;
import static com.castorama.mobile.tools.CastoramaConstants.*;

/**
 * Returns calculator id and videos fro SMA server side
 *
 * @author Mikalai_Khatsko
 */
public class CalcAndVideoDroplet extends DynamoServlet {
    /**
     * 
     */
    private static final String VIDEOS_CONST = "videos";

    /**
     * 
     */
    private static final String URL_CONST = "url";

    /**
     * 
     */
    private static final String NAME_CONST = "name";

    /**
     * 
     */
    private static final String CALCULATOR_ID = "calculatorId";

    /** OUTPUT constant. */
    public static final ParameterName OUTPUT = ParameterName.getParameterName("output");

    /** ERROR constant. */
    public static final ParameterName ERROR = ParameterName.getParameterName("error");

    /** EMPTY constant. */
    public static final ParameterName EMPTY = ParameterName.getParameterName("empty");


    /** CODE_ARTICLE constant. */
    private static final String CODE_ARTICLE = "codeArticle";

    /** SMA_RESPONSE constant. */
    private static final String SMA_RESPONSE = "response";

    /** mSmaHelperServlet property. */
    private String mSmaHelperServlet;

    /** mConnectionTimeout property. */
    private int mConnectionTimeout;

    /** mConnectionCharset property. */
    private String mConnectionCharset;

    /** mReadTimeout property. */
    private int mReadTimeout;


    /**
     * Returns smaHelperServlet property.
     *
     * @return mSmaHelperSErvlet property.
     */
    public String getSmaHelperServlet() {
        return mSmaHelperServlet;
    }

    /**
     * Sets the value of the smaHelperServlet property.
     *
     * @param pSmaHelperServlet parameter to set.
     */
    public void setSmaHelperServlet(String pSmaHelperServlet) {
        mSmaHelperServlet = pSmaHelperServlet;
    }

    /**
     * Returns mConnectionTimeout property.
     *
     * @return mConnectionTimeout property.
     */
    public int getConnectionTimeout() {
        return mConnectionTimeout;
    }

    /**
     * Sets the value of the mConnectionTimeout property.
     *
     * @param pConnectionTimeout parameter to set.
     */
    public void setConnectionTimeout(int pConnectionTimeout) {
        mConnectionTimeout = pConnectionTimeout;
    }
    
    /**
     * Returns mReadTimeout property.
     *
     * @return mReadTimeout property.
     */
    public int getReadTimeout() {
        return mReadTimeout;
    }

    /**
     * Sets the value of the mReadTimeout property.
     *
     * @param pReadTimeout parameter to set.
     */
    public void setReadTimeout(int pReadTimeout) {
        mReadTimeout = pReadTimeout;
    }

    /**
     * Returns mConnectionCharset property.
     *
     * @return mConnectionCharset property.
     */
    public String getConnectionCharset() {
        return mConnectionCharset;
    }
    
    /**
     * Sets the value of the mConnectionCharset property.
     *
     * @param pConnectionCharset parameter to set.
     */
    public void setConnectionCharset(String pConnectionCharset) {
        mConnectionCharset = pConnectionCharset;
    }

    /**
     * Returns product calculator type and videos
     *
     * @param  pRequest  parameter
     * @param  pResponse parameter
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) {
        BufferedReader in = null;
        URL url = null;
        JSONObject jo = null;
        String codeArticle = pRequest.getParameter(CODE_ARTICLE);
        if (codeArticle == null || codeArticle.length()==0) return;
        try {
            url = new URL(getSmaHelperServlet()+codeArticle);
            URLConnection urlConn = (URLConnection) url.openConnection();
            urlConn.setConnectTimeout(getConnectionTimeout());
            urlConn.setReadTimeout(getReadTimeout());
            StringBuffer contenu = new StringBuffer();
            in = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), getConnectionCharset()));
            String line = null;
            while ((line = in.readLine()) != null) {
                contenu.append(line);
            }
            String strContent = contenu.toString();
            if (strContent!=null && strContent.length()>0){
                jo = new JSONObject(strContent);
                String calcId= jo.getString(CALCULATOR_ID);
                JSONArray videos = jo.getJSONArray(VIDEOS_CONST);
                String[][] nameAndUrl = null;
                if (videos != null && !videos.isEmpty()){
                    @SuppressWarnings("unchecked")
                    Iterator<JSONObject> it = videos.iterator();
                    nameAndUrl = new String [videos.size()][2];
                    int counter = 0;
                    while(it.hasNext()){
                        JSONObject ob = it.next();
                        nameAndUrl[counter][0] = ob.has(NAME_CONST)?ob.getString(NAME_CONST):"";
                        nameAndUrl[counter][1] = ob.getString(URL_CONST);
                        counter ++;
                    }
                }
                if ((strContent != null) && (strContent.length() > 0)) {
                    pRequest.setParameter(CALCULATOR_ID, calcId);
                    pRequest.setParameter(VIDEOS_CONST, nameAndUrl);
                }
                pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
            } else {
                pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
            }
        } catch (SocketTimeoutException socketTimeoutException) {
            if (isLoggingError()) {
                logError("Error connecting to url \""+url
                        +"\"",
                        socketTimeoutException);
            }           
        } catch (MalformedURLException malformedURLException) {
            if (isLoggingError()) {
                logError(malformedURLException);
            }           
        } catch (FileNotFoundException fileNotFoundException) {
            if (isLoggingError()) {
                logError("Failed to open stream to URL \""+url
                        +"\"", fileNotFoundException);
            }            
        } catch (IOException iOException) {
            if (isLoggingError()) {
                logError("Error reading URL content \""+url
                        +"\"", iOException);
            }            
        } catch (JSONException e) {
            if (isLoggingError()) {
                logError("Error reading JSON responce content \""+url
                        +"\"", e);
            }            
        } catch (ServletException e) {
            if (isLoggingError()) {
                logError("Error service local parameter output \""+url
                        +"\"", e);
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                }
            }
        }  // end try-catch-finally
        
    }
}
