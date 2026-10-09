package ortus.boxlang.modules.forms.components;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.compiler.parser.BoxSourceType;
import ortus.boxlang.runtime.BoxRuntime;
import ortus.boxlang.runtime.modules.ModuleRecord;
import ortus.boxlang.runtime.scopes.Key;

public class FormComponentTest extends ortus.boxlang.modules.forms.BaseIntegrationTest {

	static BoxRuntime	instance;
	static Key			result	= Key.of( "result" );

	/**
	 * Verifies CF form defaults and passthrough submission attributes.
	 */
	@DisplayName( "It defaults form id and submission method" )
	@Test
	public void testFormDefaults() {
		runtime.executeSource( """
		                       <cfform name="form1" action="index.cfm" onsubmit="return checkForm()">
		                           <cfinput name="other" />
		                       </cfform>
		                       <cfset result = getBoxContext().getBuffer().toString()>
		                       """, context, BoxSourceType.CFTEMPLATE );
		String output = variables.getAsString( result );
		assertTrue( output.contains( "id=\"form1\"" ) );
		assertTrue( output.contains( "action=\"index.cfm\"" ) );
		assertTrue( output.contains( "method=\"post\"" ) );
		assertTrue( output.contains( "onsubmit=" ) );
		assertTrue( !output.contains( "ajax-core.js" ) );
	}

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

