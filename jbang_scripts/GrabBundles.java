///usr/bin/env jbang "$0" "$@" ; exit $?
//REPOS mavencentral,github=https://maven.pkg.github.com/rmcdouga/*
//DEPS info.picocli:picocli:4.6.3
//DEPS com.github.rmcdouga:github-package-repo:0.0.1-SNAPSHOT
//DEPS org.slf4j:slf4j-simple:1.7.36
//JAVA 17+

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.Callable;

import com.github.rmcdouga.ghrepo.GithubPackages;
import com.github.rmcdouga.ghrepo.GithubPackages.Repo.Group.Artifact.Version;

/**
 * This JBang script will grab the latest bundles from the 4PointSolutions (GitHub) package repository.
 * 
 * NOTE: The GitHub package repositories require user authentication.  This script uses your maven 
 * credentials to log into the GitHub package repository.  This means that, in order to
 * pull down the .jars, a user needs to have setup their credentials in their local ~/.m2/settings.xml file.
 * 
 * Your settings.xml should look something like this:
 *  <?xml version="1.0"?>
 *  <settings xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://maven.apache.org/SETTINGS/1.0.0">
 *  	<servers>
 *  		<server>
 *  			<id>github</id>
 *  			<username>Your GitHub Username goes here</username>
 *  			<password>Your Personal Access Token goes here</password>
 *  		</server>
 *  	</servers>
 *  </settings>
 *  
 */
@Command(name = "GrabBundles", mixinStandardHelpOptions = true, version = "GrabBundles 0.1",
        description = "GrabBundles will grab the latest bundle .jar files from the 4PointSolutions (GitHub) package repository.")
class GrabJars implements Callable<Integer> {

	@Parameters(index = "0", description = "Destination directory where .jars will be written.", defaultValue = ".")
    private String desintationDir;

    public static void main(String... args) {
        int exitCode = new CommandLine(new GrabJars()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
    	final Path dest = Path.of(desintationDir);
    	GithubPackages packages = GithubPackages.create().verboseMode(true);

    	packages.repo("4PointSolutions", "FluentFormsAPI")
				.group("com._4point.aem.docservices")
				.artifact("rest-services.server")
				.version("0.0.2-SNAPSHOT")
				.copyTo(dest, StandardCopyOption.REPLACE_EXISTING);
		
		packages.repo("4PointSolutions", "FluentFormsAPI")
				.group("com._4point.aem")
				.artifact("fluentforms.core")
				.version("0.0.2-SNAPSHOT")
				.copyTo(dest, StandardCopyOption.REPLACE_EXISTING);

        return 0;
    }
}
