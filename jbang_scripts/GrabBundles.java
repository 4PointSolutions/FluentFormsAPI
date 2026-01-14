///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS info.picocli:picocli:4.7.7
//DEPS io.github.rmcdouga:github-package-repo:0.0.2
//DEPS org.slf4j:slf4j-simple:2.0.17
//JAVA 25+

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
				.version("0.0.5-SNAPSHOT")
				.copyTo(dest, StandardCopyOption.REPLACE_EXISTING);
		
		packages.repo("4PointSolutions", "FluentFormsAPI")
				.group("com._4point.aem")
				.artifact("fluentforms.core")
				.version("0.0.5-SNAPSHOT")
				.copyTo(dest, StandardCopyOption.REPLACE_EXISTING);

        return 0;
    }
}
