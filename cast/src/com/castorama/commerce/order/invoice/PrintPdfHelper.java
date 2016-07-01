/*
 * Copyright (c) 2010 EPAM Systems.
 * All rights reserved.
 */

package com.castorama.commerce.order.invoice;

import atg.nucleus.GenericService;
import atg.nucleus.Nucleus;
import atg.repository.RepositoryItem;

import com.castorama.CastConfiguration;
import com.castorama.purchasehistory.TicketRetrievalService;
import com.castorama.purchasehistory.pdf.ErrorTicketFilter;
import com.castorama.purchasehistory.pdf.TicketFilter;
import com.castorama.utils.CastPlaceList;
import com.castorama.utils.SaxXmlBuilder;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import java.io.*;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * Generate pdf for invoice using FOP.
 *
 * @author Igor_Kulik
 */
public class PrintPdfHelper extends GenericService {

    private static final String FORM_PDF_XSLT = "printInvoice.xsl";
    private static final String PRINT_TICKET_XSLT = "printTicket.xsl";
    private static final String PRINT_TICKET_ERORR_XSLT = "printTicketError.xsl";

    //Remote file names for xsl inclusion
    private static final String HEADER_XSL_FILE_NAME = "header.xsl";
    private static final String INVOICE_INFO_XSL_FILE_NAME = "invoiceInfo.xsl";
    private static final String FOOTER_XSL_FILE_NAME = "footer.xsl";

    //xsl:template names
    private static final String HEADER_TEMPLATE_NAME = "header";
    private static final String INVOICE_INFO_TEMPLATE_NAME = "invoiceInfo";
    private static final String FOOTER_TEMPLATE_NAME = "footer";

    private static final String CAST_CONFIGURATION_PATH = "/com/castorama/CastConfiguration";

    /**
     * PrintPdfHelper singleton instance
     */
    private static PrintPdfHelper instance = null;
    
    private PrintPdfHelper() {
    }

    public static PrintPdfHelper getInstance() {
        if (instance == null) {
            synchronized (PrintPdfHelper.class) {
                if (instance == null) {
                    instance = new PrintPdfHelper();
                }
            }
        }
        return instance;
    }

    /**
     * Get xml document from remote file
     *
     * @param fileName remote file name file name. Remote root directory configuration:
     *                 component /com/castorama/CastConfiguration/
     *                 property xslTemplatesUrl
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    private Document getXSLDocument(final String fileName)
            throws ParserConfigurationException, SAXException, IOException {
        CastConfiguration castConfiguration =
                (CastConfiguration) Nucleus.getGlobalNucleus().resolveName(CAST_CONFIGURATION_PATH);
        String xslTemplatesUrl = castConfiguration.getXslTemplatesUrl();
        String fullUri = xslTemplatesUrl + fileName;
        if (isLoggingDebug()) {
            vlogDebug("Get xsl for include from {0}.", fullUri);
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(fullUri);
    }

    /**
     * Merge full xml document with template xml document
     *
     * @param fullXsl document with xsl:template data
     * @param templateXsl main xsl document
     * @param mergeTemplateName name of xsl:template
     */
    private Document mergeXslFoBody(final Document templateXsl, final Document fullXsl, final String mergeTemplateName) {
        Node templateNode = getTemplateNode(fullXsl, mergeTemplateName);
        NodeList bodyNodeList = getBodyNodeList(templateXsl);
        for (int i = 0; i < bodyNodeList.getLength(); i++) {
            Node bodyNodeImported = templateXsl.importNode(bodyNodeList.item(i), true);
            fullXsl.adoptNode(bodyNodeImported);
            templateNode.appendChild(bodyNodeImported);
        }

        return fullXsl;
    }

    /**
     * Get xsl:template body as NodeList for insertion to full xml document
     *
     * @param document document with xsl:template body
     * @return xsl:template body
     */
    private NodeList getBodyNodeList(Document document) {
        NodeList nodeList = document.getElementsByTagName("xsl:template");
        return nodeList.item(0).getChildNodes();
    }

    //Get template xml body from main xsl document as NodeList.
    private Node getTemplateNode(Document document, String mergeTemplateName) {
        NodeList nodeList = document.getElementsByTagName("xsl:template");
        for (int x = 0, size = nodeList.getLength(); x < size; x++) {
            Node node = nodeList.item(x).getAttributes().getNamedItem("name");
            if (node != null) {
                if (mergeTemplateName.equals(nodeList.item(x).getAttributes().getNamedItem("name").getNodeValue())) {
                    return nodeList.item(x);
                }
            }
        }

        return null;
    }

    /**
     * Generate pdf for invoice.
     *
     * @param  pOut    out      pdf output stream
     * @param  pItem   facture repository item
     * @param  pParams parameters for pdf generation
     */
    public void generateInvoicePdf(final OutputStream pOut,
                                   final RepositoryItem pItem,
                                   final Map pParams,
                                   final CastPlaceList countries) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        InputStream printInvoiceInputStream = PrintPdfHelper.class.getResourceAsStream(FORM_PDF_XSLT);
        InputStream invoiceInputStream = null;

