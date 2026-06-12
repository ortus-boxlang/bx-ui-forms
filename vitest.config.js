import { defineConfig } from "vitest/config";

export default defineConfig({
	test: {
		environment: "jsdom",
		globals: true,
		setupFiles: ["./src/test/js/setup.js"],
		include: ["src/test/js/**/*.test.js"],
		coverage: {
			provider: "istanbul",
			include: ["src/main/resources/js/**/*.js"],
			reporter: ["text", "html", "lcov"],
			reportsDirectory: "./build/reports/js-coverage"
		}
	}
});
