package com._4point.aem.fluentforms.impl.convertPdf;

import static com._4point.aem.fluentforms.impl.BuilderUtils.setIfNotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.convertPdf.ConvertPdfService.ConvertPdfServiceException;
import com._4point.aem.fluentforms.api.convertPdf.ToImageOptionsSpec;
import com._4point.aem.fluentforms.api.convertPdf.ToPSOptionsSpec;
import com._4point.aem.fluentforms.impl.AdobeDocumentFactoryImpl;

import com.adobe.fd.cpdf.api.enumeration.ImageConvertFormat;

public class AdobeConvertPdfServiceAdapter implements TraditionalConvertPdfService {

	private static final Logger log = LoggerFactory.getLogger(AdobeConvertPdfServiceAdapter.class);
	private final DocumentFactory documentFactory;
	private final com.adobe.fd.cpdf.api.ConvertPdfService convertPdfService;
	
	public AdobeConvertPdfServiceAdapter(com.adobe.fd.cpdf.api.ConvertPdfService convertPdfService) {
		super();
		this.documentFactory = DocumentFactory.getDefault();
		this.convertPdfService = Objects.requireNonNull(convertPdfService, "Adobe Convert PDF Service cannot be null.");
	}
	
	public AdobeConvertPdfServiceAdapter(com.adobe.fd.cpdf.api.ConvertPdfService convertPdfService, DocumentFactory documentFactory) {
		super();
		this.documentFactory = Objects.requireNonNull(documentFactory, "Document Factory cannot be null.");
		this.convertPdfService = Objects.requireNonNull(convertPdfService, "Adobe Convert PDF Service cannot be null.");
	}
	
	@Override
	public List<Document> toImage(Document inPdfDoc, ToImageOptionsSpec toImageOptionsSpec) throws ConvertPdfServiceException {
		List<Document> resultDocList = Collections.emptyList();
		try {
			List<com.adobe.aemfd.docmanager.Document> imageList = convertPdfService.toImage(AdobeDocumentFactoryImpl.getAdobeDocument(inPdfDoc), toImageOptionsSpec(toImageOptionsSpec));
			for (com.adobe.aemfd.docmanager.Document img : imageList) {
				Document resultDoc = documentFactory.create(img);
				resultDoc.setContentType(getMimeType(toImageOptionsSpec.getImageConvertFormat()));
				resultDocList.add(resultDoc);
			}
			return resultDocList;
		} catch (com.adobe.fd.cpdf.api.ConvertPdfException e) {
			throw new ConvertPdfServiceException(e);
		}
	}
	
