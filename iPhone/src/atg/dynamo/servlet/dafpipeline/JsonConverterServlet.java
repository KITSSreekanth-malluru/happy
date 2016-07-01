package atg.dynamo.servlet.dafpipeline;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import java.io.Reader;
import java.io.InputStreamReader;
import java.util.Iterator;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.pipeline.InsertableServletImpl;
import atg.json.JSONObject;
import atg.json.JSONArray;
import atg.json.JSONTokener;
import atg.json.JSONException;

import com.castorama.mobile.tools.CastoramaTools;

/**
 * Converts Json from response to objects for mobile application. Check whether
 * application is mobile and parameters was passed by POST method.
 * 
 * @author Katsiaryna Sharstsiuk
 */
public class JsonConverterServlet extends InsertableServletImpl {
    /** POST constant. */
    private static final String POST = "POST";

    /** mMobileUrlContextRoot property. */
    private String mMobileUrlContextRoot = "/iphone";

    /** mCastoramaTools property. */
    private CastoramaTools mCastoramaTools;

    /**
     * Returns mobileUrlContextRoot property.
     * 
     * @return mobileUrlContextRoot property.
     */
    public String getMobileUrlContextRoot() {
        return mMobileUrlContextRoot;
    }

    /**
     * Sets the value of the mobileUrlContextRoot property.
     * 
     * @param pMobileUrlContextRoot parameter to set.
     */
    public void setMobileUrlContextRoot(String pMobileUrlContextRoot) {
        mMobileUrlContextRoot = pMobileUrlContextRoot;
    }

    /**
     * Returns castoramaTools property.
     * 
     * @return castoramaTools property.
     */
    public CastoramaTools getCastoramaTools() {
        return mCastoramaTools;
    }

    /**
     * Sets the value of the castoramaTools property.
     * 
     * @param pCastoramaTools parameter to set.
     */
    public void setCastoramaTools(CastoramaTools pCastoramaTools) {
        mCastoramaTools = pCastoramaTools;
    }

    /**
     * Converts Json from response to objects for mobile application.
     * 
     * @param pRequest parameter
     * @param pResponse parameter
     * 
     * @throws IOException exception
     * @throws ServletException exception
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws IOException,
            ServletException {
        if (pRequest.getMethod().contains(POST) && pRequest.getContextPath().contains(getMobileUrlContextRoot())) {
            ServletInputStream sis = pRequest.getInputStream();
            Reader reader = new InputStreamReader(sis);
            CastoramaTools castoramaTools = getCastoramaTools();
            if (castoramaTools != null) {
                String s = CastoramaTools.inputToString(reader);
                JSONTokener jsonTokener = new JSONTokener(s);
                try {
                    JSONObject jsonObject = new JSONObject(jsonTokener);

                    if (isLoggingDebug()) {
                        logDebug("Main JSONObject loaded: " + jsonObject.toString());
                    }

                    // flattenJSON will translate any strings, integers, doubles
                    // or objects into request params
                    castoramaTools.flattenJSON(jsonObject, "", pRequest);

                    // These are the elements of a ATGSearchRequest submission
                    if (jsonObject.has("appliedFacet")) {
                        int i;
                        JSONArray jsonArray = (JSONArray) jsonObject.getJSONArray("appliedFacet");
                        String appliedFacets = "";
                        for (i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject jsonAF = (JSONObject) jsonArray.get(i);
                                if (i == 0)
                                    appliedFacets = jsonAF.getString("facetId") + ":" + jsonAF.getString("valueId");
                                else
                                    appliedFacets = appliedFacets + ":" + jsonAF.getString("facetId") + ":"
                                            + jsonAF.getString("valueId");

                                if (isLoggingDebug()) {
                                    logDebug("appliedFacets=" + appliedFacets);
                                }
                            } catch (JSONException e) {
                                logError("Error in appliedFacets: " + e.getMessage());
                            }
                        }
                        pRequest.setAttribute("appliedFacets", appliedFacets);
                    }

                } catch (JSONException e) {
                    logError("Error creating a JSONObject: " + e.getMessage());
                }
            }
        }
        passRequest(pRequest, pResponse);
    }
}
