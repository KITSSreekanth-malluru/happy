package com.castorama.commerce.order.invoice;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.fop.apps.Driver;
import org.apache.fop.messaging.MessageHandler;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import atg.apache.xalan.processor.TransformerFactoryImpl;

/**
 * FOPProducer is a print formatter for XSL formatting objects. It can be used
 * to render an XML file containing XSL formatting objects into a page layout.
 * (It transforms XML to XSL-FO, then formats the XSL-FO to PDF (or another FOP
 * output format).) The main target is PDF but other rendering targets are
 * supported, such as PCL and direct printing. If transform processor encounters
 * an xsl:include, xsl:import, or document() function than the default URI
 * resolver must be set up. The implementation of this class is based on Apache
 * FOP project. Note: It is the responsibility of the user to make sure that the
 * XSL-FO submitted to FOP is correct. Usage: To make processing be sure that
 * you set up XML and XSL input sources by calling appropriate <code>
 * setXSLTSource(...)</code> and <code>setXMLSource(...)</code> methods.
 * Example: FOPProducer fop = new FOPProducer(); fop.setXMLSource("input.xml");
 * fop.setXSLTSource("stylesheet.xsl"); OutputStream outputStream = .....;
 * fop.process(outputStream); ...... outputStream.close();
 *
 * @version 1.0
 */
public class FOPProducer {
    /** holds PDF rendered type. */
    public static final int RENDER_PDF = Driver.RENDER_PDF;

    /** holds PRINT rendered type. */
    public static final int RENDER_PRINT = Driver.RENDER_PRINT;

    /** holds PCL rendered type. */
    public static final int RENDER_PCL = Driver.RENDER_PCL;

    /** holds instance of input XML object. */
    private Source mXmlSource;

    /** holds instance of input XSLT object. */
    private Source mXsltSource;

    /** holds instance of URIResolver. */
    private URIResolver mUriResolver;

    /**
     * Creates a new FOPProducer object.
     */
    public FOPProducer() {
    }

    /**
     * Sets up URI Resolver.
     *
     * @param pResolver javax.xml.transform.URIResolver is object, which can be
     *                  called by the processor to turn a URI used in
     *                  document(), xsl:import, or xsl:include into a Source
     *                  object.
     *
     * @see   URIResolver
     */
    public void setURIResolver(final URIResolver pResolver) {
        mUriResolver = pResolver;
    }

    /**
     * Setup XML input source for XSLT transformation.
     *
     * @param pSrc javax.xml.transform.Source is common interface that contains
     *             the information needed to act as source input (XML source or
     *             transformation instructions).
     */
    public void setXMLSource(final Source pSrc) {
        mXmlSource = pSrc;
    }

    /**
     * Setup XML input source for XSLT transformation.
     *
     * @param pUrlSrc String is URL to XML source
     */
    public void setXMLSource(final String pUrlSrc) {
        if (pUrlSrc != null) {
            mXmlSource = new StreamSource(new File(pUrlSrc));
        }
    }

    /**
     * Setup XML input source for XSLT transformation.
     *
     * @param pIoSrc InputStream represents an input stream of bytes.
     */
    public void setXMLSource(final InputStream pIoSrc) {
        mXmlSource = new StreamSource(pIoSrc);
    }

    /**
     * Setup XML input source for XSLT transformation.
     *
     * @param pDomSrc Node represents a primary datatype for the entire Document
     *                Object Model.
     */
    public void setXMLSource(final Node pDomSrc) {
        mXmlSource = new DOMSource(pDomSrc);
    }

    /**
     * Setup XML input source for XSLT transformation.
     *
     * @param pIsSrc InputSource is an input source for an XML entity.
     */
    public void setXMLSource(final InputSource pIsSrc) {
        mXmlSource = new SAXSource(pIsSrc);
    }

    /**
     * Setup XSL input source for XSLT transformation.
     *
     * @param pSrc javax.xml.transform.Source is common interface that contains
     *             the information needed to act as source input (XML source or
     *             transformation instructions).
     */
    public void setXSLTSource(final Source pSrc) {
        mXsltSource = pSrc;
    }

