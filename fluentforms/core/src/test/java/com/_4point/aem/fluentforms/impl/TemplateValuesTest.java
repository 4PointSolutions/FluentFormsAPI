package com._4point.aem.fluentforms.impl;

import static com._4point.aem.fluentforms.api.TestUtils.SAMPLE_FORM;
import static com._4point.aem.fluentforms.api.TestUtils.SAMPLE_FORMS_DIR;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com._4point.aem.fluentforms.api.PathOrUrl;

class TemplateValuesTest {

	private static final String BAD_CONTENT_ROOT = "D:\\foobar";
	private static final String BAD_TEMPLATE = "NonExistentForm.xdp";
	
	@Test
	@DisplayName("TemplateValues Test: Absolute Path in form with Path content root")
	public void testTemplateValuesAbsPathwDir() throws Exception {
		Path template = SAMPLE_FORM.toAbsolutePath();
		PathOrUrl templatesDir = new PathOrUrl(SAMPLE_FORMS_DIR.getParent().toAbsolutePath());
		
		TemplateValues resultTv = TemplateValues.determineTemplateValues(template, templatesDir, UsageContext.SERVER_SIDE);
		
		// Content Root should be ignored.
		assertEquals(template.getParent(), resultTv.getContentRoot().getPath(), "Content Root wasn't what was expected.");
		assertEquals(template.getFileName(), resultTv.getTemplate(), "Template wasn't what was expected.");
	}

	@Test
	@DisplayName("TemplateValues Test: Relative Path in form with content root")
	public void testTemplateValuesRelPathwDir() throws Exception {
		Path template = SAMPLE_FORM.getParent().getFileName().resolve(SAMPLE_FORM.getFileName());
		PathOrUrl templatesDir = new PathOrUrl(SAMPLE_FORMS_DIR.getParent());
		
		TemplateValues resultTv = TemplateValues.determineTemplateValues(template, templatesDir, UsageContext.SERVER_SIDE);
		
		assertEquals(SAMPLE_FORMS_DIR, resultTv.getContentRoot().getPath(), "Content Root wasn't what was expected.");
		assertEquals(SAMPLE_FORM.getFileName(), resultTv.getTemplate(), "Template wasn't what was expected.");
	}

	@Test
	@DisplayName("TemplateValues Test: Absolute Path in form with Url content root")
	public void testTemplateValuesAbsPathwUrl() throws Exception {
		String urlString = "http://foo/bar";
		Path template = SAMPLE_FORM.toAbsolutePath();
		PathOrUrl templatesDir = new PathOrUrl(new URL(urlString));
		
		TemplateValues resultTv = TemplateValues.determineTemplateValues(template, templatesDir, UsageContext.SERVER_SIDE);
		
		// Content Root should be ignored. (i.e. it will be the parent dir of the template)
		assertEquals(SAMPLE_FORMS_DIR.toAbsolutePath(), resultTv.getContentRoot().getPath(), "Content Root wasn't what was expected.");
		assertEquals(template.getFileName(), resultTv.getTemplate(), "Template wasn't what was expected.");
	}

	@Test
	@DisplayName("TemplateValues Test: Relative Path in form with Url content root")
	public void testTemplateValuesRelPathwUrl() throws Exception {
		Path template = SAMPLE_FORM.getParent().getFileName().resolve(SAMPLE_FORM.getFileName());
		String urlString = "http://foo/bar";
		PathOrUrl templatesDir = new PathOrUrl(new URL(urlString));
		
		TemplateValues resultTv = TemplateValues.determineTemplateValues(template, templatesDir, UsageContext.SERVER_SIDE);
		
		assertEquals(urlString + "/" + SAMPLE_FORMS_DIR.getFileName().toString(), resultTv.getContentRoot().getUrl().toString(), "Content Root wasn't what was expected.");
		assertEquals(SAMPLE_FORM.getFileName(), resultTv.getTemplate(), "Template wasn't what was expected.");
	}

	@Test
	@DisplayName("TemplateValues Test: Absolute Path in form with CRX content root")
	public void testTemplateValuesAbsPathwCrxUrl() throws Exception {
		String urlString = "crx://foo/bar";
		Path template = SAMPLE_FORM.toAbsolutePath();
		PathOrUrl templatesDir = PathOrUrl.fromString(urlString);
		
		TemplateValues resultTv = TemplateValues.determineTemplateValues(template, templatesDir, UsageContext.SERVER_SIDE);
		
		// Content Root should be ignored. (i.e. it will be the parent dir of the template)
		assertEquals(SAMPLE_FORMS_DIR.toAbsolutePath(), resultTv.getContentRoot().getPath(), "Content Root wasn't what was expected.");
		assertEquals(template.getFileName(), resultTv.getTemplate(), "Template wasn't what was expected.");
	}

