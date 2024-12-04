package ortus.boxlang.modules.forms.components;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
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
			<bx:input type="tel" name="phone" />
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
		assertTrue( output.contains( "type=\"tel\" name=\"phone\"" ) );
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
		assertTrue( output.contains( "<optgroup label=\"Canada\">" ) );
		assertTrue( output.contains( "<option value=\"ON\" >Ontario</option>" ) );
		assertTrue( output.contains( "<option value=\"MI\" selected>Michigan</option>" ) );
	}

	// TODO: Enable when we add ajax support
	@DisplayName( "It can test the Input component with a typeahead options as a variable binding" )
	@Test
	@Disabled
	public void testInpjtComponentTypeaheadVariable() {
		// @formatter:off
		runtime.executeSource( """
		<cfscript>
		var countries = ["Afghanistan","Albania","Algeria","Andorra","Angola","Anguilla","Antigua &amp; Barbuda","Argentina","Armenia","Aruba","Australia","Austria","Azerbaijan","Bahamas","Bahrain","Bangladesh","Barbados","Belarus","Belgium","Belize","Benin","Bermuda","Bhutan","Bolivia","Bosnia &amp; Herzegovina","Botswana","Brazil","British Virgin Islands","Brunei","Bulgaria","Burkina Faso","Burundi","Cambodia","Cameroon","Canada","Cape Verde","Cayman Islands","Central Arfrican Republic","Chad","Chile","China","Colombia","Congo","Cook Islands","Costa Rica","Cote D Ivoire","Croatia","Cuba","Curacao","Cyprus","Czech Republic","Denmark","Djibouti","Dominica","Dominican Republic","Ecuador","Egypt","El Salvador","Equatorial Guinea","Eritrea","Estonia","Ethiopia","Falkland Islands","Faroe Islands","Fiji","Finland","France","French Polynesia","French West Indies","Gabon","Gambia","Georgia","Germany","Ghana","Gibraltar","Greece","Greenland","Grenada","Guam","Guatemala","Guernsey","Guinea","Guinea Bissau","Guyana","Haiti","Honduras","Hong Kong","Hungary","Iceland","India","Indonesia","Iran","Iraq","Ireland","Isle of Man","Israel","Italy","Jamaica","Japan","Jersey","Jordan","Kazakhstan","Kenya","Kiribati","Kosovo","Kuwait","Kyrgyzstan","Laos","Latvia","Lebanon","Lesotho","Liberia","Libya","Liechtenstein","Lithuania","Luxembourg","Macau","Macedonia","Madagascar","Malawi","Malaysia","Maldives","Mali","Malta","Marshall Islands","Mauritania","Mauritius","Mexico","Micronesia","Moldova","Monaco","Mongolia","Montenegro","Montserrat","Morocco","Mozambique","Myanmar","Namibia","Nauro","Nepal","Netherlands","Netherlands Antilles","New Caledonia","New Zealand","Nicaragua","Niger","Nigeria","North Korea","Norway","Oman","Pakistan","Palau","Palestine","Panama","Papua New Guinea","Paraguay","Peru","Philippines","Poland","Portugal","Puerto Rico","Qatar","Reunion","Romania","Russia","Rwanda","Saint Pierre &amp; Miquelon","Samoa","San Marino","Sao Tome and Principe","Saudi Arabia","Senegal","Serbia","Seychelles","Sierra Leone","Singapore","Slovakia","Slovenia","Solomon Islands","Somalia","South Africa","South Korea","South Sudan","Spain","Sri Lanka","St Kitts &amp; Nevis","St Lucia","St Vincent","Sudan","Suriname","Swaziland","Sweden","Switzerland","Syria","Taiwan","Tajikistan","Tanzania","Thailand","Timor L'Este","Togo","Tonga","Trinidad &amp; Tobago","Tunisia","Turkey","Turkmenistan","Turks &amp; Caicos","Tuvalu","Uganda","Ukraine","United Arab Emirates","United Kingdom","United States of America","Uruguay","Uzbekistan","Vanuatu","Vatican City","Venezuela","Vietnam","Virgin Islands (US)","Yemen","Zambia","Zimbabwe"];
		</cfscript>
		<cfform action="index.cfm" method="post">
			<cfinput name="typeahead" type="text" autosuggest="#countries#" autosuggestMinLength="3" />
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
		assertTrue( output.contains( "<optgroup label=\"Canada\">" ) );
		assertTrue( output.contains( "<option value=\"ON\" >Ontario</option>" ) );
		assertTrue( output.contains( "<option value=\"MI\" selected>Michigan</option>" ) );
	}

}