    /**
     * Setup XSL input source for XSLT transformation.
     *
     * @param pDomSrc Node represents a primary datatype for the entire Document
     *                Object Model.
     */
    public void setXSLTSource(final Node pDomSrc) {
        mXsltSource = new DOMSource(pDomSrc);
    }

    /**
     * Setup XSL input source for XSLT transformation.
     *
     * @param pIsSrc InputSource is an input source for an XSL entity.
     */
    public void setXSLTSource(final InputSource pIsSrc) {
        mXsltSource = new SAXSource(pIsSrc);
    }

    /**
     * Setup XSL input source for XSLT transformation.
     *
     * @param pIoSrc InputStream represents an input stream of bytes.
     */
    public void setXSLTSource(final InputStream pIoSrc) {
        mXsltSource = new StreamSource(pIoSrc);
    }

    /**
     * Setup XSL input source for XSLT transformation.
     *
     * @param pUrlSrc String is URL to XSL source
     */
    public void setXSLTSource(final String pUrlSrc) {
        if (pUrlSrc != null) {
            mXsltSource = new StreamSource(new File(pUrlSrc));
        }
    }

    /**
     * Makes the XSLT transformation and FOP processing and writes the generated
     * Document to a giving output stream.
     *
     * @param  pOutStream OutputStream represents an output stream of bytes
     *
     * @throws TransformerException signals that exceptional condition occurred
     *                              during the transformation process.
     */
    public void process(final OutputStream pOutStream) throws TransformerException {
        process(pOutStream, null);
    }

    /**
     * Makes the XSLT transformation and FOP processing and writes the generated
     * Document to a giving output stream.
     *
     * @param  pOutStream  OutputStream represents an output stream of bytes
     * @param  pParameters map with parameters for transformation
     *
     * @throws TransformerException signals that exceptional condition occurred
     *                              during the transformation process.
     */
    public void process(final OutputStream pOutStream, final Map pParameters)
                 throws TransformerException {
        //Construct driver
        final Driver driver = new Driver();

        //Setup logger
        final Logger logger = new ConsoleLogger(ConsoleLogger.LEVEL_DISABLED);
        driver.setLogger(logger);
        MessageHandler.setScreenLogger(logger);

        //Setup Renderer (output format)
        driver.setRenderer(Driver.RENDER_PDF);
        
        
        // Enable PDF encryption.
        // Mantis 0001936: [Purchase History] Lock PDF edition
        
        final Map rendererOptions = new java.util.HashMap();
        rendererOptions.put("allowCopyContent", "FALSE");
        rendererOptions.put("allowEditContent", "FALSE");
        rendererOptions.put("allowPrint", "TRUE");

        driver.getRenderer().setOptions(rendererOptions);
        
        driver.setOutputStream(pOutStream);

        final TransformerFactory factory;

        //Setup XSLT
        factory = new TransformerFactoryImpl();  //TransformerFactory.newInstance();

        if (mUriResolver != null) {
            factory.setURIResolver(mUriResolver);
        }

        final Transformer transformer;

        //Setup XSLT
        transformer = factory.newTransformer(mXsltSource);

        if (null != pParameters) {
            final Iterator iterator = pParameters.entrySet().iterator();

            while (iterator.hasNext()) {
                final Map.Entry entry = (Map.Entry) iterator.next();
                final String key = (String) entry.getKey();
                final Object value = entry.getValue();
                transformer.setParameter(key, value);
            }
        }

        //Resulting SAX events (the generated FO) must be piped through to FOP
        final Result res = new SAXResult(driver.getContentHandler());

        //Start XSLT transformation and FOP processing
        transformer.transform(mXmlSource, res);

    }

    /**
     * This method is reserved for future using.
     *
     * @param  pFormat int
     *
     * @throws UnsupportedOperationException when error occurs.
     */
    public void setFormat(final int pFormat) {
        throw new UnsupportedOperationException("This method is reserved for future using");
    }
}
