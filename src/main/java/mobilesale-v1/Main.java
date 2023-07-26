// /////////////////////////////////////////////////////////////////////////////
// REFCODES.ORG
// /////////////////////////////////////////////////////////////////////////////
// This code is written and provided by Siegfried Steiner, Munich, Germany.
// Feel free to use it as skeleton for your own applications. Make sure you have
// considered the license conditions of the included artifacts (pom.xml).
// -----------------------------------------------------------------------------
// The REFCODES.ORG artifacts used by this template are copyright (c) by
// Siegfried Steiner, Munich, Germany and licensed under the following
// (see "http://en.wikipedia.org/wiki/Multi-licensing") licenses:
// -----------------------------------------------------------------------------
// GNU General Public License, v3.0 ("http://www.gnu.org/licenses/gpl-3.0.html")
// -----------------------------------------------------------------------------
// Apache License, v2.0 ("http://www.apache.org/licenses/LICENSE-2.0")
// -----------------------------------------------------------------------------
// Please contact the copyright holding author(s) of the software artifacts in
// question for licensing issues not being covered by the above listed licenses,
// also regarding commercial licensing models or regarding the compatibility
// with other open source licenses.
// /////////////////////////////////////////////////////////////////////////////

package mobilesale-v1;

import static org.refcodes.cli.CliSugar.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.refcodes.archetype.CliHelper;
import org.refcodes.cli.Term;
import org.refcodes.cli.Example;
import org.refcodes.cli.Flag;
import org.refcodes.cli.Option;
import org.refcodes.data.AsciiColorPalette;
import org.refcodes.io.TimeoutInputStream;
import org.refcodes.logger.RuntimeLogger;
import org.refcodes.logger.RuntimeLoggerFactorySingleton;
import org.refcodes.properties.ext.application.ApplicationProperties;
import org.refcodes.runtime.Execution;
import org.refcodes.textual.Case;
import org.refcodes.textual.Font;
import org.refcodes.textual.FontFamily;
import org.refcodes.textual.FontStyle;

/**
 * A minimum REFCODES.ORG enabled Pipes and Filters command line interface (CLI) 
 * application. Get inspired by "https://bitbucket.org/funcodez".
 */
public class Main {

	// See "http://www.refcodes.org/blog/logging_like_the_nerds_log" |-->
	private static RuntimeLogger LOGGER = RuntimeLoggerFactorySingleton.createRuntimeLogger();
	// <--| See "http://www.refcodes.org/blog/logging_like_the_nerds_log"

	// /////////////////////////////////////////////////////////////////////////
	// STATICS:
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// CONSTANTS:
	// /////////////////////////////////////////////////////////////////////////

	private static final String NAME = "mobile-sale";
	private static final String TITLE = ">>> " + NAME.toUpperCase() + " >>>";
	private static final String DESCRIPTION = "A minimum REFCODES.ORG enabled command application for filtering a shell's piped data (pipes & filters). Get inspired by [https://bitbucket.org/funcodez].";
	private static final String LICENSE_NOTE = "Licensed under GNU General Public License, v3.0 and Apache License, v2.0";
	private static final String COPYRIGHT = "Copyright (c) by CLUB.FUNCODES (see [https://www.funcodes.club])";
	private static final char[] BANNER_PALETTE = AsciiColorPalette.MAX_LEVEL_GRAY.getPalette();
	private static final Font BANNER_FONT = new Font( FontFamily.DIALOG, FontStyle.BOLD );
	private static final String OUTPUTFILE_PROPERTY = "outputFile";
	private static final String INPUTFILE_PROPERTY = "inputFile";
	private static final String UPPER_CASE_PROPERTY = "upperCase";
	private static final String LOWER_CASE_PROPERTY = "lowerCase";
	private static final String TIMEOUT_PROPERTY = "timeout";
	private static final int BUFFER_SIZE = 128;

	// /////////////////////////////////////////////////////////////////////////
	// VARIABLES:
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS:
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// INJECTION:
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// METHODS:
	// /////////////////////////////////////////////////////////////////////////

