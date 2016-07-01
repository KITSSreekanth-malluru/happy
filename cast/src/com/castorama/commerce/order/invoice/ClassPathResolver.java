package com.castorama.commerce.order.invoice;

import org.xml.sax.InputSource;

import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;

/**
 * Implementation of <code>URIResolver</code> that used for URI resolving in
 * xsl.
 *
 * @author  Igor_Kulik
 * @version 1.0
 */
public class ClassPathResolver implements URIResolver {
    /**
     * Called by the processor when it encounters an xsl:include, xsl:import, or
     * document() function.
     *
     * @param  pHref An href attribute, which may be relative or absolute
     * @param  pBase The base URI in effect when the href attribute was
     *               encountered
     *
     * @return A Source object, or null if the href cannot be resolved, and the
     *         processor should try to resolve the URI itself
     *
     * @throws TransformerException if an error occurs when trying to resolve
     *                              the URI
     */
    public Source resolve(final String pHref, final String pBase)
                   throws TransformerException {
        InputStream in = null;
        SAXSource source = null;

        in = ClassPathResolver.class.getResourceAsStream(pHref);

        if (in != null) {
            source = new SAXSource(new InputSource(in));
        } else {
            throw new TransformerException("Resource was not found: " + pHref);
        }

        return source;
    }
}
