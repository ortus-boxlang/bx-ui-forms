/**
 * BoxLang Forms Validation
 * Handles client-side validation for form inputs
 */

(function () {
	'use strict';

	/**
	 * Parse a date string according to a given format
	 * Supports: mm, dd, yyyy, m, d, yy
	 * @param {string} dateString - The date to parse
	 * @param {string} format - The format pattern (e.g., 'mm/dd/yyyy')
	 * @returns {Date|null} - Parsed Date object or null if invalid
	 */
	function parseDate(dateString, format) {
		if (!dateString || !format) return null;

		format = format.toLowerCase().trim();
		dateString = dateString.trim();

		// Tokenize the format so replacements do not mutate regex fragments.
		const tokenRegex = /(yyyy|yy|mm|m|dd|d|[^ymd]+)/g;
		const tokens = format.match(tokenRegex);
		if (!tokens) {
			return null;
		}

		const parts = [];
		const componentOrder = [];

		tokens.forEach((token) => {
			switch (token) {
				case 'yyyy':
					parts.push('(\\d{4})');
					componentOrder.push('year4');
					break;
				case 'yy':
					parts.push('(\\d{2})');
					componentOrder.push('year2');
					break;
				case 'mm':
				case 'm':
					parts.push('(\\d{1,2})');
					componentOrder.push('month');
					break;
				case 'dd':
				case 'd':
					parts.push('(\\d{1,2})');
					componentOrder.push('day');
					break;
				default:
					parts.push(token.replace(/[.*+?^${}()|[\\]\\]/g, '\\$&'));
			}
		});

		const dateRegex = new RegExp('^' + parts.join('') + '$');
		const matches = dateString.match(dateRegex);
		if (!matches) return null;

		const components = {
			month: null,
			day: null,
			year: null
		};

		for (let i = 0; i < componentOrder.length; i++) {
			const value = parseInt(matches[i + 1], 10);
			switch (componentOrder[i]) {
				case 'month':
					components.month = value;
					break;
				case 'day':
					components.day = value;
					break;
				case 'year4':
					components.year = value;
					break;
				case 'year2':
					components.year = value > 50 ? 1900 + value : 2000 + value;
					break;
			}
		}

		if (components.month === null || components.day === null || components.year === null) {
			console.warn('Invalid date format pattern:', format);
			return null;
		}

		// Handle 2-digit year
		if (components.year < 100) {
			components.year += (components.year > 50) ? 1900 : 2000;
		}

		// Validate date components
		if (components.month < 1 || components.month > 12) return null;
		if (components.day < 1 || components.day > 31) return null;

		// Create date object
		const date = new Date(components.year, components.month - 1, components.day);

		// Verify the date is valid (e.g., Feb 30 should fail)
		if (date.getFullYear() !== components.year ||
			date.getMonth() !== components.month - 1 ||
			date.getDate() !== components.day) {
			return null;
		}

		return date;
	}

	/**
	 * Validate a single input element
	 * @param {HTMLElement} input - The input element to validate
	 * @returns {boolean} - True if valid, false otherwise
	 */
	function validateInput(input) {
		const validationType = input.getAttribute('data-validate');

		if (!validationType) {
			return true;
		}

		// Get the error message
		const errorMessage = input.getAttribute('data-validation-message');

		switch (validationType) {
			case 'date':
				return validateDate(input, errorMessage);
			case 'time':
				return validateTime(input, errorMessage);
			case 'float':
				return validateFloat(input, errorMessage);
			case 'integer':
				return validateInteger(input, errorMessage);
			case 'telephone':
				return validateTelephone(input, errorMessage);
			case 'zipcode':
				return validateZipcode(input, errorMessage);
			case 'creditcard':
				return validateCreditCard(input, errorMessage);
			case 'social_security_number':
				return validateSSN(input, errorMessage);
			case 'regular_expression':
				return validateRegularExpression(input, errorMessage);
			default:
				return true;
		}
	}

	/**
	 * Validate a time input (hh:mm:ss)
	 * @param {HTMLElement} input
	 * @param {string} errorMessage
	 * @returns {boolean}
	 */
	function validateTime(input, errorMessage) {
		const value = input.value.trim();
		if (!value && !input.hasAttribute('required')) { clearError(input); return true; }
		if (!value) { showError(input, errorMessage || 'This field is required'); return false; }

		// Matches: HH:mm:ss
		const timeRegex = /^([01]\d|2[0-3]):[0-5]\d:[0-5]\d$/;
		if (!timeRegex.test(value)) {
			showError(input, errorMessage || 'Invalid time format (expected hh:mm:ss)');
			return false;
		}
		clearError(input);
		return true;
	}

	/**
	 * Validate a float (decimal number)
	 * @param {HTMLElement} input
	 * @param {string} errorMessage
	 * @returns {boolean}
	 */
	function validateFloat(input, errorMessage) {
		const value = input.value.trim();
		if (!value && !input.hasAttribute('required')) { clearError(input); return true; }
		if (!value) { showError(input, errorMessage || 'This field is required'); return false; }

		if (isNaN(parseFloat(value)) || !isFinite(value)) {
			showError(input, errorMessage || 'Please enter a valid number');
			return false;
		}
		clearError(input);
		return true;
	}

	/**
	 * Validate an integer (whole number)
	 * @param {HTMLElement} input
	 * @param {string} errorMessage
	 * @returns {boolean}
	 */
	function validateInteger(input, errorMessage) {
		const value = input.value.trim();
		if (!value && !input.hasAttribute('required')) { clearError(input); return true; }
		if (!value) { showError(input, errorMessage || 'This field is required'); return false; }

		if (!/^-?\d+$/.test(value)) {
			showError(input, errorMessage || 'Please enter a whole number');
			return false;
		}
		clearError(input);
		return true;
	}

	/**
	 * Validate a US telephone number (###-###-####, separators can be blank)
	 * Area code and exchange must begin with digit 1-9
	 * @param {HTMLElement} input
	 * @param {string} errorMessage
	 * @returns {boolean}
	 */
	function validateTelephone(input, errorMessage) {
		const value = input.value.trim();
		if (!value && !input.hasAttribute('required')) { clearError(input); return true; }
		if (!value) { showError(input, errorMessage || 'This field is required'); return false; }

		// Matches: ###-###-#### or ### ### #### (area code and exchange start with 1-9)
		const phoneRegex = /^[1-9]\d{2}[\s\-]?[1-9]\d{2}[\s\-]?\d{4}$/;
		if (!phoneRegex.test(value)) {
			showError(input, errorMessage || 'Please enter a valid phone number (e.g. 555-555-5555)');
			return false;
		}
		clearError(input);
		return true;
	}

	/**
	 * Validate a US zip code (##### or #####-####, separator can be blank)
	 * @param {HTMLElement} input
	 * @param {string} errorMessage
	 * @returns {boolean}
	 */
	function validateZipcode(input, errorMessage) {
		const value = input.value.trim();
		if (!value && !input.hasAttribute('required')) { clearError(input); return true; }
		if (!value) { showError(input, errorMessage || 'This field is required'); return false; }

		if (!/^\d{5}([\s\-]?\d{4})?$/.test(value)) {
			showError(input, errorMessage || 'Please enter a valid zip code (e.g. 12345 or 12345-6789)');
			return false;
		}
		clearError(input);
		return true;
	}

	/**
	 * Validate a credit card number using the Luhn algorithm (13-16 digits)
	 * @param {HTMLElement} input
	 * @param {string} errorMessage
	 * @returns {boolean}
	 */
	function validateCreditCard(input, errorMessage) {
		const value = input.value.trim().replace(/[\s\-]/g, '');
		if (!value && !input.hasAttribute('required')) { clearError(input); return true; }
		if (!value) { showError(input, errorMessage || 'This field is required'); return false; }

		if (!/^\d{13,16}$/.test(value)) {
			showError(input, errorMessage || 'Please enter a valid credit card number');
			return false;
		}

		// Luhn algorithm
		let sum = 0;
		let alternate = false;
		for (let i = value.length - 1; i >= 0; i--) {
			let digit = parseInt(value.charAt(i), 10);
			if (alternate) {
				digit *= 2;
				if (digit > 9) digit -= 9;
			}
			sum += digit;
			alternate = !alternate;
		}

		if (sum % 10 !== 0) {
			showError(input, errorMessage || 'Please enter a valid credit card number');
			return false;
		}
		clearError(input);
		return true;
	}

	/**
	 * Validate a US Social Security Number (###-##-####, separator can be blank)
	 * @param {HTMLElement} input
	 * @param {string} errorMessage
	 * @returns {boolean}
	 */
	function validateSSN(input, errorMessage) {
		const value = input.value.trim();
		if (!value && !input.hasAttribute('required')) { clearError(input); return true; }
		if (!value) { showError(input, errorMessage || 'This field is required'); return false; }

		if (!/^\d{3}[\s\-]\d{2}[\s\-]\d{4}$/.test(value)) {
			showError(input, errorMessage || 'Please enter a valid SSN (e.g. 123-45-6789)');
			return false;
		}
		clearError(input);
		return true;
	}

	/**
	 * Validate using the native HTML5 pattern attribute
	 * @param {HTMLElement} input
	 * @param {string} errorMessage
	 * @returns {boolean}
	 */
	function validateRegularExpression(input, errorMessage) {
		const value = input.value.trim();
		if (!value && !input.hasAttribute('required')) { clearError(input); return true; }
		if (!value) { showError(input, errorMessage || 'This field is required'); return false; }

		const pattern = input.getAttribute('pattern');
		if (!pattern) { clearError(input); return true; }

		try {
			const regex = new RegExp('^(?:' + pattern + ')$');
			if (!regex.test(value)) {
				showError(input, errorMessage || 'Please enter a value in the correct format');
				return false;
			}
		} catch (e) {
			console.warn('Invalid regular expression pattern:', pattern);
		}
		clearError(input);
		return true;
	}

	/**
	 * Validate a date input
	 * @param {HTMLElement} input - The input element
	 * @param {string} errorMessage - The error message to display
	 * @returns {boolean} - True if valid, false otherwise
	 */
	function validateDate(input, errorMessage) {
		const value = input.value.trim();

		// Allow empty if not required
		if (!value && !input.hasAttribute('required')) {
			clearError(input);
			return true;
		}

		// Required but empty
		if (!value && input.hasAttribute('required')) {
			showError(input, errorMessage || 'This field is required');
			return false;
		}

		// Get date format
		const format = input.getAttribute('data-dateformat') || 'mm/dd/yy';

		// Parse and validate
		const parsedDate = parseDate(value, format);

		if (parsedDate === null) {
			showError(input, errorMessage || `Invalid date format (expected ${format})`);
			return false;
		}

		clearError(input);
		return true;
	}

	/**
	 * Show validation error
	 * @param {HTMLElement} input - The input element
	 * @param {string} message - The error message
	 */
	function showError(input, message) {
		input.classList.add('bx-forms-error');
		input.setAttribute('aria-invalid', 'true');

		// Remove existing error message if present
		const existingError = input.nextElementSibling;
		if (existingError && existingError.classList.contains('bx-forms-error-message')) {
			existingError.remove();
		}

		// Create and insert error message element
		const errorDiv = document.createElement('div');
		errorDiv.className = 'bx-forms-error-message';
		errorDiv.setAttribute('role', 'alert');
		errorDiv.textContent = message;

		input.parentNode.insertBefore(errorDiv, input.nextSibling);
	}

	/**
	 * Clear validation error
	 * @param {HTMLElement} input - The input element
	 */
	function clearError(input) {
		input.classList.remove('bx-forms-error');
		input.setAttribute('aria-invalid', 'false');

		// Remove error message element
		const errorDiv = input.nextElementSibling;
		if (errorDiv && errorDiv.classList.contains('bx-forms-error-message')) {
			errorDiv.remove();
		}
	}

	/**
	 * Validate all inputs in a form
	 * @param {HTMLFormElement} form - The form element
	 * @returns {boolean} - True if all valid, false otherwise
	 */
	function validateForm(form) {
		const inputs = form.querySelectorAll('[data-validate]');
		let isValid = true;

		inputs.forEach(input => {
			if (!validateInput(input)) {
				isValid = false;
			}
		});

		return isValid;
	}

	/**
	 * Initialize form validation
	 */
	function init() {
		// Attach blur validation to all inputs with data-validate
		document.addEventListener('blur', function (e) {
			if (e.target.hasAttribute('data-validate')) {
				validateInput(e.target);
			}
		}, true);

		// Attach form submission validation
		document.addEventListener('submit', function (e) {
			const form = e.target;
			if (form.tagName === 'FORM') {
				if (!validateForm(form)) {
					e.preventDefault();
				}
			}
		}, true);

		// Clear error on input
		document.addEventListener('input', function (e) {
			if (e.target.hasAttribute('data-validate') &&
				e.target.classList.contains('bx-forms-error')) {
				// Debounce validation while user is typing
				clearTimeout(e.target._validationTimeout);
				e.target._validationTimeout = setTimeout(() => {
					validateInput(e.target);
				}, 500);
			}
		}, true);
	}

	// Initialize when DOM is ready
	if (document.readyState === 'loading') {
		document.addEventListener('DOMContentLoaded', init);
	} else {
		init();
	}

	// Export for testing/external use
	window.BXForms = {
		validateInput: validateInput,
		validateForm: validateForm,
		parseDate: parseDate,
		validateDate: validateDate,
		validateTime: validateTime,
		validateFloat: validateFloat,
		validateInteger: validateInteger,
		validateTelephone: validateTelephone,
		validateZipcode: validateZipcode,
		validateCreditCard: validateCreditCard,
		validateSSN: validateSSN,
		validateRegularExpression: validateRegularExpression
	};
})();