	public static void main( String args[] ) {

		// ---------------------------------------------------------------------
		// CLI:
		// ---------------------------------------------------------------------

		// See "http://www.refcodes.org/refcodes/refcodes-cli" |-->

		Option<String> theInputPathArg = stringOption( 'i', "input-file", INPUTFILE_PROPERTY, "The input file from which to read the values. If omitted, then <STDIN> is used." );
		Option<String> theOutputPathArg = stringOption( 'o', "output-file", OUTPUTFILE_PROPERTY, "The output file to which to write the values. If omitted, then <STDOUT> is used." );
		Option<Integer> theTimeoutSecsArg = intOption( 't', "timeout", TIMEOUT_PROPERTY, "Specifies the timeout (seconds) to wait for input, a value of -1 disables the dedicated timeout." );
		Flag theUpperFlag = flag( 'u', "upper-case", UPPER_CASE_PROPERTY, "Convert the data from <STDIN> to upper case." );
		Flag theLowerFlag = flag( 'l', "lower-case", LOWER_CASE_PROPERTY, "Convert the data <STDIN> in to lower case." );
		Flag theVerboseFlag = verboseFlag();
		Flag theSysInfoFlag = sysInfoFlag();
		Flag theHelpFlag = helpFlag();
		Flag theDebugFlag = debugFlag();

		// @formatter:off
		Term theArgsSyntax =  cases(
			and( xor(theUpperFlag, theLowerFlag), optional( theInputPathArg, theOutputPathArg, theTimeoutSecsArg, theVerboseFlag, theDebugFlag ) ),
			xor( theHelpFlag, and( theSysInfoFlag, any ( theVerboseFlag ) ) )
		);
		Example[] theExamples = examples(
			example( "Writes text from <STDIN> in upper case to <STDOUT>.", theUpperFlag),
			example( "Writes text from <STDIN> in upper case to destination file." , theUpperFlag, theOutputPathArg ),
			example( "Writes text from <STDIN> in upper case to destination file using a timeout." , theUpperFlag, theOutputPathArg, theTimeoutSecsArg ),
			example( "Writes text from source file in lower case to <STDOUT>." , theLowerFlag, theInputPathArg ),
			example( "Writes text from source file in lower case to destination file." , theLowerFlag, theInputPathArg, theOutputPathArg ),
			example( "To show the help text", theHelpFlag ),
			example( "To print the system info", theSysInfoFlag )
		);
		CliHelper theCliHelper = CliHelper.builder().
			withArgs( args ).
			// withArgs( args, ArgsFilter.D_XX ).
			withArgsSyntax( theArgsSyntax ).
			withExamples( theExamples ).
			withResourceClass( Main.class ).
			withName( NAME ).
			withTitle( TITLE ).
			withDescription( DESCRIPTION ).
			withLicense( LICENSE_NOTE ).
			withCopyright( COPYRIGHT ).
			withBannerFont( BANNER_FONT ).
			withBannerFontPalette( BANNER_PALETTE ).
			withLogger( LOGGER ).build();
		// @formatter:on

		ApplicationProperties theArgsProperties = theCliHelper.getApplicationProperties();

		// <--| See "http://www.refcodes.org/refcodes/refcodes-cli"

		// ---------------------------------------------------------------------
		// MAIN:
		// ---------------------------------------------------------------------

		boolean isVerbose = theCliHelper.isVerbose();
		boolean isDebug = theArgsProperties.getBoolean( theDebugFlag );

		if ( isVerbose ) {
			LOGGER.info( "Starting application <" + NAME + "> ..." );
		}

		if ( isDebug ) {
			LOGGER.info( "Additional debug output enabled ..." );
		}

		// ---------------------------------------------------------------------
		// CSV:
		// ---------------------------------------------------------------------

		try {
			Case theCase = theLowerFlag.isEnabled() ? Case.LOWER : Case.UPPER;
			File theInputFile = (theInputPathArg.hasValue()) ? new File( theArgsProperties.get( INPUTFILE_PROPERTY ) ) : null;
			File theOutputFile = (theOutputPathArg.hasValue()) ? new File( theArgsProperties.get( OUTPUTFILE_PROPERTY ) ) : null;
			int theTimeoutSecs = theTimeoutSecsArg.getValueOr( -1 );
			int theTimeoutMillis = theTimeoutSecs != -1 ? theTimeoutSecs * 1000 : -1;
			if ( isVerbose ) {
				LOGGER.printSeparator();
				LOGGER.info( "Input file = <" + (theInputFile != null ? theInputFile : "STDIN") + ">" );
				LOGGER.info( "Output file = <" + (theOutputFile != null ? theOutputFile : "STDOUT") + ">" );
				LOGGER.info( "Case conversion = <" + theCase + ">" );
				LOGGER.info( "Timeout (seconds) = <" + (theTimeoutSecs == -1 ? "none" : theTimeoutSecs) + ">" );
			}
			int eSize;
			char[] eChars = new char[BUFFER_SIZE];
			try (InputStreamReader theInputReader = new InputStreamReader( theInputFile != null ? new TimeoutInputStream( new FileInputStream( theInputFile ), theTimeoutMillis ) : new BufferedInputStream( new TimeoutInputStream( Execution.toBootstrapStandardIn(), theTimeoutMillis ) ) ); BufferedWriter theOutputWriter = new BufferedWriter( new OutputStreamWriter( (theOutputFile != null ? new FileOutputStream( theOutputFile ) : new BufferedOutputStream( Execution.toBootstrapStandardOut() )) ) );) {
				if ( (eSize = theInputReader.read( eChars )) != -1 ) {
					if ( theOutputFile == null && isVerbose ) LOGGER.printTail(); // STDOUT will follow
					do {
						for ( int i = 0; i < eSize; i++ ) {
							theOutputWriter.write( theCase == Case.LOWER ? Character.toLowerCase( eChars[i] ) : Character.toUpperCase( eChars[i] ) );
						}
					} while ( (eSize = theInputReader.read( eChars )) != -1 );
				}
				theOutputWriter.flush();
			}
			if ( isVerbose && theOutputFile != null ) {
				LOGGER.printSeparator();
				LOGGER.info( "Successful converted <" + theOutputFile.getName() + "> to <" + theCase.name() + "> case!" );
			}
		}
		catch ( Throwable e ) {
			theCliHelper.exitOnException( e );
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// HOOKS:
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// HELPER:
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// INNER CLASSES:
	// /////////////////////////////////////////////////////////////////////////

}
