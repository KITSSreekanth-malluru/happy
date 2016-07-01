package com.castorama.stock.mchannel.rest;

import java.util.Locale;

import org.springframework.core.Ordered;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

public class SimpleViewResolver implements ViewResolver/*, Ordered*/ {
    private final View mView;
    private int mOrder;

    public SimpleViewResolver(View view) {
        this.mView = view;
    }

    public View resolveViewName(String viewName, Locale locale) throws Exception {
        return mView;
    }

    public int getOrder() {
        return mOrder;
    }

    public void setOrder(int order) {
        this.mOrder = order;
    }
}
