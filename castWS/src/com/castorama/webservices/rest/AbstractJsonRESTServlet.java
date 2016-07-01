package com.castorama.webservices.rest;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import atg.json.JSONException;
import atg.json.JSONObject;
import atg.nucleus.Nucleus;
import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/**
 * This is an abstract servlet that serves as a base for a REST web service which uses JSON messages
 * to pass request and/or response data. The servlet provides basic facilities for this.
 * In particular, this includes:
 * <ul>
 *  <li><em>the lifecycle of request processing</em> is managed by this servlet. The responsibility of
 *      a descendant class is to implement the request processing itself in the
 *      <tt>{@link #handleRequest(JSONObject, HttpServletRequest, HttpServletResponse)}</tt> function</li>
 *      
 *  <li><em>manipulation on JSON objects</em> is facilitated by the JSON.org library which provides
 *      an object model for a JSON object.<br/>
 *      See also <tt>{@link #putValue(JSONObject, String, Object)}</tt></li>
 *  
 *  <li><em>integration with ATG.</em> The servlet allows for accessing global-scoped Nucleus components
 *      by means of the <tt>{@link #resolveNucleusComponent(String)}</tt> function. Session- and request-scoped
 *      components could be resolved by a <tt>DynamoHttpServletRequest</tt> which could be constructed by the
 *      <tt>{@link #dynamoRequest(HttpServletRequest, HttpServletResponse)} function.<br>
 *      
 *      ATG-fashioned logging is supported by a logger returned by <tt>{@link #logger()}</tt></li>
 *      
 *  <li><em>basic error handling</em> is supplied with the servlet. The
 *      <tt>{@link #responseBodyForBadRequest(HttpServletRequest)}</tt> and
 *      <tt>{@link #responseBodyForInternalError(HttpServletRequest)}</tt> functions allow concrete services to specify
 *      how to reply on an invalid request and unexpected error while processing the request, respectively</li>
 * </ul>
 */
public abstract class AbstractJsonRESTServlet extends HttpServlet {
    private static final Charset UTF8 = Charset.forName("utf-8");
    
    private volatile ApplicationLogging log;
    
