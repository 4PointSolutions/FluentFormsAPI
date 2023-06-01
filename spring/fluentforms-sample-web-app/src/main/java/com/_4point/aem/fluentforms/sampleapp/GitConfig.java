package com._4point.aem.fluentforms.sampleapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.ClassPathResource;

import com._4point.aem.fluentforms.api.PathOrUrl;

@Configuration
public class GitConfig {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Value("${git.build.time}")
    private String commitBuildTime;
    @Value("${git.branch}")
    private String branch;
    @Value("${git.commit.time}")
    private String commitTime;
    @Value("${git.commit.id.abbrev}")
    private String commitIdAbbrev;
    @Value("${git.dirty}")
    private String dirty;

    public String getCommitBuildTime() {
		return commitBuildTime;
	}
	public String getBranch() {
		return branch;
	}
	public String getCommitTime() {
		return commitTime;
	}
	public String getCommitIdAbbrev() {
		return commitIdAbbrev;
	}
	
	public void logGitInformation() {
    	log.info("Build Information: Branch '{}', Commit Id '{}'{} ({}), Build Time '{}'.", branch, commitIdAbbrev, (Boolean.parseBoolean(dirty) ? "*" : "") + "'" + dirty + "'", commitTime, commitBuildTime);
	}

	@Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        PropertySourcesPlaceholderConfigurer propsConfig = new PropertySourcesPlaceholderConfigurer();
        propsConfig.setLocation(new ClassPathResource("git.properties"));
        propsConfig.setIgnoreResourceNotFound(true);
        propsConfig.setIgnoreUnresolvablePlaceholders(true);
        return propsConfig;
    }

//	@Configuration
//	public static class BooleanConverter implements Converter<String, Boolean> {
//
//		@Override
//		public Boolean convert(String source) {
//			System.out.println("---Converting '" + source + ".");
//			return Boolean.parseBoolean(source);
//		}
//		
//	}

}
