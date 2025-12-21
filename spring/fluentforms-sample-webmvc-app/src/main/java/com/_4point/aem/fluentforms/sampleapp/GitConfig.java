package com._4point.aem.fluentforms.sampleapp;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class GitConfig {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Value("${git.build.time}")
    @Nullable private String commitBuildTime;
    @Value("${git.branch}")
    @Nullable private String branch;
    @Value("${git.commit.time}")
    @Nullable private String commitTime;
    @Value("${git.commit.id.abbrev}")
    @Nullable private String commitIdAbbrev;
    @Value("${git.dirty}")
    @Nullable private String dirty;

    public @Nullable String getCommitBuildTime() {
		return commitBuildTime;
	}
	public @Nullable String getBranch() {
		return branch;
	}
	public @Nullable String getCommitTime() {
		return commitTime;
	}
	public @Nullable String getCommitIdAbbrev() {
		return commitIdAbbrev;
	}
	
	public void logGitInformation() {
    	log.info("Build Information: Branch '{}', Commit Id '{}'{}, Build Time '{}'.", branch, commitIdAbbrev, (Boolean.parseBoolean(dirty) ? "*" : ""), commitTime, commitBuildTime);
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