    private InitialContext initialContext;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
        try {
            log = new ApplicationLoggingImpl(this.getClass().getName(), true);
            synchronized (this) {
                initialContext = new InitialContext();
            }
        } catch (NamingException ex) {
            throw new ServletException(ex);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    private void processRequest(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        final Charset charset;
        try {
            // if the request does not specify the encoding then utf-8 is used as the default one
            final String encoding = request.getCharacterEncoding();
            charset = encoding == null ? UTF8 : Charset.forName(encoding); // Charset#forName validates the encoding
        } catch (IllegalArgumentException ex) {
            // cannot do anything about explicitly specified invalid encoding; just stops processing
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        JSONObject result;
        try {
            final JSONObject requestBody = readRequestBody(request, charset);
            result = handleRequest(requestBody, request, response);
        } catch (JSONException ex) {
            if (logger().isLoggingError()) {
                logger().logError("the request is wrong", ex);
            }
            // this exception is possible only if the request is malformed, so 'bad request' is an adequate response
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result = responseBodyForBadRequest(request);
        } catch (Throwable ex) {
            if (logger().isLoggingError()) {
                logger().logError("unexpected error", ex);
            }
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                result = responseBodyForInternalError(request);
            } catch (Throwable ex2) {
                if (logger().isLoggingError()) {
                    logger().logError("Cannot render the 'internal error' response. An empty body is returned", ex2);
                }
                result = null;
            }
        }
        writeResponseBody(result, response, charset);
    }
    
    /**
     * <p>This member function performs the main service logic. It processes the request message in order to
     * return a response that is returned to the caller.</p>
     * 
     * <p>Manual filling of the HTTP response with data should not be performed here. It is performed outside.
     * Not following this could corrupt the response sent to the caller.</p>
     * 
     * @param requestBody the request message to be processed.
     * @param request the HTTP request to be processed.
     * @param response the HTTP response for the request.
     * 
     * @return the response message to be passed to the service caller.
     */
    protected abstract JSONObject handleRequest(final JSONObject requestBody, final HttpServletRequest request,
            final HttpServletResponse response);
    
    /**
     * Returns a JSON structure which indicates that an HTTP request (either parameters or body) is incorrect.
     * This structure is used by the servlet to populate the response body to tell the sender
     * that the request she sent it incorrect.
     * 
     * @param request the current HTTP request. It could contain the attributes necessary for building the result
     * structure.
     * 
     * @return a JSON structure which indicates that an HTTP request is wrong.
     */
    protected JSONObject responseBodyForBadRequest(final HttpServletRequest request) {
        return null;
    }
    
    /**
     * Returns a JSON structure which indicates that an unexpected error has been encountered while processing
     * a request. This structure is used by the servlet to populate the response body to inform the sender
     * about this.
     * 
     * @param request the current HTTP request. It could contain the attributes necessary for building the result
     * structure.
     * 
     * @return a JSON structure which indicates that an unexpected error has been encountered while processing
     * a request.
     */
    protected JSONObject responseBodyForInternalError(final HttpServletRequest request) {
        return null;
    }
    
    private static JSONObject readRequestBody(final HttpServletRequest request, final Charset charset)
            throws JSONException, IOException {
        final String content;
        final int contentLength = request.getContentLength();
        if (contentLength >= 0) {
            final byte[] rawContent = new byte[contentLength];
            final int bytesRead = request.getInputStream().read(rawContent);
            if (bytesRead != contentLength) {
                throw new InternalError();
            }
            content = new String(rawContent, charset.name());
        } else {
            final Reader reader = request.getReader();
            final StringWriter buf = new StringWriter();
            int c;
            while ((c = reader.read()) >= 0) {
                buf.append((char) c);
            }
            content = buf.toString();
        }
        return new JSONObject(content);
    }
    
    private static void writeResponseBody(final JSONObject result, final HttpServletResponse response, final Charset charset)
            throws IOException {
        response.setContentType("application/json;charset=" + charset.name());
        final Writer out = response.getWriter();
        if (result != null) {
            out.write(result.toString());
        }
        out.close();
    }
    
    /**
     * Returns the logger to be used by the servlet to log information about the processing of requests.
     * 
     * @return the logger to be used by the servlet to log information about the processing of requests.
     */
    protected final ApplicationLogging logger() {
        return log;
    }
    
    /**
     * Resolves the global-scoped Nucleus component with the given name.
     * 
     * @param componentName the full name of the component to be resolved.
     * 
     * @return the component handler if it is resolved successfully, or <tt>null</tt>
     * if it cannot be resolved by the given name.
     */
    protected final Object resolveNucleusComponent(final String componentName) {
        try {
            synchronized (initialContext) {
                return initialContext.lookup("dynamo:" + componentName);
            }
        } catch (NamingException ex) {
            return null;
        }
    }
    
    /**
     * Returns a Dynamo wrapper over the given HTTP request to allow for resolving session- and
     * request-scoped components and using various ATG APIs that require a Dynamo HTTP request to
     * be used.
     * 
     * @param request the HTTP request to be converted into a Dynamo HTTP request.
     * @param response the HTTP response for the request.
     * 
     * @return a Dynamo wrapper over the given HTTP request.
     */
    protected static DynamoHttpServletRequest dynamoRequest(final HttpServletRequest request, final HttpServletResponse response) {
        final DynamoHttpServletRequest result = new DynamoHttpServletRequest();
        result.setRequest(request);
        final DynamoHttpServletResponse dynamoRs = new DynamoHttpServletResponse();
        dynamoRs.setRequest(result);
        dynamoRs.setResponse(response);
        result.setResponse(dynamoRs);
        result.setNucleus(Nucleus.getGlobalNucleus());
        return result;
    }
    
    /**
     * Sets a given value to the JSON object's field. In JavaScript this could be written as
     * <code>obj[fieldName] = value;</code>
     * 
     * @param obj the object to be updated.
     * @param fieldName the name of the object field to be updated.
     * @param value the new value of the object field.
     * 
     * @throws NullPointerException if either <i>obj</i> or <i>fieldName</i> is <tt>null</tt>.
     */
    protected static void putValue(final JSONObject obj, final String fieldName, final Object value) {
        if (fieldName == null) {
            throw new NullPointerException("fieldName");
        }
        try {
            obj.put(fieldName, value);
        } catch (JSONException ex) {
            // cannot happen
            throw new Error(ex);
        }
    }
}
