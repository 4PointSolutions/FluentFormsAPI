package com._4point.aem.fluentforms.sampleapp.ports;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com._4point.aem.fluentforms.sampleapp.domain.DataService;

public final class LocalFolderDataService implements DataService {
	
	private final Path folder;

	public LocalFolderDataService(Path folder) {
		this.folder = folder;
	}

	@Override
	public byte[] load(String key) {
		try {
			return Files.readAllBytes(folder.resolve(key));
		} catch (IOException e) {
			throw new DataServiceException("Error while reading file '%s' from folder '%s'.".formatted(key, folder), e);
		}
	}

	@Override
	public void save(String key, byte[] data) {
		try {
			Files.write(folder.resolve(key), data, StandardOpenOption.CREATE_NEW);
		} catch (IOException e) {
			throw new DataServiceException("Error while writing file '%s' from folder '%s'.".formatted(key, folder), e);
		}
	}

	
}
