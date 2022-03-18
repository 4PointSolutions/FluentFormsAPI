package com._4point.aem.docservices.rest_services.cli.restservices.cli;

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
    private String commitBuildTime;
    @Value("${git.branch}")
    private String branch;
    @Value("${git.commit.time}")
    private String commitTime;
    @Value("${git.commit.id.abbrev}")
    private String commitIdAbbrev;
    @Value("${git.dirty}")
    private boolean dirty;

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
    	log.info("Build Information: Branch '{}', Commit Id '{}'{} ({}), Build Time '{}'.", branch, commitIdAbbrev, dirty ? "*" : "", commitTime, commitBuildTime);
	}

	@Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        PropertySourcesPlaceholderConfigurer propsConfig = new PropertySourcesPlaceholderConfigurer();
        propsConfig.setLocation(new ClassPathResource("git.properties"));
        propsConfig.setIgnoreResourceNotFound(true);
        propsConfig.setIgnoreUnresolvablePlaceholders(true);
        return propsConfig;
    }

}
