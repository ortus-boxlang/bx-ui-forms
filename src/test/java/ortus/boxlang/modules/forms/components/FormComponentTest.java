package ortus.boxlang.modules.forms.components;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.compiler.parser.BoxSourceType;
import ortus.boxlang.runtime.BoxRuntime;
import ortus.boxlang.runtime.scopes.Key;

public class FormComponentTest extends ortus.boxlang.modules.forms.BaseIntegrationTest {

	static BoxRuntime	instance;
	static Key			result	= Key.of( "result" );

	@DisplayName( "It can test the FormComponent with basic nested tags" )
	@Test
	public void testFormComponentInCF() {
		// @formatter:off
		runtime.executeSource( """
		<cfform action="index.cfm" method="post">
			<cfinput type="text" name="name" />
			<cfselect name="select" id="mySelect">
				<option value="1">One</option>
				<option value="2">Two</option>
			</cfselect>
			<cftextarea name="textarea">Blah</cftextarea>
			<cfslider name="slider" min="0" max="10" />
			<cfinput name="submit" type="submit" value="Submit" />
		</cfform>
		<cfset result = getBoxContext().getBuffer().toString()>
		""",
		context,
		BoxSourceType.CFTEMPLATE
		);
		// @formatter:on
		String output = variables.getAsString( result );
		assertTrue( output.contains( "<form" ) );
		assertTrue( output.contains( "</form>" ) );
		assertTrue( output.contains( "action=\"index.cfm\" method=\"post\"" ) );
		assertTrue( output.contains( "type=\"text\" name=\"name\"" ) );
		assertTrue( output.contains( "type=\"text\" name=\"name\"" ) );
		assertTrue( output.contains( "<textarea name=\"textarea\">Blah</textarea>" ) );
	}

	@DisplayName( "It can test the FormComponent with basic inputs in BL components" )
	@Test
	public void testFormComponentInBL() {
		// @formatter:off
		runtime.executeSource( """
		<bx:form action="index.cfm" method="post">
			<bx:input type="text" name="name" />
			<bx:select name="select" id="mySelect">
				<option value="1">One</option>
				<option value="2">Two</option>
			</bx:select>
			<bx:textarea name="textarea">Blah</bx:textarea>
			<bx:slider name="slider" min="0" max="10" />
			<bx:input name="submit" type="submit" value="Submit" />
		</bx:form>
		<bx:set result = getBoxContext().getBuffer().toString()>
		""",
		context,
		BoxSourceType.BOXTEMPLATE
		);
		// @formatter:on
		String output = variables.getAsString( result );
		assertTrue( output.contains( "<form" ) );
		assertTrue( output.contains( "</form>" ) );
		assertTrue( output.contains( "action=\"index.cfm\" method=\"post\"" ) );
		assertTrue( output.contains( "type=\"text\" name=\"name\"" ) );
		assertTrue( output.contains( "type=\"text\" name=\"name\"" ) );
		assertTrue( output.contains( "<textarea name=\"textarea\">Blah</textarea>" ) );
	}

	@DisplayName( "It can test the Select component with a query binding" )
	@Test
	public void testSelectComponentQuery() {
		// @formatter:off
		runtime.executeSource( """
		<cfscript>
		myQuery = queryNew("abbr,name");
		queryAddRow(myQuery, {abbr="MI", name="Michigan"});
		queryAddRow(myQuery, {abbr="AL", name="Alabama"});
		</cfscript>
		<cfform action="index.cfm" method="post">
			<cfselect name="select" query="myQuery" display="name" value="abbr" selected="MI" queryPosition="below">
				<option value="">Select a State</option>
			</cfselect>
			<cfinput name="submit" type="submit" value="Submit" />
		</cfform>
		<cfset result = getBoxContext().getBuffer().toString()>
		""",
		context,
		BoxSourceType.CFTEMPLATE
		);
		// @formatter:on
		String output = variables.getAsString( result );
		assertTrue( output.contains( "<form" ) );
		assertTrue( output.contains( "</form>" ) );
		assertTrue( output.contains( "action=\"index.cfm\" method=\"post\"" ) );
		assertTrue( output.contains( "<option value=\"MI\" selected>Michigan</option>" ) );
	}

	@DisplayName( "It can test the Select component with a grouped binding" )
	@Test
	public void testSelectComponentQueryGroup() {
		// @formatter:off
		runtime.executeSource( """
		<cfscript>
		myQuery = queryNew("abbr,name,country");
		queryAddRow(myQuery, {abbr="MI", name="Michigan", country="United States"});
		queryAddRow(myQuery, {abbr="AL", name="Alabama", country="United States"});
		queryAddRow(myQuery, {abbr="ON", name="Ontario", country="Canada"});
		</cfscript>
		<cfform action="index.cfm" method="post">
			<cfselect name="select" query="myQuery" display="name" value="abbr" selected="MI" queryPosition="below" group="country">
				<option value="">Select a State</option>
			</cfselect>
			<cfinput name="submit" type="submit" value="Submit" />
		</cfform>
		<cfset result = getBoxContext().getBuffer().toString()>
		""",
		context,
		BoxSourceType.CFTEMPLATE
		);
		// @formatter:on
		String output = variables.getAsString( result );
		assertTrue( output.contains( "<form" ) );
		assertTrue( output.contains( "</form>" ) );
		assertTrue( output.contains( "action=\"index.cfm\" method=\"post\"" ) );
		assertTrue( output.contains( "<option value=\"MI\" selected>Michigan</option>" ) );
	}

}
