package com.castorama.seo;

import atg.servlet.GenericFilterService;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author EPAM team
 */
public class UrlRewriteFilter extends GenericFilterService {
    /** urlRewriteService property. */
    private UrlRewriteService mUrlRewriteService;

    /**
     *
     * @param  pServletRequest  parameter
     * @param  pServletResponse parameter
     * @param  pFilterChain     parameter
     *
     * @return 
     *
     * @throws IOException      exception
     * @throws ServletException exception
     */
    protected boolean doFilterRequest(HttpServletRequest pServletRequest, HttpServletResponse pServletResponse,
                                      FilterChain pFilterChain) throws IOException, ServletException {
        return mUrlRewriteService.doFilterRequest(pServletRequest, pServletResponse, pFilterChain, getServletContext());
    }

    /**
     * Returns urlRewriteService property.
     *
     * @return urlRewriteService property.
     */
    public UrlRewriteService getUrlRewriteService() {
        return mUrlRewriteService;
    }

    /**
     * Sets the value of the urlRewriteService property.
     *
     * @param pUrlRewriteService parameter to set.
     */
    public void setUrlRewriteService(UrlRewriteService pUrlRewriteService) {
        mUrlRewriteService = pUrlRewriteService;
    }
}
