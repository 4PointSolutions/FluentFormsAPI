package com._4point.aem.fluentforms.sampleapp.resources;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com._4point.aem.fluentforms.spring.AemProxyAfSubmission;
import com._4point.aem.fluentforms.spring.AemProxyAfSubmission.AfSubmissionHandler.SubmitResponse.Response;

@Component
public class AfSubmissionHandler implements AemProxyAfSubmission.AfSubmissionHandler {
	private final static Logger logger = LoggerFactory.getLogger(AfSubmissionHandler.class);
	public static final String AF_TEMPLATE_NAME = "sample00002test";

	@Override
	public boolean canHandle(String formName) {
		return Objects.equals(AF_TEMPLATE_NAME, formName);
	}

	@Override
	public SubmitResponse processSubmission(Submission submission) {
		// TODO: Implement a less trivial submission handler, maybe one that generates and returns a PDF.
		logger.atInfo().log("Received submission");
		return Response.text("Successful Submit");
	}

}
