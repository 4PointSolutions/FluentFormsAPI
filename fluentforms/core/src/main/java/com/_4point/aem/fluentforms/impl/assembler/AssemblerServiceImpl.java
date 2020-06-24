package com._4point.aem.fluentforms.impl.assembler;

import java.util.Map;
import java.util.Objects;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSetter;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSpec;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.api.assembler.AssemblerService;
import com._4point.aem.fluentforms.impl.UsageContext;

public class AssemblerServiceImpl implements AssemblerService {
	private final TraditionalDocAssemblerService adobDocAssemblerService;
	private final UsageContext usageContext;

	public AssemblerServiceImpl(TraditionalDocAssemblerService adobDocAssemblerService, UsageContext usageContext) {
		super();
		this.adobDocAssemblerService = new SafeDocAssemblerServiceAdapterWrapper(adobDocAssemblerService);
		this.usageContext = usageContext;
	}

	@Override
	public AssemblerResult invoke(Document ddx, Map<String, Object> sourceDocuments,
			AssemblerOptionsSpec assemblerOptionSpec) throws AssemblerServiceException {
		// TODO Auto-generated method stub
		Objects.requireNonNull(ddx, "ddx Document cannot be null.");
		Objects.requireNonNull(sourceDocuments, "sourceDocuments cannot be null.");
		try {
			return adobDocAssemblerService.invoke(ddx, sourceDocuments, assemblerOptionSpec);
		} catch (Exception e) {
			throw new AssemblerServiceException(e);
		}
	}

	private class AssemblerArgumentBuilderImpl implements AssemblerArgumentBuilder {

		AssemblerOptionsSpec assemblerOptionsSpec = new AssemblerOptionsSpecImpl();

		@Override
		public AssemblerArgumentBuilder setFailOnError(boolean isFailOnError) {
			this.assemblerOptionsSpec.setFailOnError(isFailOnError);
			return this;
		}

		@Override
		public AssemblerResult executeOn(Document ddx, Map<String, Object> sourceDocuments) throws AssemblerServiceException {
			return invoke(ddx, sourceDocuments, this.assemblerOptionsSpec);

		}

		@Override
		public AssemblerOptionsSetter setContentRoot(PathOrUrl contentRoot) {
			this.assemblerOptionsSpec.setContentRoot(contentRoot);
			return this;
		}

	}

	@Override
	public AssemblerArgumentBuilder invoke() {
		return new AssemblerArgumentBuilderImpl();
	}

}
