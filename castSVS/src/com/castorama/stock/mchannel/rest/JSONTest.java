package com.castorama.stock.mchannel.rest;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.AbstractView;

public class JSONTest extends AbstractView {

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        for (Entry<String, Object> entry : model.entrySet()) {
            PrintWriter writer = response.getWriter();
            writer.write("# " + entry.getKey() + "\n");
            writer.write(entry.getValue().toString() + "\n");
        }
    }

}
