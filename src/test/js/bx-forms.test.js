import { beforeAll, beforeEach, describe, expect, it, vi } from "vitest";
import { loadCompatScript, loadScript } from "./setup.js";

describe("AJAX form binds", () => {
	beforeAll(() => {
		loadCompatScript("ajax-core.js");
		loadCompatScript("ajaxproxy.js");
	});

	it("scans CF selects, registers source changes, and provides the ColdFusion handler cache", () => {
		document.body.innerHTML = `<form name="catalogForm" id="catalogForm">
			<input name="category" value="books">
			<select name="products" bind="cfc:CatalogService.getProducts({category})" bindonload="true"></select>
		</form>`;
		const source = document.querySelector("input");
		const select = document.querySelector("select");
		const listener = vi.spyOn(source, "addEventListener");
		const invoke = vi.spyOn(window.BXUICompat.AjaxProxy, "invoke").mockImplementation((proxy, method, token, args, context) => {
			proxy.callbackHandler({ COLUMNS: ["ID", "NAME"], DATA: [[args.category, "Available"]] }, context);
		});
		try {
			window.BXUICompat.Bind.scan(document);
			expect(select.id).toBe("products");
			expect(listener).toHaveBeenCalledWith("change", expect.any(Function), false);
			expect(window.ColdFusion).toBe(window.BXUICompat);
			expect(window.ColdFusion.bindHandlerCache.products).toBeTypeOf("function");
			expect(invoke).toHaveBeenCalledTimes(1);
			expect(select.options[0].value).toBe("books");
			expect(select.options[0].text).toBe("Available");
			source.value = "games";
			source.dispatchEvent(new Event("change"));
			expect(invoke).toHaveBeenCalledTimes(2);
			expect(invoke.mock.calls[1][3]).toEqual({ category: "games" });
			expect(select.options).toHaveLength(1);
			expect(select.options[0].value).toBe("games");
			expect(() => window.ColdFusion.bindHandlerCache.products.call()).not.toThrow();
			expect(invoke).toHaveBeenCalledTimes(3);
			window.BXUICompat.Bind.scan(document);
			expect(invoke).toHaveBeenCalledTimes(3);
		} finally {
			vi.restoreAllMocks();
			document.body.innerHTML = "";
		}
	});

	it("uses emitted column and selection metadata rather than the first two columns", () => {
		document.body.innerHTML = `<select id="custom" name="custom"></select>`;
		try {
			window.BXUICompat.initSelect("custom", "CODE", "LABEL", ["10"]);
			window.BXUICompat.Bind.assignValue("custom", "value", {
				COLUMNS: ["IGNORED", "CODE", "LABEL"],
				DATA: [["unused", "1", "One"], ["unused", "10", "Ten"]],
			});
			const select = document.querySelector("select");
			expect(Array.from(select.options, (option) => [option.value, option.text])).toEqual([["1", "One"], ["10", "Ten"]]);
			expect(select.value).toBe("10");
		} finally {
			delete window.BXUICompat.objectCache.custom;
			document.body.innerHTML = "";
		}
	});
});

