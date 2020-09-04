package com._4point.aem.docservices.rest_services.it_tests;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlFormDocument {
	private final Document doc;

	private HtmlFormDocument(Document doc) {
		super();
		this.doc = doc;
	}

	public String getTitle() {
		Elements selectedElements = doc.getElementsByTag("title");
		assertNotNull(selectedElements);
		assertNotEquals(0, selectedElements.size());
		Element titleElement = selectedElements.first();
		assertNotNull(titleElement);
		return titleElement.text();
	}
	
	public static HtmlFormDocument create(byte[] html, URI baseUri) throws IOException {
		return new HtmlFormDocument(Jsoup.parse(new ByteArrayInputStream(html), StandardCharsets.UTF_8.toString(), baseUri.toString()));
	}
	
}