	static com.adobe.fd.cpdf.api.ToImageOptionsSpec toImageOptionsSpec(ToImageOptionsSpec options) {
		com.adobe.fd.cpdf.api.ToImageOptionsSpec adobeOptions = new com.adobe.fd.cpdf.api.ToImageOptionsSpec();
		setIfNotNull(adobeOptions::setCmykPolicy, options.getCmykPolicy());
		setIfNotNull(adobeOptions::setColorCompression, options.getColorCompression());
		setIfNotNull(adobeOptions::setColorSpace, options.getColorSpace());
		setIfNotNull(adobeOptions::setFilter, options.getFilter());
		setIfNotNull(adobeOptions::setFormat, options.getFormat());
		setIfNotNull(adobeOptions::setGrayScaleCompression, options.getGrayScaleCompression());
		setIfNotNull(adobeOptions::setGrayScalePolicy, options.getGrayScalePolicy());
		setIfNotNull(adobeOptions::setImageConvertFormat, options.getImageConvertFormat());
		setIfNotNull(adobeOptions::setImageSizeHeight, options.getImageSizeHeight());
		setIfNotNull(adobeOptions::setImageSizeWidth, options.getImageSizeWidth());
		setIfNotNull(adobeOptions::setInterlace, options.getInterlace());
		setIfNotNull(adobeOptions::setMonochrome, options.getMonochrome());
		setIfNotNull(adobeOptions::setMultiPageTiff, options.getMultiPageTiff());
		setIfNotNull(adobeOptions::setPageRange, options.getPageRange());
		setIfNotNull(adobeOptions::setResolution, options.getResolution());
		setIfNotNull(adobeOptions::setRgbPolicy, options.getRgbPolicy());
		setIfNotNull(adobeOptions::setRowsPerStrip, options.getRowsPerStrip());
		setIfNotNull(adobeOptions::setTileSize, options.getTileSize());
		setIfNotNull(adobeOptions::setIncludeComments, options.isIncludeComments());
		setIfNotNull(adobeOptions::setUseLegacyImageSizeBehavior, options.isUseLegacyImageSizeBehavior());
		log.info("CMYKPolicy={}", adobeOptions.getCmykPolicy().toString());
		log.info("ColorCompression={}", adobeOptions.getColorCompression()); //.toString());
		log.info("ColorSpace={}", adobeOptions.getColorSpace().toString());
		log.info("PNGFilter={}", adobeOptions.getFilter().toString());
		log.info("JPEGFormat={}", adobeOptions.getFormat().toString());
		log.info("GrayScaleCompression={}", adobeOptions.getGrayScaleCompression()); //.toString());
		log.info("GrayScalePolicy={}", adobeOptions.getGrayScalePolicy().toString());
		log.info("ImageConvertFormat={}", adobeOptions.getImageConvertFormat()); //.toString());
		log.info("ImageSizeHeight={}", adobeOptions.getImageSizeHeight());
		log.info("ImageSizeWidth={}", adobeOptions.getImageSizeWidth());
		log.info("IncludeComments={}", adobeOptions.isIncludeComments());
		log.info("Interlace={}", adobeOptions.getInterlace().toString());
		log.info("MonochromeCompression={}", adobeOptions.getMonochrome().toString());
		log.info("MultiPageTIFF={}", adobeOptions.getMultiPageTiff());
		log.info("PageRange={}", adobeOptions.getPageRange());
		log.info("Resolution={}", adobeOptions.getResolution());
		log.info("RGBPolicy={}", adobeOptions.getRgbPolicy().toString());
		log.info("RowsPerStrip={}", adobeOptions.getRowsPerStrip());
		log.info("TileSize={}", adobeOptions.getTileSize());
		log.info("UseLegacyImageSizeBehavior={}", adobeOptions.isUseLegacyImageSizeBehavior());
		return adobeOptions;
	}
	
	private String getMimeType(ImageConvertFormat imageConvertFormat) {
		String mimeType = "";
		if (imageConvertFormat == ImageConvertFormat.JPEG) {
			mimeType = "image/jpeg";
		} else if (imageConvertFormat == ImageConvertFormat.JPEG2K) {
			mimeType = "image/jp2";
		} else if (imageConvertFormat == ImageConvertFormat.PNG) {
			mimeType = "image/png";
		} else if (imageConvertFormat == ImageConvertFormat.TIFF) {
			mimeType = "image/tiff";
		}
		return mimeType;
	}
	
	@Override
	public Document toPS(Document inPdfDoc, ToPSOptionsSpec toPSOptionsSpec) throws ConvertPdfServiceException {
		try {
			Document resultDoc = documentFactory.create(convertPdfService.toPS(AdobeDocumentFactoryImpl.getAdobeDocument(inPdfDoc), toPSOptionsSpec(toPSOptionsSpec)));
			resultDoc.setContentType("application/postscript");
			return resultDoc;
		} catch (com.adobe.fd.cpdf.api.ConvertPdfException e) {
			throw new ConvertPdfServiceException(e);
		}
	}
	
