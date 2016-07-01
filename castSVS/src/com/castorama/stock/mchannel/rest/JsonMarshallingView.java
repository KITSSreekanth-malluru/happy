package com.castorama.stock.mchannel.rest;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

public class JsonMarshallingView extends MappingJacksonJsonView
{
    private ObjectMapper objectMapper = new ObjectMapper();
    private String attributeToRender;

    @Override
    protected void renderMergedOutputModel(Map<String, Object> pModel, HttpServletRequest pRequest, HttpServletResponse pResponse)
        throws Exception
    {
        JsonGenerator generator = objectMapper.getJsonFactory().createJsonGenerator(pResponse.getOutputStream(), JsonEncoding.UTF8);
        objectMapper.writeValue(generator, pModel.get(attributeToRender));
    }

    public String getAttributeToRender()
    {
        return attributeToRender;
    }

    public void setAttributeToRender(String pAttributeToRender)
    {
        attributeToRender = pAttributeToRender;
    }

}