	@DisplayName( "It can test the Input component with a typeahead options as a variable binding" )
	@Test
	public void testInpjtComponentTypeaheadVariable() {
		// @formatter:off
		runtime.executeSource( """
		<cfscript>
		countries = ["Afghanistan","Albania","Algeria","Andorra","Angola","Anguilla","Antigua &amp; Barbuda","Argentina","Armenia","Aruba","Australia","Austria","Azerbaijan","Bahamas","Bahrain","Bangladesh","Barbados","Belarus","Belgium","Belize","Benin","Bermuda","Bhutan","Bolivia","Bosnia &amp; Herzegovina","Botswana","Brazil","British Virgin Islands","Brunei","Bulgaria","Burkina Faso","Burundi","Cambodia","Cameroon","Canada","Cape Verde","Cayman Islands","Central Arfrican Republic","Chad","Chile","China","Colombia","Congo","Cook Islands","Costa Rica","Cote D Ivoire","Croatia","Cuba","Curacao","Cyprus","Czech Republic","Denmark","Djibouti","Dominica","Dominican Republic","Ecuador","Egypt","El Salvador","Equatorial Guinea","Eritrea","Estonia","Ethiopia","Falkland Islands","Faroe Islands","Fiji","Finland","France","French Polynesia","French West Indies","Gabon","Gambia","Georgia","Germany","Ghana","Gibraltar","Greece","Greenland","Grenada","Guam","Guatemala","Guernsey","Guinea","Guinea Bissau","Guyana","Haiti","Honduras","Hong Kong","Hungary","Iceland","India","Indonesia","Iran","Iraq","Ireland","Isle of Man","Israel","Italy","Jamaica","Japan","Jersey","Jordan","Kazakhstan","Kenya","Kiribati","Kosovo","Kuwait","Kyrgyzstan","Laos","Latvia","Lebanon","Lesotho","Liberia","Libya","Liechtenstein","Lithuania","Luxembourg","Macau","Macedonia","Madagascar","Malawi","Malaysia","Maldives","Mali","Malta","Marshall Islands","Mauritania","Mauritius","Mexico","Micronesia","Moldova","Monaco","Mongolia","Montenegro","Montserrat","Morocco","Mozambique","Myanmar","Namibia","Nauro","Nepal","Netherlands","Netherlands Antilles","New Caledonia","New Zealand","Nicaragua","Niger","Nigeria","North Korea","Norway","Oman","Pakistan","Palau","Palestine","Panama","Papua New Guinea","Paraguay","Peru","Philippines","Poland","Portugal","Puerto Rico","Qatar","Reunion","Romania","Russia","Rwanda","Saint Pierre &amp; Miquelon","Samoa","San Marino","Sao Tome and Principe","Saudi Arabia","Senegal","Serbia","Seychelles","Sierra Leone","Singapore","Slovakia","Slovenia","Solomon Islands","Somalia","South Africa","South Korea","South Sudan","Spain","Sri Lanka","St Kitts &amp; Nevis","St Lucia","St Vincent","Sudan","Suriname","Swaziland","Sweden","Switzerland","Syria","Taiwan","Tajikistan","Tanzania","Thailand","Timor L'Este","Togo","Tonga","Trinidad &amp; Tobago","Tunisia","Turkey","Turkmenistan","Turks &amp; Caicos","Tuvalu","Uganda","Ukraine","United Arab Emirates","United Kingdom","United States of America","Uruguay","Uzbekistan","Vanuatu","Vatican City","Venezuela","Vietnam","Virgin Islands (US)","Yemen","Zambia","Zimbabwe"];
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
		assertTrue( output.contains( "autosuggest=" ) );
		assertTrue( output.contains( "autosuggestminlength=\"3\"" ) );
	}

	/**
	 * Loads the real compatibility module from the test installation or source checkout.
	 * BX_UI_COMPAT_DIR can override the module directory for other development layouts.
	 */
	private void loadAjaxRuntime() {
		if ( runtime.getComponentService().hasComponent( "AjaxImport" ) ) {
			return;
		}
		String	override	= System.getenv( "BX_UI_COMPAT_DIR" );
		Path	modulePath	= override == null ? Path.of( "src/test/resources/modules/bx-ui-compat" ) : Path.of( override );
		if ( override == null && !Files.exists( modulePath.resolve( "ModuleConfig.bx" ) ) ) {
			modulePath = Path.of( "../bx-ui-compat/src/main/bx" );
		}
		assertTrue( Files.exists( modulePath.resolve( "ModuleConfig.bx" ) ), "Install bx-ui-compat or set BX_UI_COMPAT_DIR" );
		ModuleRecord compatModule = new ModuleRecord( modulePath.toAbsolutePath().toString() );
		moduleService.getRegistry().put( Key.of( "bx-ui-compat" ), compatModule );
		compatModule.loadDescriptor( context ).register( context ).activate( context );
	}

	/**
	 * Verifies CF select bind attributes and runtime imports preceding the form markup.
	 */
	@Test
	public void testSelectAjaxBind() {
		loadAjaxRuntime();
		runtime.executeSource( """
		                       <cfform name="form1">
		                           <cfinput name="other" />
		                           <cfselect name="x" bind="cfc:svc.getData({other})" bindOnLoad="true"></cfselect>
		                       </cfform>
		                       <cfset result = getBoxContext().getBuffer().toString()>
		                       """, context, BoxSourceType.CFTEMPLATE );
		String output = variables.getAsString( result );
		assertTrue( output.contains( "id=\"x\"" ) );
		assertTrue( output.contains( "bind=" ) );
		assertTrue( output.contains( "bindonload=\"true\"" ) );
		assertTrue( output.contains( "ajaxproxy.js" ) );
		assertTrue( output.contains( "ajax-core.css" ) );
		assertTrue( output.indexOf( "ajax-core.js" ) >= 0 && output.indexOf( "ajax-core.js" ) < output.indexOf( "<form" ) );
		assertTrue( !output.contains( "ColdFusion.Bind.register" ) );
	}

	/**
	 * Verifies explicit select metadata and supported input bind callbacks.
	 */
	@Test
	public void testBindMetadataAndCallbacks() {
		loadAjaxRuntime();
		runtime.executeSource( """
		                       <cfform name="form1">
		                           <cfinput name="other" bind="url:data.cfm" bindAttribute="value" bindOnLoad="true"
		                               onBindError="bindError" onBindSuccess="bindSuccess" />
		                           <cfselect name="x" id="custom" bind="cfc:svc.getData({other})"
		                               value="code" display="label" selected="MI"></cfselect>
		                       </cfform>
		                       <cfset result = getBoxContext().getBuffer().toString()>
		                       """, context, BoxSourceType.CFTEMPLATE );
		String output = variables.getAsString( result );
		assertTrue( output.contains( "BXUICompat.initSelect(\"custom\",\"code\",\"label\",[\"MI\"])" ) );
		assertTrue( output.contains( "bindattribute=\"value\"" ) );
		assertTrue( output.contains( "bindonload=\"true\"" ) );
		assertTrue( output.contains( "onbinderror=\"bindError\"" ) );
		assertTrue( output.contains( "onbindsuccess=\"bindSuccess\"" ) );
	}

	/**
	 * Verifies metadata inferred from a server query preserves the tag's option columns.
	 */
	@Test
	public void testSelectBoundQueryColumns() {
		runtime.executeSource( """
		                       <cfset options = queryNew("code,label", "varchar,varchar", [{code="MI",label="Michigan"}])>
		                       <cfform name="form1">
		                           <cfselect name="states" query="options" bind="cfc:svc.getData()" selected="MI"></cfselect>
		                       </cfform>
		                       <cfset result = getBoxContext().getBuffer().toString()>
		                       """, context, BoxSourceType.CFTEMPLATE );
		String output = variables.getAsString( result );
		assertTrue( output.contains( "BXUICompat.initSelect(\"states\",\"code\",\"code\",[\"MI\"])" ) );
	}

	/**
	 * Verifies bound forms remain renderable when the optional AjaxImport component is absent.
	 */
	@Test
	public void testBindWithoutCompat() {
		var	componentService	= runtime.getComponentService();
		var	ajaxImport			= componentService.getComponent( "AjaxImport" );
		componentService.unregisterComponent( Key.of( "AjaxImport" ) );
		try {
			runtime.executeSource( """
			                       <cfform name="form1" id="explicit" action="index.cfm" method="get">
			                           <cfselect name="x" bind="cfc:svc.getData()" bindonload="true"></cfselect>
			                       </cfform>
			                       <cfset result = getBoxContext().getBuffer().toString()>
			                       """, context, BoxSourceType.CFTEMPLATE );
			String output = variables.getAsString( result );
			assertTrue( output.contains( "id=\"explicit\"" ) );
			assertTrue( output.contains( "method=\"get\"" ) );
			assertTrue( output.contains( "id=\"x\"" ) );
			assertTrue( !output.contains( "ajax-core.js" ) );
		} finally {
			if ( ajaxImport != null ) {
				componentService.registerComponent( ajaxImport, true );
			}
		}
	}

	@DisplayName( "It can test the Input component with date validation using default format" )
	@Test
	public void testInputComponentDateValidationDefault() {
		// @formatter:off
		runtime.executeSource( """
		<bx:form action="index.cfm" method="post">
			<bx:input 
				type="text" 
				name="transDate" 
				id="transDate" 
				validate="date" 
				message="Invalid Date mm/dd/yyyy." 
			/>
		</bx:form>
		<bx:set result = getBoxContext().getBuffer().toString()>
		""",
		context,
		BoxSourceType.BOXTEMPLATE
		);
		// @formatter:on
		String output = variables.getAsString( result );
		assertTrue( output.contains( "data-validate=\"date\"" ), "Should contain data-validate attribute" );
		assertTrue( output.contains( "data-dateformat=\"mm/dd/yyyy\"" ), "Should contain default dateformat" );
		assertTrue( output.contains( "data-validation-message=" ), "Should contain validation message attribute" );
	}

	@DisplayName( "It can test the Input component with date validation using custom format" )
	@Test
	public void testInputComponentDateValidationCustomFormat() {
		// @formatter:off
		runtime.executeSource( """
		<bx:form action="index.cfm" method="post">
			<bx:input 
				type="text" 
				name="transDate" 
				id="transDate" 
				validate="date" 
				dateformat="yyyy-mm-dd"
				message="Invalid Date yyyy-mm-dd." 
			/>
		</bx:form>
		<bx:set result = getBoxContext().getBuffer().toString()>
		""",
		context,
		BoxSourceType.BOXTEMPLATE
		);
		// @formatter:on
		String output = variables.getAsString( result );
		assertTrue( output.contains( "data-validate=\"date\"" ), "Should contain data-validate attribute" );
		assertTrue( output.contains( "data-dateformat=\"yyyy-mm-dd\"" ), "Should contain custom dateformat" );
		assertTrue( output.contains( "data-validation-message=" ), "Should contain validation message attribute" );
	}

	@DisplayName( "It can test the Input component with date validation with label" )
	@Test
	public void testInputComponentDateValidationWithLabel() {
		// @formatter:off
		runtime.executeSource( """
		<bx:form action="index.cfm" method="post">
			<bx:input 
				type="text" 
				name="transDate" 
				id="transDate" 
				label="Transaction Date"
				validate="date" 
				message="Invalid Date mm/dd/yyyy." 
				required="true"
			/>
		</bx:form>
		<bx:set result = getBoxContext().getBuffer().toString()>
		""",
		context,
		BoxSourceType.BOXTEMPLATE
		);
		// @formatter:on
		String output = variables.getAsString( result );
		assertTrue( output.contains( "<label for=\"transDate\">Transaction Date</label>" ), "Should contain label element" );
		assertTrue( output.contains( "data-validate=\"date\"" ), "Should contain data-validate attribute" );
		assertTrue( output.contains( "data-dateformat=\"mm/dd/yyyy\"" ), "Should contain default dateformat" );
		assertTrue( output.contains( "required" ), "Should contain required attribute" );
	}

	@DisplayName( "It can test the Input component with time validation" )
	@Test
	public void testInputComponentTimeValidation() {
		// @formatter:off
		runtime.executeSource( """
		<bx:form action="index.cfm" method="post">
			<bx:input type="text" name="apptTime" validate="time" message="Invalid time format." />
		</bx:form>
		<bx:set result = getBoxContext().getBuffer().toString()>
		""",
		context,
		BoxSourceType.BOXTEMPLATE
		);
		// @formatter:on
		String output = variables.getAsString( result );
		assertTrue( output.contains( "data-validate=\"time\"" ), "Should contain data-validate=time" );
		assertTrue( output.contains( "data-validation-message=" ), "Should contain validation message attribute" );
	}

	@DisplayName( "It can test the Input component with float validation" )
	@Test
	public void testInputComponentFloatValidation() {
		// @formatter:off
		runtime.executeSource( """
		<bx:form action="index.cfm" method="post">
			<bx:input type="text" name="price" validate="float" message="Please enter a valid number." />
		</bx:form>
		<bx:set result = getBoxContext().getBuffer().toString()>
		""",
		context,
		BoxSourceType.BOXTEMPLATE
		);
		// @formatter:on
		String output = variables.getAsString( result );
		assertTrue( output.contains( "data-validate=\"float\"" ), "Should contain data-validate=float" );
		assertTrue( output.contains( "data-validation-message=" ), "Should contain validation message attribute" );
	}

	@DisplayName( "It can test the Input component with integer validation" )
	@Test
	public void testInputComponentIntegerValidation() {
		// @formatter:off
		runtime.executeSource( """
		<bx:form action="index.cfm" method="post">
			<bx:input type="text" name="qty" validate="integer" message="Please enter a whole number." />
		</bx:form>
		<bx:set result = getBoxContext().getBuffer().toString()>
		""",
		context,
		BoxSourceType.BOXTEMPLATE
		);
		// @formatter:on
		String output = variables.getAsString( result );
		assertTrue( output.contains( "data-validate=\"integer\"" ), "Should contain data-validate=integer" );
		assertTrue( output.contains( "data-validation-message=" ), "Should contain validation message attribute" );
	}

	@DisplayName( "It can test the Input component with telephone validation" )
	@Test
	public void testInputComponentTelephoneValidation() {
		// @formatter:off
		runtime.executeSource( """
		<bx:form action="index.cfm" method="post">
			<bx:input type="text" name="phone" validate="telephone" message="Invalid phone number." />
		</bx:form>
		<bx:set result = getBoxContext().getBuffer().toString()>
		""",
		context,
		BoxSourceType.BOXTEMPLATE
		);
		// @formatter:on
		String output = variables.getAsString( result );
		assertTrue( output.contains( "data-validate=\"telephone\"" ), "Should contain data-validate=telephone" );
		assertTrue( output.contains( "data-validation-message=" ), "Should contain validation message attribute" );
	}

	@DisplayName( "It can test the Input component with zipcode validation" )
	@Test
	public void testInputComponentZipcodeValidation() {
		// @formatter:off
		runtime.executeSource( """
		<bx:form action="index.cfm" method="post">
			<bx:input type="text" name="zip" validate="zipcode" message="Invalid zip code." />
		</bx:form>
		<bx:set result = getBoxContext().getBuffer().toString()>
		""",
		context,
		BoxSourceType.BOXTEMPLATE
		);
		// @formatter:on
		String output = variables.getAsString( result );
		assertTrue( output.contains( "data-validate=\"zipcode\"" ), "Should contain data-validate=zipcode" );
		assertTrue( output.contains( "data-validation-message=" ), "Should contain validation message attribute" );
	}

	@DisplayName( "It can test the Input component with creditcard validation" )
	@Test
	public void testInputComponentCreditCardValidation() {
		// @formatter:off
		runtime.executeSource( """
		<bx:form action="index.cfm" method="post">
			<bx:input type="text" name="ccNum" validate="creditcard" message="Invalid credit card number." />
		</bx:form>
		<bx:set result = getBoxContext().getBuffer().toString()>
		""",
		context,
		BoxSourceType.BOXTEMPLATE
		);
		// @formatter:on
		String output = variables.getAsString( result );
		assertTrue( output.contains( "data-validate=\"creditcard\"" ), "Should contain data-validate=creditcard" );
		assertTrue( output.contains( "data-validation-message=" ), "Should contain validation message attribute" );
	}

	@DisplayName( "It can test the Input component with social_security_number validation" )
	@Test
	public void testInputComponentSSNValidation() {
		// @formatter:off
		runtime.executeSource( """
		<bx:form action="index.cfm" method="post">
			<bx:input type="text" name="ssn" validate="social_security_number" message="Invalid SSN." />
		</bx:form>
		<bx:set result = getBoxContext().getBuffer().toString()>
		""",
		context,
		BoxSourceType.BOXTEMPLATE
		);
		// @formatter:on
		String output = variables.getAsString( result );
		assertTrue( output.contains( "data-validate=\"social_security_number\"" ), "Should contain data-validate=social_security_number" );
		assertTrue( output.contains( "data-validation-message=" ), "Should contain validation message attribute" );
	}

	@DisplayName( "It can test the Input component with regular_expression validation" )
	@Test
	public void testInputComponentRegexValidation() {
		// @formatter:off
		runtime.executeSource( """
		<bx:form action="index.cfm" method="post">
			<bx:input type="text" name="code" validate="regular_expression" pattern="[A-Z]{3}-[0-9]{4}" message="Invalid format." />
		</bx:form>
		<bx:set result = getBoxContext().getBuffer().toString()>
		""",
		context,
		BoxSourceType.BOXTEMPLATE
		);
		// @formatter:on
		String output = variables.getAsString( result );
		assertTrue( output.contains( "data-validate=\"regular_expression\"" ), "Should contain data-validate=regular_expression" );
		assertTrue( output.contains( "pattern=" ), "Should contain native pattern attribute" );
		assertTrue( output.contains( "data-validation-message=" ), "Should contain validation message attribute" );
	}

}