	@Test
	@DisplayName("TemplateValues Test: Relative Path in form with CRX content root")
	public void testTemplateValuesRelPathwCrxUrl() throws Exception {
		Path template = SAMPLE_FORM.getParent().getFileName().resolve(SAMPLE_FORM.getFileName());
		String urlString = "crx://foo/bar";
		PathOrUrl templatesDir = PathOrUrl.fromString(urlString);
		
		TemplateValues resultTv = TemplateValues.determineTemplateValues(template, templatesDir, UsageContext.SERVER_SIDE);
		
		assertEquals(urlString + "/" + SAMPLE_FORMS_DIR.getFileName().toString(), resultTv.getContentRoot().getCrxUrl(), "Content Root wasn't what was expected.");
		assertEquals(SAMPLE_FORM.getFileName(), resultTv.getTemplate(), "Template wasn't what was expected.");
	}

	@Test
	@DisplayName("TemplateValues Test: Relative Path in form with no content root")
	public void testTemplateValuesRelPathwNoDir() throws Exception {
		Path template = SAMPLE_FORM;
		PathOrUrl templatesDir = null;
		
		TemplateValues resultTv = TemplateValues.determineTemplateValues(template, templatesDir, UsageContext.SERVER_SIDE);
		
		assertEquals(template.getParent(), resultTv.getContentRoot().getPath(), "Content Root wasn't what was expected.");
		assertEquals(template.getFileName(), resultTv.getTemplate(), "Template wasn't what was expected.");
	}

	@Test
	@DisplayName("TemplateValues Test: Relative Path in form with no content root")
	public void testTemplateValuesNoPathwNoDir() throws Exception {
		Path template = SAMPLE_FORM.getFileName();
		PathOrUrl templatesDir = null;
		
		TemplateValues resultTv = TemplateValues.determineTemplateValues(template, templatesDir, UsageContext.CLIENT_SIDE);
		
		assertNull(resultTv.getContentRoot(), "Content Root wasn't what was expected.");
		assertEquals(template.getFileName(), resultTv.getTemplate(), "Template wasn't what was expected.");
	}

	@Test
	@DisplayName("TemplateValues Test: Bad template with content root")
	public void testBadTemplateValuesAbsPathwDir_Client() throws Exception {
		Path template = Paths.get(BAD_TEMPLATE);
		PathOrUrl templatesDir = new PathOrUrl(SAMPLE_FORMS_DIR.toAbsolutePath());
		
		TemplateValues resultTv = TemplateValues.determineTemplateValues(template, templatesDir, UsageContext.CLIENT_SIDE);
		assertEquals(templatesDir, resultTv.getContentRoot(), "Content Root wasn't what was expected.");
		assertEquals(template.getFileName(), resultTv.getTemplate(), "Template wasn't what was expected.");
	}
	
	@Test
	@DisplayName("TemplateValues Test: Bad template with content root")
	public void testBadTemplateValuesAbsPathwDir_Server() {
		Path template = Paths.get(BAD_TEMPLATE);
		PathOrUrl templatesDir = new PathOrUrl(SAMPLE_FORMS_DIR.toAbsolutePath());

		FileNotFoundException e = assertThrows(FileNotFoundException.class, ()->TemplateValues.determineTemplateValues(template, templatesDir, UsageContext.SERVER_SIDE));
		assertTrue(e.getMessage().contains("Unable to find template"), "Expected 'Unable to find template' to be in the message.");
		assertTrue(e.getMessage().contains(BAD_TEMPLATE), "Expected template name to be in the message.");
		assertTrue(e.getMessage().contains(SAMPLE_FORMS_DIR.toString()), "Expected content root to be in the message.");
	}

	@Test
	@DisplayName("TemplateValues Test: Template with bad content root")
	public void testBadTemplateValuesAbsPathwDir2() {
		Path template = SAMPLE_FORM.getFileName();
		PathOrUrl templatesDir = new PathOrUrl(Paths.get(BAD_CONTENT_ROOT));

		FileNotFoundException e = assertThrows(FileNotFoundException.class, ()->TemplateValues.determineTemplateValues(template, templatesDir, UsageContext.SERVER_SIDE));
		assertTrue(e.getMessage().contains("Unable to find template"), "Expected 'Unable to find template' to be in the message.");
		assertTrue(e.getMessage().contains(template.toString()), "Expected template name to be in the message.");
		assertTrue(e.getMessage().contains(BAD_CONTENT_ROOT), "Expected content root to be in the message.");
	}

	@Test
	@DisplayName("TemplateValues Test: Non-existent relative template with content root")
	public void testBadTemplateValuesRelPathwDir() {
		Path template = SAMPLE_FORM.getParent().getFileName().resolve(SAMPLE_FORM.getFileName());
		PathOrUrl templatesDir = new PathOrUrl(Paths.get(BAD_CONTENT_ROOT));

		FileNotFoundException e = assertThrows(FileNotFoundException.class, ()->TemplateValues.determineTemplateValues(template, templatesDir, UsageContext.SERVER_SIDE));
		assertTrue(e.getMessage().contains("Unable to find template"), "Expected 'Unable to find template' to be in the message.");
		assertTrue(e.getMessage().contains(template.toString()), "Expected template name to be in the message.");
		assertTrue(e.getMessage().contains(BAD_CONTENT_ROOT), "Expected content root to be in the message.");
	}

}
