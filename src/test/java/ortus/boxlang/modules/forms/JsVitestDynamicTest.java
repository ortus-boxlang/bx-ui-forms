package ortus.boxlang.modules.forms;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsVitestDynamicTest {

	private static final Duration	NPM_TIMEOUT	= Duration.ofMinutes( 5 );
	private static final Path		JSON_REPORT	= Path.of( "build", "test-results", "js", "vitest-results.json" );

	@TestFactory
	List<DynamicTest> vitestResultsAsJUnitTests() throws Exception {
		String npmCommand = isWindows() ? "npm.cmd" : "npm";
		if ( !isCommandAvailable( npmCommand ) ) {
			Assumptions.assumeTrue( false, "Skipping JS tests because npm was not found on PATH." );
		}

		String commandOutput = runVitestJson( npmCommand );
		assertFalse( Files.notExists( JSON_REPORT ), "Vitest JSON report was not generated." );

		ObjectMapper		mapper	= new ObjectMapper();
		JsonNode			root	= mapper.readTree( JSON_REPORT.toFile() );
		List<DynamicTest>	tests	= new ArrayList<>();

		JsonNode			suites	= root.path( "testResults" );
		if ( !suites.isArray() || suites.isEmpty() ) {
			throw new IllegalStateException( "Vitest JSON report has no test suites. Output:\n" + commandOutput );
		}

		for ( JsonNode suite : suites ) {
			JsonNode assertions = suite.path( "assertionResults" );
			for ( JsonNode assertion : assertions ) {
				String	fullName	= assertion.path( "fullName" ).asText( assertion.path( "title" ).asText( "Unnamed JS test" ) );
				String	status		= assertion.path( "status" ).asText( "unknown" );
				String	failure		= joinFailures( assertion.path( "failureMessages" ) );

				tests.add( DynamicTest.dynamicTest( "[JS] " + fullName, () -> {
					switch ( status.toLowerCase( Locale.ROOT ) ) {
						case "passed" :
							return;
						case "pending" :
						case "skipped" :
						case "todo" :
							Assumptions.assumeTrue( false, "Skipped by Vitest: " + fullName );
							return;
						case "failed" :
							fail( failure.isEmpty()
							    ? "Vitest test failed: " + fullName + "\n\nNPM output:\n" + commandOutput
							    : failure );
							return;
						default :
							fail( "Unexpected Vitest status '" + status + "' for " + fullName );
					}
				} ) );
			}
		}

		if ( tests.isEmpty() ) {
			throw new IllegalStateException( "No Vitest assertions were found in JSON report." );
		}

		return tests;
	}

	private static String runVitestJson( String npmCommand ) throws Exception {
		Files.createDirectories( JSON_REPORT.getParent() );

		ProcessBuilder	processBuilder	= new ProcessBuilder( npmCommand, "run", "test:json" )
		    .directory( new File( "." ) )
		    .redirectErrorStream( true );

		Process			process			= processBuilder.start();
		String			output;
		try ( BufferedReader reader = new BufferedReader( new InputStreamReader( process.getInputStream(), StandardCharsets.UTF_8 ) ) ) {
			StringBuilder	stdout	= new StringBuilder();
			String			line;
			while ( ( line = reader.readLine() ) != null ) {
				stdout.append( line ).append( System.lineSeparator() );
			}
			output = stdout.toString();
		}

		boolean completed = process.waitFor( NPM_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS );
		if ( !completed ) {
			process.destroyForcibly();
			throw new IllegalStateException( "Timed out running npm test:json" );
		}

		if ( process.exitValue() != 0 ) {
			throw new IllegalStateException( "npm test:json failed with exit code " + process.exitValue() + "\n" + output );
		}

		return output;
	}

	private static String joinFailures( JsonNode failureMessagesNode ) {
		if ( !failureMessagesNode.isArray() || failureMessagesNode.isEmpty() ) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		for ( JsonNode message : failureMessagesNode ) {
			if ( builder.length() > 0 ) {
				builder.append( System.lineSeparator() ).append( System.lineSeparator() );
			}
			builder.append( message.asText() );
		}
		return builder.toString();
	}

	private static boolean isCommandAvailable( String command ) {
		try {
			Process	process		= new ProcessBuilder( command, "--version" )
			    .redirectErrorStream( true )
			    .start();
			boolean	completed	= process.waitFor( 10, TimeUnit.SECONDS );
			return completed && process.exitValue() == 0;
		} catch ( IOException | InterruptedException e ) {
			return false;
		}
	}

	private static boolean isWindows() {
		return System.getProperty( "os.name", "" ).toLowerCase( Locale.ROOT ).contains( "win" );
	}
}
