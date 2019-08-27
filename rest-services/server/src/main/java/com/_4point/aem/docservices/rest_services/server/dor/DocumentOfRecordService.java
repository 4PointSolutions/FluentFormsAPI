package com._4point.aem.docservices.rest_services.server.dor;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.apache.sling.api.resource.Resource;

import com._4point.aem.docservices.rest_services.server.dor.DocumentOfRecordService.DocumentOfRecordOptions;
import com.adobe.aemds.guide.addon.dor.DoRGenerationException;
import com.adobe.aemds.guide.addon.dor.DoROptions;
import com.adobe.aemds.guide.addon.dor.DoRResult;
import com.adobe.aemds.guide.addon.dor.DoRService;
import com.adobe.forms.common.service.FileAttachmentWrapper;

// It's best practice to never mock a class you don't own.  Consequently, we wrap the Adobe DoR service objects with
// our own classes.
public interface DocumentOfRecordService {

	DocumentOfRecordResult render(DocumentOfRecordOptions dorOptions) throws DocumentOfRecordException;
	
	public static class DocumentOfRecordServiceImpl implements DocumentOfRecordService {
		private final DoRService adobeDorService;
	
		private DocumentOfRecordServiceImpl(DoRService adobeDorService) {
			super();
			this.adobeDorService = adobeDorService;
		}
	
		public static DocumentOfRecordService of(DoRService adobeDorService) {
			return new DocumentOfRecordServiceImpl(adobeDorService);
		}
	
		@Override
		public DocumentOfRecordResult render(DocumentOfRecordOptions dorOptions) throws DocumentOfRecordException {
			try {
				return new DocumentOfRecordResultImpl(this.adobeDorService.render(dorOptions.getDorOptions()));
			} catch (DoRGenerationException e) {
				throw new DocumentOfRecordException(e);
			}
		}
	}	
	

	public static interface DocumentOfRecordResult {

		public byte[] getContent();

		public String getContentType();

		public Object getValue(String arg0);
		
	}
	

	public static class DocumentOfRecordResultImpl  implements DocumentOfRecordResult{
		private final DoRResult dorResult;

		private DocumentOfRecordResultImpl(DoRResult dorResult) {
			super();
			this.dorResult = Objects.requireNonNull(dorResult, "Adobe Document of Record Service returned null result.");
		}

		public byte[] getContent() {
			return dorResult.getContent();
		}

		public String getContentType() {
			return dorResult.getContentType();
		}

		public Object getValue(String arg0) {
			return dorResult.getValue(arg0);
		}
		
	}
	
	public static class DocumentOfRecordOptions {
		private final DoROptions dorOptions;

		private DocumentOfRecordOptions(DoROptions dorOptions) {
			super();
			this.dorOptions = dorOptions;
		}

		public String getData() {
			return dorOptions.getData();
		}

		public List<FileAttachmentWrapper> getFileAttachments() {
			return dorOptions.getFileAttachments();
		}

		public Resource getFormResource() {
			return dorOptions.getFormResource();
		}

		public boolean getIncludeAttachments() {
			return dorOptions.getIncludeAttachments();
		}

		public Locale getLocale() {
			return dorOptions.getLocale();
		}

		private DoROptions getDorOptions() {
			return dorOptions;
		}
	}

	public static class DocumentOfRecordOptionsBuilder {
		private String data = null;
		private List<FileAttachmentWrapper> attachments = null;
		private Resource afResource = null;
		private Boolean includeAttachments = null;
		private Locale locale = null;
		
		private DocumentOfRecordOptionsBuilder() {
		}
		
		public static DocumentOfRecordOptionsBuilder create() {
			return new DocumentOfRecordOptionsBuilder();
		}
		
		public DocumentOfRecordOptionsBuilder setData(String data) {
			this.data = data;
			return this;
		}

		public DocumentOfRecordOptionsBuilder setFileAttachments(List<FileAttachmentWrapper> attachments) {
			this.attachments = attachments;
			return this;
		}

		public DocumentOfRecordOptionsBuilder setFormResource(Resource afResource) {
			this.afResource = afResource;
			return this;
		}

		public DocumentOfRecordOptionsBuilder setIncludeAttachments(boolean includeAttachments) {
			this.includeAttachments = includeAttachments;
			return this;
		}

		public DocumentOfRecordOptionsBuilder setLocale(Locale locale) {
			this.locale = locale;
			return this;
		}

		public DocumentOfRecordOptions build() {
			DoROptions dorOptions = new DoROptions();
			if (data != null) 				dorOptions.setData(data);
			if (attachments != null) 		dorOptions.setFileAttachments(attachments);
			if (afResource != null) 		dorOptions.setFormResource(afResource);
			if (includeAttachments != null) dorOptions.setIncludeAttachments(includeAttachments);
			if (locale != null) 			dorOptions.setLocale(locale);
			return new DocumentOfRecordOptions(dorOptions);
		}
	}
	
	@SuppressWarnings("serial")
	public static class DocumentOfRecordException extends Exception {

		public DocumentOfRecordException() {
			super();
		}

		public DocumentOfRecordException(String arg0, Throwable arg1) {
			super(arg0, arg1);
		}

		public DocumentOfRecordException(String arg0) {
			super(arg0);
		}

		public DocumentOfRecordException(Throwable arg0) {
			super(arg0);
		}
	}
}
