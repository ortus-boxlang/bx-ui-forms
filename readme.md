# ⚡︎ BoxLang Module: Boxlang UI Forms Module

## Components

This module contributes the following Components to the language:

* `form` - the wrapping HTML `<form...></form>` component other[HTML form tag](https://www.w3schools.com/tags/tag_form.asp) components. Produces an HTML form.
* `input` - the [`<input.../>` HTML form component](https://www.w3schools.com/tags/tag_input.asp)
* `select` - the [`<select ... ></select>` HTML form component](https://www.w3schools.com/tags/tag_select.asp)
* `slider` - generates an [HTML 5 input range slider](https://www.w3schools.com/tags/att_input_type_range.asp)
* `textarea` - the [`<textarea>...</textarea>` HTML form component](https://www.w3schools.com/tags/tag_textarea.asp)

The majority of attributes to these components serve as pass-through attributes to the generated HTML tag.  Exceptions to these, by tag, include:

### `form`

* `format` - this attribute will throw an error if present and is anything other than `HTML`.  XML and Flash forms are not supported.
* `passthrough` - this is a list of passthrough attributes which will be added to the form tag.  
* `scriptsrc` - this defines an external location for the custom forms javascript file, which is sourced into the HTML output
* `preservedata` - When the form action  posts back to the page that contains the form, this attribute determines whether to override the control values with the submitted values.

#### Unsupported `form` Component Attributes

The following attributes are unsupported and generate a warning log message if used.

* `enablecab`
* `skin`
* `preloader`
* `timeout`
* `wmode`
* `accessible`
* `archive`
* `codebase`

### `input`

* `label` - if provided, will generate an HTML `label` tag before the generated `input` tag
* `message` - can be provided as a custom message if validation fails
* `passthrough` - this is a list of passthrough attributes which will be added to the form tag.

#### Planned `input` Component Attributes supported

The following have not yet been implemented in the `input` component but are planned for development in the near-term.

* `autosuggest` - Valid only for `type="text"`. Specifies entry completion suggestions to display as the user types into a text input. The user can select a suggestion to complete the text entry. The valid value can be either of the following:
  * A string consisting of the suggestion values separated by the delimiter specified by the delimiter attribute.
  * A bind expression that gets the suggestion values based on the current input text.
* `autosuggestbinddelay` -  Valid only for `type="text"`. The minimum time between autosuggest bind expression invocations, in seconds. Use this attribute to limit the number of requests that are sent to the server when a user types.
* `autosuggestminlength` - Valid only for `type="text"`. The minimum number of characters required in the text box before invoking a bind expression to return items for suggestion.
* `delimiter` - Optional delimiter which informs the `autosuggest` attribute
* `maxresultsdisplayed` - Optional numeric value which informs the max result displayed by the autosuggest
* `onbinderror` - The name of a JavaScript function to execute if evaluating a bind expression, including an autosuggest bind expression, results in an error. The function must take two attributes: an HTTP status code and a message.
* `showautosuggestloadingicon` - A Boolean value that specifies whether to display an animated icon when loading an autosuggest value for a text input.
* `typeahead` - A Boolean value that specifies whether the autosuggest feature should automatically complete a user's entry with the first result in the suggestion list.

#### Unsupported `input` Component Attributes

* `bind` - this attribute is unsupported and will throw an error if used

### `slider`

The slider component creates a [`type="range"` input](https://www.w3schools.com/tags/att_input_type_range.asp) component but access the following additional attributes:

* `increment` - specifies the `step` value of the slider
* `bgColor` - specifies the background color of the slider body
* `color` - specifes the color of the slider control
* `vertical` - Optional boolean attribute.  When `true`, the slider will have a vertical orientation

### `textarea`

* `passthrough` - this is a list of passthrough attributes which will be added to the form tag.
* `message` - can be provided as a custom message if validation fails
* `passthrough` - this is a list of passthrough attributes which will be added to the form tag.

#### Unsupported `textarea` Component Attributes

The following attributes are not supported and will be ignored if provided:

* `stylesXML`
* `templatesXML`
* `richtext`
* `toolbar`
* `toolbarOnFocus`


```
|:------------------------------------------------------:|
| ⚡︎ B o x L a n g ⚡︎
| Dynamic : Modular : Productive
|:------------------------------------------------------:|
```

<blockquote>
	Copyright Since 2023 by Ortus Solutions, Corp
	<br>
	<a href="https://www.boxlang.io">www.boxlang.io</a> |
	<a href="https://www.ortussolutions.com">www.ortussolutions.com</a>
</blockquote>


## Ortus Sponsors

BoxLang is a professional open-source project and it is completely funded by the [community](https://patreon.com/ortussolutions) and [Ortus Solutions, Corp](https://www.ortussolutions.com).  Ortus Patreons get many benefits like a cfcasts account, a FORGEBOX Pro account and so much more.  If you are interested in becoming a sponsor, please visit our patronage page: [https://patreon.com/ortussolutions](https://patreon.com/ortussolutions)
