package com._4point.aem.fluentforms.impl.output;

import com._4point.aem.fluentforms.api.output.BatchOptions;

public class BatchOptionsImpl implements BatchOptions {

	private final boolean generateManyFiles;

	public static BatchOptionsImpl create(boolean generateManyFiles) {
		return new BatchOptionsImpl(generateManyFiles);
	}

	public static BatchOptionsImpl create() {
		return new BatchOptionsImpl();
	}

	private BatchOptionsImpl(boolean generateManyFiles) {
		super();
		this.generateManyFiles = generateManyFiles;
	}

	private BatchOptionsImpl() {
		super();
		this.generateManyFiles = false;
	}

	@Override
	public boolean getGenerateManyFiles() {
		return this.generateManyFiles;
	}

}