	static com.adobe.fd.cpdf.api.ToPSOptionsSpec toPSOptionsSpec(ToPSOptionsSpec options) {
		com.adobe.fd.cpdf.api.ToPSOptionsSpec adobeOptions = new com.adobe.fd.cpdf.api.ToPSOptionsSpec();
		setIfNotNull(adobeOptions::setFontInclusion, options.getFontInclusion());
		setIfNotNull(adobeOptions::setLineWeight, options.getLineWeight());
		setIfNotNull(adobeOptions::setPageRange, options.getPageRange());
		setIfNotNull(adobeOptions::setPageSize, options.getPageSize());
		setIfNotNull(adobeOptions::setPageSizeHeight, options.getPageSizeHeight());
		setIfNotNull(adobeOptions::setPageSizeWidth, options.getPageSizeWidth());
		setIfNotNull(adobeOptions::setPsLevel, options.getPsLevel());
		setIfNotNull(adobeOptions::setStyle, options.getStyle());
		setIfNotNull(adobeOptions::setAllowBinaryContent, options.isAllowBinaryContent());
		setIfNotNull(adobeOptions::setBleedMarks, options.isBleedMarks());
		setIfNotNull(adobeOptions::setColorBars, options.isColorBars());
		setIfNotNull(adobeOptions::setConvertTrueTypeToType1, options.isConvertTrueTypeToType1());
		setIfNotNull(adobeOptions::setEmitCIDFontType2, options.isEmitCIDFontType2());
		setIfNotNull(adobeOptions::setEmitPSFormObjects, options.isEmitPSFormObjects());
		setIfNotNull(adobeOptions::setExpandToFit, options.isExpandToFit());
		setIfNotNull(adobeOptions::setIncludeComments, options.isIncludeComments());
		setIfNotNull(adobeOptions::setLegacyToSimplePSFlag, options.isLegacyToSimplePSFlag());
		setIfNotNull(adobeOptions::setPageInformation, options.isPageInformation());
		setIfNotNull(adobeOptions::setRegistrationMarks, options.isRegistrationMarks());
		setIfNotNull(adobeOptions::setReverse, options.isReverse());
		setIfNotNull(adobeOptions::setRotateAndCenter, options.isRotateAndCenter());
		setIfNotNull(adobeOptions::setShrinkToFit, options.isShrinkToFit());
		setIfNotNull(adobeOptions::setTrimMarks, options.isTrimMarks());
		setIfNotNull(adobeOptions::setUseMaxJPEGImageResolution, options.isUseMaxJPEGImageResolution());
		log.info("FontInclusion={}", adobeOptions.getFontInclusion().toString());
		log.info("LineWeight={}", adobeOptions.getLineWeight().toString());
		log.info("PageRange={}", adobeOptions.getPageRange());
		log.info("PageSize={}", adobeOptions.getPageSize().toString());
		log.info("PageSizeHeight={}", adobeOptions.getPageSizeHeight());
		log.info("PageSizeWidth={}", adobeOptions.getPageSizeWidth());
		log.info("PsLevel={}", adobeOptions.getPsLevel().toString());
		log.info("Style={}", adobeOptions.getStyle().toString());
		log.info("AllowBinaryContent={}", adobeOptions.isAllowBinaryContent());
		log.info("BleedMarks={}", adobeOptions.isBleedMarks());
		log.info("ColorBars={}", adobeOptions.isColorBars());
		log.info("ConvertTrueTypeToType1={}", adobeOptions.isConvertTrueTypeToType1());
		log.info("EmitCIDFontType2={}", adobeOptions.isEmitCIDFontType2());
		log.info("EmitPSFormObjects={}", adobeOptions.isEmitPSFormObjects());
		log.info("ExpandToFit={}", adobeOptions.isExpandToFit());
		log.info("IncludeComments={}", adobeOptions.isIncludeComments());
		log.info("LegacyToSimplePSFlag={}", adobeOptions.isLegacyToSimplePSFlag());
		log.info("PageInformation={}", adobeOptions.isPageInformation());
		log.info("RegistrationMarks={}", adobeOptions.isRegistrationMarks());
		log.info("Reverse={}", adobeOptions.isReverse());
		log.info("RotateAndCenter={}", adobeOptions.isRotateAndCenter());
		log.info("ShrinkToFit={}", adobeOptions.isShrinkToFit());
		log.info("TrimMarks={}", adobeOptions.isTrimMarks());
		log.info("UseMaxJPEGImageResolution={}", adobeOptions.isUseMaxJPEGImageResolution());
		return adobeOptions;
	}
}
