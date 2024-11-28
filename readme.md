# ⚡︎ BoxLang Module: Boxlang UI Forms Module

## Components

This module contributes the following Components to the language:

* `form` - the wrapping HTML `<form...></form>` component other HTML form tag components. Produces an HTML form.
* `input` - the `<input.../>` HTML form component
* `select` - the `<select ... ></select>` HTML form component
* `slider` - generates an [HTML 5 input range slider](https://www.w3schools.com/tags/att_input_type_range.asp)
* `textarea` - the `<textarea>...</textarea>` HTML form component

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
