package com._4point.aem.docservices.rest_services.it_tests;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;

import com._4point.aem.docservices.rest_services.it_tests.Pdf.UsageRights.UsageRightsBuilder;

public class Pdf implements AutoCloseable {
	private static final String NEEDS_RENDERING_KEY = "NeedsRendering";
	private static final String PERMISSIONS_KEY = "Perms";
	private static final String USAGE_RIGHTS_KEY_OLD = "UR";
	private static final String USAGE_RIGHTS_KEY_NEW = "UR3";
	
	private final PDDocument doc;
	private final PDDocumentCatalog catalog;

	private Pdf(PDDocument doc) {
		super();
		this.doc = doc;
		this.catalog = doc.getDocumentCatalog();
	}
	
	public boolean isDynamic() {
		COSDictionary cosObject = this.catalog.getCOSObject();
		return cosObject.getBoolean(NEEDS_RENDERING_KEY, false);

	}
	
	public boolean isInteractive() {
		return this.catalog.getAcroForm() != null;
	}
	
	public boolean hasXfa() {
		return this.catalog.getAcroForm() != null && this.catalog.getAcroForm().hasXFA();
	}
	
	public boolean hasRights() {
		return getUsageRights() != null;
	}
	
	public UsageRights getUsageRights() {
		COSDictionary cosObject = this.catalog.getCOSObject();
		COSDictionary permissionsDictionary = (COSDictionary)cosObject.getDictionaryObject(PERMISSIONS_KEY);
		if (permissionsDictionary != null) {
			COSDictionary usageRightsDictionary = (COSDictionary)permissionsDictionary.getDictionaryObject(COSName.getPDFName(USAGE_RIGHTS_KEY_OLD), COSName.getPDFName(USAGE_RIGHTS_KEY_NEW));
			if (usageRightsDictionary != null) {
				COSArray referenceObject = (COSArray)usageRightsDictionary.getDictionaryObject("Reference");
				if (referenceObject.size() == 1) {
					COSDictionary usageRightsDictionary2 = (COSDictionary)referenceObject.get(0);
					if (usageRightsDictionary2 != null) {
						COSDictionary transformParamsDictionary = (COSDictionary) usageRightsDictionary2.getDictionaryObject("TransformParams");
						COSArray annotsPermissions = (COSArray)transformParamsDictionary.getDictionaryObject("Annots");
						COSArray formPermissions = (COSArray)transformParamsDictionary.getDictionaryObject("Form");
						COSArray formExPermissions = (COSArray)transformParamsDictionary.getDictionaryObject("FormEx");
						COSArray efPermissions = (COSArray)transformParamsDictionary.getDictionaryObject("EF");
//						boolean p = transformParamsDictionary.getBoolean("P", false);
//						System.out.println("Annots='" + annotsPermissions.toString() + "'");
//						System.out.println("Form='" + formPermissions.toString() + "'");
////						System.out.println("FormEx='" + formExPermissions.toString() + "'");
////						System.out.println("EF='" + efPermissions.toString() + "'");
//						System.out.println("p='" + p + "'");
						UsageRightsBuilder builder = UsageRightsBuilder.instance();
						if (annotsPermissions != null) {
							for(COSBase name : annotsPermissions) {
							builder.addAnnotsRight(((COSName)name).getName());
							}
						}
						if (formPermissions != null) {
							for(COSBase name : formPermissions) {
							builder.addFormRight(((COSName)name).getName());
							}
						}
						if (formExPermissions != null) {
							for(COSBase name : formExPermissions) {
							builder.addFormExRight(((COSName)name).getName());
							}
						}
						if (efPermissions != null) {
							for(COSBase name : efPermissions) {
							builder.addEfRight(((COSName)name).getName());
							}
						}
						
						return builder.build();
					}
					
//					Set<Entry<COSName, COSBase>> entrySet = usageRightsDictionary2.entrySet();
//					for(Entry<COSName, COSBase> entry : entrySet) {
//						String entryString = entry.getKey().toString();
//						System.out.println("Key='" + entryString + "'.");
////						if (entryString.contains("Type") || entryString.contains("M") || entryString.contains("{Name}") || entryString.contains("{Prop_Build}")) {
////							System.out.println("   Value=" + entry.getValue().toString() );
////						}
//					}

				} else {
					throw new IllegalStateException("Expected only 1 entry in the UR Array.");
				}
			}
		}
		
		return null;
	}
	
	public static Pdf from(byte[] docBytes) throws PdfException  {
		 try {
			return new Pdf(PDDocument.load(docBytes));
		} catch (IOException e) {
			throw new PdfException(e);
		}
	}
	
	public static Pdf from(InputStream docStream) throws PdfException  {
		 try {
			return new Pdf(PDDocument.load(docStream));
		} catch (IOException e) {
			throw new PdfException(e);
		}
	}
	
	public static Pdf from(Path docPath) throws PdfException {
		try {
			return Pdf.from(Files.newInputStream(docPath));
		} catch (PdfException | IOException e) {
			throw new PdfException("Error reading file (" + docPath.toString() + ")", e);
		}
	}

	public static class UsageRights {
		private final Set<String> annotsRights;
		private final Set<String> formRights;
		private final Set<String> formExRights;
		private final Set<String> efRights;
		private UsageRights(Set<String> annotsRights, Set<String> formRights, Set<String> formExRights,
				Set<String> efRights) {
			super();
			this.annotsRights = annotsRights;
			this.formRights = formRights;
			this.formExRights = formExRights;
			this.efRights = efRights;
		}
		public Set<String> getAnnotsRights() {
			return annotsRights;
		}
		public Set<String> getFormRights() {
			return formRights;
		}
		public Set<String> getFormExRights() {
			return formExRights;
		}
		public Set<String> getEfRights() {
			return efRights;
		}
		
		public static class UsageRightsBuilder {
			private final Set<String> annotsRights = new HashSet<>();
			private final Set<String> formRights = new HashSet<>();
			private final Set<String> formExRights = new HashSet<>();
			private final Set<String> efRights = new HashSet<>();
			
			private UsageRightsBuilder() {
				super();
			}
			public UsageRightsBuilder addAnnotsRight(String right) {
				annotsRights.add(right);
				return this;
			}
			public UsageRightsBuilder addFormRight(String right) {
				formRights.add(right);
				return this;
			}
			public UsageRightsBuilder addFormExRight(String right) {
				formExRights.add(right);
				return this;
			}
			public UsageRightsBuilder addEfRight(String right) {
				efRights.add(right);
				return this;
			}
			public UsageRights build() {
				return new UsageRights(Collections.unmodifiableSet(annotsRights), Collections.unmodifiableSet(formRights), Collections.unmodifiableSet(formExRights), Collections.unmodifiableSet(efRights));
			}
			public static UsageRightsBuilder instance() {
				return new UsageRightsBuilder();
			}
		}
	}
	
	@SuppressWarnings("serial")
	public static class PdfException extends Exception {

		private PdfException() {
			super();
		}

		private PdfException(String message, Throwable cause) {
			super(message, cause);
		}

		private PdfException(String message) {
			super(message);
		}

		private PdfException(Throwable cause) {
			super(cause);
		}
	}

	@Override
	public void close() throws Exception {
		doc.close();
	}
}
