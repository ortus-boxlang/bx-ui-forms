import { beforeAll, beforeEach, describe, expect, it } from "vitest";
import { loadScript } from "./setup.js";

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
