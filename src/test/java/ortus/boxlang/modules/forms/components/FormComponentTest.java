package ortus.boxlang.modules.forms.components;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.compiler.parser.BoxSourceType;
import ortus.boxlang.runtime.BoxRuntime;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.context.ScriptingRequestBoxContext;
import ortus.boxlang.runtime.scopes.IScope;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.scopes.VariablesScope;

public class FormComponentTest extends ortus.boxlang.modules.forms.BaseIntegrationTest {

	static BoxRuntime	instance;
	static Key			result	= new Key( "result" );


	@DisplayName( "It can test the FormComponent" )
	@Test
	public void testFormComponentInCF() {
		runtime.executeSource( """
		                        <cfform action="index.cfm" method="post">
		                        	<cfinput type="text" name="name" />
		                        	<cfinput type="submit" value="Submit" />
		                                          </cfform>
		                                                           """, context, BoxSourceType.CFTEMPLATE );
		String result = context.getBuffer().toString();
		System.out.println( result );
		assertTrue( result.contains( "<input type=\"text\" name=\"name\">" ) );
	}

}
