package com.castorama.integration.webservice.inventory.support;


import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;
import com.castorama.integration.webservice.Converter;
import com.castorama.integration.webservice.inventory.exception.NotImplementedException;
import com.castorama.integration.webservice.inventory.message.InventoryRequestMessage;
import com.castorama.integration.webservice.inventory.model.WebServiceConfiguration;
import com.castorama.integration.webservice.inventory.model.WebServiceConstants;
import com.castorama.integration.webservice.inventory.model.xml.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author EPAM team
 */
public class RequestConverter implements Converter<InventoryRequestMessage, String> {

    //region Dependencies
    private WebServiceConfiguration configuration;
    //endregion

    @Override
    public InventoryRequestMessage convertToMessage(String s) {
        throw new NotImplementedException();
    }

    @Override
    public String convertFromMessage(InventoryRequestMessage inventoryRequestMessage) throws Exception {
        ByteArrayOutputStream outputStream = null;

        try {
            Envelope envelope = convertMessageToEnvelope(inventoryRequestMessage);
            outputStream = new ByteArrayOutputStream();
            getMarshaller().marshal(envelope, outputStream);

            return outputStream.toString();
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    /**
     * Return xml marshaller
     *
     * @return xml marshaller
     * @throws javax.xml.bind.JAXBException
     */
    private Marshaller getMarshaller() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Envelope.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        return marshaller;
    }

    /**
     * Convert inventory web service request message to Envelope
     *
     * @param inventoryRequestMessage web service request message object
     * @return InventoryResponseMessage
     */
    private Envelope convertMessageToEnvelope(InventoryRequestMessage inventoryRequestMessage) throws ParseException {
        SimpleDateFormat webServiceSimpleDateFormat =
                webServiceSimpleDateFormat = new SimpleDateFormat(configuration.getRequestWebServiceDateFormat());

        Expression expression = new Expression("Application", "en");
        Get get = new Get(expression);
        DataArea dataArea = new DataArea(inventoryRequestMessage.getStoreId(), get);
        for (Integer codeArticle : inventoryRequestMessage.getCodeArticles()) {
            ProductItem productItem = new ProductItem(codeArticle);
            InventoryBalance inventoryBalance = new InventoryBalance(productItem);
            dataArea.addInventoryBalance(inventoryBalance);
        }
        String stringDate = webServiceSimpleDateFormat.format(inventoryRequestMessage.getCreationDateTime());
        ApplicationArea area = new ApplicationArea(stringDate);
        GetInventoryBalance balance = new GetInventoryBalance(area, dataArea);
        Header header = new Header();
        Body body = new Body(balance);

        return new Envelope(header, body);
    }

    public WebServiceConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(WebServiceConfiguration configuration) {
        this.configuration = configuration;
    }
}