        try {
            docBuilder = documentBuilderFactory.newDocumentBuilder();
            Document invoiceXslFo = docBuilder.parse(printInvoiceInputStream);

            //Insert header data to main xsl-fo template
            Document headerDoc = getXSLDocument(HEADER_XSL_FILE_NAME);
            if (headerDoc != null) {
                mergeXslFoBody(headerDoc, invoiceXslFo, HEADER_TEMPLATE_NAME);
            }

            //Insert invoiceInfo data to main xsl-fo template
            Document invoiceInfoDoc = getXSLDocument(INVOICE_INFO_XSL_FILE_NAME);
            if (headerDoc != null) {
                mergeXslFoBody(invoiceInfoDoc, invoiceXslFo, INVOICE_INFO_TEMPLATE_NAME);
            }

            //Insert footer data to main xsl-fo template
            Document footerDoc = getXSLDocument(FOOTER_XSL_FILE_NAME);
            if (headerDoc != null) {
                mergeXslFoBody(footerDoc, invoiceXslFo, FOOTER_TEMPLATE_NAME);
            }

            if (isLoggingDebug()) {
                vlogDebug("Full xsl-fo file after merge:\n{0}", getStringFromDocument(invoiceXslFo));
            }

            invoiceInputStream = documentToInputStream(invoiceXslFo);
            buildPdf(new InvoiceFilter(pItem, countries), pParams, pOut, invoiceInputStream);

        } catch (Exception e) {
            if (isLoggingError()) {
                logError("Exception during generateInvoicePdf.", e);
            }
        } finally {
            closeInputStream(invoiceInputStream);
            closeInputStream(printInvoiceInputStream);
        }
    }

    /**
     * Generates ticket PDF.
     *
     * @param pOut    out      pdf output stream
     * @param pParams parameters for pdf generation
     * @throws TransformerException
     */
    public void generateTicketPdf(final OutputStream pOut, final RepositoryItem pTicket,
                                  final RepositoryItem pStore, final RepositoryItem pUser, final TicketRetrievalService pTicketRetrievalService, final Map pParams)
            throws TransformerException {
        buildPdf(new TicketFilter(pTicket, pStore, pUser, pTicketRetrievalService), pParams, pOut, PRINT_TICKET_XSLT);
    }

    /**
     * Generates ticket erorr PDF.
     *
     * @param pOut    out  pdf output stream
     * @param pParams parameters for pdf generation
     * @throws TransformerException
     */
    public void generateTicketErrorPdf(final OutputStream pOut, final Map pParams)
            throws TransformerException {
        buildPdf(new ErrorTicketFilter(), pParams, pOut, PRINT_TICKET_ERORR_XSLT);
    }

    /**
     * Generate pdf for invoice.
     *
     * @param pBuilder      sax filter for form
     * @param pParams       parameters for pdf generation
     * @param pOut          out pdf output stream
     * @param pXsltFileName xslt file name
     * @throws TransformerException
     */
    private void buildPdf(final SaxXmlBuilder pBuilder, final Map pParams, final OutputStream pOut,
                          final String pXsltFileName) throws TransformerException {
        final FOPProducer fopProducer = new FOPProducer();
        fopProducer.setURIResolver(new ClassPathResolver());
        InputStream xsltIn = null;
        try {
            xsltIn = PrintPdfHelper.class.getResourceAsStream(pXsltFileName);
            buildPdf(pBuilder, pParams, pOut, xsltIn);
        } finally {
            if (xsltIn != null) {
                try {
                    xsltIn.close();
                } catch (IOException e) {
                    if (isLoggingError()) {
                        logError("Unable to close input stream.", e);
                    }
                }
            }
        }
    }

    /**
     * Generate pdf for invoice.
     *
     * @param pBuilder           sax filter for form
     * @param pParams            parameters for pdf generation
     * @param pOut               out pdf output stream
     * @param invoiceInputStream xslt input stream
     * @throws TransformerException
     */
    private void buildPdf(final SaxXmlBuilder pBuilder, final Map pParams, final OutputStream pOut,
                          final InputStream invoiceInputStream) throws TransformerException {
        final FOPProducer fopProducer = new FOPProducer();
        fopProducer.setURIResolver(new ClassPathResolver());
        final Source xmlSource = new SAXSource(pBuilder, new InputSource());
        fopProducer.setXMLSource(xmlSource);
        final InputSource inXSLT = new InputSource(invoiceInputStream);
        fopProducer.setXSLTSource(new SAXSource(inXSLT));
        fopProducer.process(pOut, pParams);
    }

    private InputStream documentToInputStream(Document document) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputFormat outputFormat = new OutputFormat(document);
        XMLSerializer serializer = new XMLSerializer(outputStream, outputFormat);
        serializer.serialize(document);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    /**
     * Convert DOM Document to String
     *
     * @param doc DOM Document
     * @return string in xml format
     * @throws TransformerException
     */
    private String getStringFromDocument(Document doc) throws TransformerException {
        DOMSource domSource = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.transform(domSource, result);
        return writer.toString();
    }

    private void closeInputStream(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                if (isLoggingError()) {
                    logError("Unable to close input stream.", e);
                }
            }
        }
    }
}
