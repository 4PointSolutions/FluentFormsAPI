package com._4point.aem.docservices.rest_services.server.data;

import com.adobe.forms.common.service.DataXMLOptions;
import com.adobe.forms.common.service.DataXMLProvider;
import com.adobe.forms.common.service.FormsException;
import java.io.InputStream;
import javax.jcr.Node;
import org.apache.sling.api.resource.NonExistingResource;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

@Component(
		immediate = true,
//		metatype = true,
//		label = "Default Form Prefill service",
		service = DataXMLProvider.class 
)

public class Prefill implements DataXMLProvider {
	private Logger logger = LoggerFactory.getLogger(Prefill.class);

	public Prefill() {
	}

	public InputStream getDataXMLForDataRef(DataXMLOptions options) throws FormsException {
		if (options.getDataRef() == null | "".equals(options.getDataRef())) {
			Resource formResource = options.getFormResource();
			ResourceResolver resolver = formResource.getResourceResolver();
			InputStream result = null;
			try {
				String nodePath = "/content/prefillservice.xml";
				Resource fileResource = resolver.resolve(nodePath);
				if (fileResource instanceof NonExistingResource) {
					return null;
				} else {
					Node jcrNode = (Node) fileResource.adaptTo(Node.class);
					Node jcrContent = jcrNode.getNode("jcr:content");
					result = jcrContent.getProperty("jcr:data").getBinary().getStream();
					return result;
				}
			} catch (Exception var9) {
				this.logger.warn("unable to read data for the dataRef ");
				throw new FormsException(var9);
			}
		} else {
			return null;
		}
	}

	public String getServiceName() {
		return "myServiceName";
	}

	public String getServiceDescription() {
		return "My Service";
	}

}