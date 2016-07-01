package com.castorama.integration.bazaarvoice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class RatingReviewXMLHandler extends DefaultHandler {
	
	private final RatingReviewWriter writer;
	
	private RatingReview ratingElement;
	
	private List<String> elementsStack = new ArrayList<String>();
	
	public RatingReviewXMLHandler(RatingReviewWriter writer) {
		this.writer = writer;
	}
	
	@Override
	public void startDocument() throws SAXException {
		this.ratingElement = null;
		elementsStack.clear();

		super.startDocument();
	}
	
	@Override
	public void endDocument() throws SAXException {
		try {
			writer.close();

			super.endDocument();
		} catch (IOException e) {
			throw new SAXException("Could not complete file writing.", e);
		}
	}

	@Override
	public void startElement(String url, String localName, String name, Attributes attr) throws SAXException {
		pushElement(name);
		
		if ("Product".equalsIgnoreCase(name)) {
			ratingElement = new RatingReview();
		}

		super.startElement(url, localName, name, attr);
	}

	@Override
	public void endElement(String url, String localName, String name) throws SAXException {
		String el = popElement();
		
		if (!el.equalsIgnoreCase(name)) throw new SAXException("Wrong closing elements");
		
		if ("Product".equalsIgnoreCase(name)) {
			try {
				writer.write(ratingElement);
			} catch (IOException e) {
				throw new SAXException("Could not write to file.", e);
			}
		}
		
		super.endElement(url, localName, name);
	}
	

	@Override
	public void characters(char[] data, int start, int len) throws SAXException {
		String elementName = getElementName();

		if ("ExternalId".equalsIgnoreCase(elementName)) {
			this.ratingElement.setExternalId(new String(data, start, len));
		} else if ("AverageRating".equalsIgnoreCase(elementName)) {
			this.ratingElement.setAvgRating(Double.valueOf(new String(data, start, len)));
		} else if ("TotalReviewCount".equalsIgnoreCase(elementName)) {
			this.ratingElement.setTotalReviewCount(Integer.valueOf(new String(data, start, len)));
		}		

		super.characters(data, start, len);
	}


	private void pushElement(String elementName) {
		elementsStack.add(elementName);
	}

	private String popElement() {
		return elementsStack.remove(elementsStack.size() - 1);
	}
	
	private String getElementName() {
		return elementsStack.get(elementsStack.size() - 1);
	}
}