describe("bx-forms.js", () => {
	beforeAll(() => {
		loadScript("bx-forms.js");
	});

	beforeEach(() => {
		document.body.innerHTML = "";
	});

	function createInput(attrs = {}) {
		const input = document.createElement("input");
		Object.entries(attrs).forEach(([key, value]) => {
			input.setAttribute(key, value);
		});
		document.body.appendChild(input);
		return input;
	}

	it("validates date values", () => {
		const input = createInput({ "data-validate": "date", "data-dateformat": "mm/dd/yyyy" });
		input.value = "02/29/2024";
		expect(window.BXForms.validateInput(input)).toBe(true);

		input.value = "02/30/2024";
		expect(window.BXForms.validateInput(input)).toBe(false);
		expect(input.getAttribute("aria-invalid")).toBe("true");
	});

	it("validates time values", () => {
		const input = createInput({ "data-validate": "time" });
		input.value = "14:30:00";
		expect(window.BXForms.validateInput(input)).toBe(true);

		input.value = "12:30 PM";
		expect(window.BXForms.validateInput(input)).toBe(false);

		input.value = "25:61:00";
		expect(window.BXForms.validateInput(input)).toBe(false);
	});

	it("validates float values", () => {
		const input = createInput({ "data-validate": "float" });
		input.value = "10.25";
		expect(window.BXForms.validateInput(input)).toBe(true);

		input.value = "not-a-number";
		expect(window.BXForms.validateInput(input)).toBe(false);
	});

	it("validates integer values", () => {
		const input = createInput({ "data-validate": "integer" });
		input.value = "42";
		expect(window.BXForms.validateInput(input)).toBe(true);

		input.value = "42.5";
		expect(window.BXForms.validateInput(input)).toBe(false);
	});

	it("validates telephone values", () => {
		const input = createInput({ "data-validate": "telephone" });
		input.value = "555-555-5555";
		expect(window.BXForms.validateInput(input)).toBe(true);

		// Area code starts with 0 (invalid)
		input.value = "055-555-5555";
		expect(window.BXForms.validateInput(input)).toBe(false);

		// Blank separator allowed
		input.value = "555 555 5555";
		expect(window.BXForms.validateInput(input)).toBe(true);
	});

	it("validates zipcode values", () => {
		const input = createInput({ "data-validate": "zipcode" });
		input.value = "12345-6789";
		expect(window.BXForms.validateInput(input)).toBe(true);

		// 5-digit only
		input.value = "12345";
		expect(window.BXForms.validateInput(input)).toBe(true);

		// Blank separator allowed
		input.value = "12345 6789";
		expect(window.BXForms.validateInput(input)).toBe(true);

		input.value = "1234";
		expect(window.BXForms.validateInput(input)).toBe(false);
	});

	it("validates credit card values", () => {
		const input = createInput({ "data-validate": "creditcard" });
		input.value = "4111111111111111";
		expect(window.BXForms.validateInput(input)).toBe(true);

		input.value = "4111111111111112";
		expect(window.BXForms.validateInput(input)).toBe(false);
	});

	it("validates social security number values", () => {
		const input = createInput({ "data-validate": "social_security_number" });
		input.value = "123-45-6789";
		expect(window.BXForms.validateInput(input)).toBe(true);

		// Blank separator allowed
		input.value = "123 45 6789";
		expect(window.BXForms.validateInput(input)).toBe(true);

		input.value = "123456789";
		expect(window.BXForms.validateInput(input)).toBe(false);
	});

	it("validates regular expression values", () => {
		const input = createInput({ "data-validate": "regular_expression", pattern: "[A-Z]{3}-[0-9]{4}" });
		input.value = "ABC-1234";
		expect(window.BXForms.validateInput(input)).toBe(true);

		input.value = "bad-value";
		expect(window.BXForms.validateInput(input)).toBe(false);
	});

	it("validates all inputs in a form", () => {
		const form = document.createElement("form");
		const dateInput = document.createElement("input");
		dateInput.setAttribute("data-validate", "date");
		dateInput.setAttribute("data-dateformat", "mm/dd/yyyy");
		dateInput.value = "01/15/2024";

		const integerInput = document.createElement("input");
		integerInput.setAttribute("data-validate", "integer");
		integerInput.value = "7";

		form.appendChild(dateInput);
		form.appendChild(integerInput);
		document.body.appendChild(form);

		expect(window.BXForms.validateForm(form)).toBe(true);

		integerInput.value = "7.5";
		expect(window.BXForms.validateForm(form)).toBe(false);
	});

	it("shows and clears error markup", () => {
		const input = createInput({ "data-validate": "integer" });
		input.value = "abc";
		expect(window.BXForms.validateInput(input)).toBe(false);
		expect(input.classList.contains("bx-forms-error")).toBe(true);
		expect(input.nextElementSibling).not.toBeNull();

		input.value = "123";
		expect(window.BXForms.validateInput(input)).toBe(true);
		expect(input.classList.contains("bx-forms-error")).toBe(false);
		expect(input.getAttribute("aria-invalid")).toBe("false");
	});
});
