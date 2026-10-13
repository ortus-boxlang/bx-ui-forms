/**
 * [BoxLang]
 *
 * Copyright [2023] [Ortus Solutions, Corp]
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package ortus.boxlang.modules.forms;

import ortus.boxlang.runtime.components.BoxComponent;
import ortus.boxlang.runtime.components.Component;
import ortus.boxlang.runtime.context.IBoxContext;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.types.IStruct;

/**
 * Minimal stand-in for bx-ui-compat's AjaxImport component.
 *
 * <p>
 * bx-ui-forms only needs to know that an "AjaxImport" component exists so Form.bx can emit the AJAX
 * runtime before the form markup. The real component and its asset output live in bx-ui-compat and
 * are covered by that module's own tests. This stub lets the forms module assert its invocation and
 * ordering without taking a dependency on the compatibility module.
 */
@BoxComponent( name = "AjaxImport", allowsBody = false, requiresBody = false )
public class StubAjaxImport extends Component {

	/**
	 * The tags attribute received on the most recent invocation.
	 */
	public String	lastTags		= "";

	/**
	 * Number of times this stub has been invoked.
	 */
	public int		invocationCount	= 0;

	/**
	 * Render a marker so tests can verify the runtime is emitted before the form markup.
	 */
	@Override
	public BodyResult _invoke( IBoxContext context, IStruct attributes, ComponentBody body, IStruct executionState ) {
		invocationCount++;
		lastTags = attributes.getAsString( Key.of( "tags" ) );
		context.writeToBuffer( "<script data-stub-ajaximport=\"true\"></script>" );
		return DEFAULT_RETURN;
	}

}
