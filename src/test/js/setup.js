import { existsSync, readFileSync } from "fs";
import { dirname, resolve } from "path";
import { fileURLToPath } from "url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);
const JS_DIR = resolve(__dirname, "../../main/bx/public/js");

export function loadScript(filename) {
	const code = readFileSync(resolve(JS_DIR, filename), "utf-8");
	const wrapped = `(function(){\n${code}\n}).call(this);`;
	const fn = new Function(wrapped);
	fn.call(window);
}

export function loadCompatScript(filename) {
	const candidates = process.env.BX_UI_COMPAT_DIR
		? [resolve(process.env.BX_UI_COMPAT_DIR, "public/js")]
		: [
			resolve(__dirname, "../resources/modules/bx-ui-compat/public/js"),
			resolve(__dirname, "../../../../bx-ui-compat/src/main/bx/public/js"),
		];
	const directory = candidates.find((candidate) => existsSync(resolve(candidate, filename)));
	if (!directory) {
		throw new Error("Install bx-ui-compat or set BX_UI_COMPAT_DIR to its module directory");
	}
	const code = readFileSync(resolve(directory, filename), "utf-8");
	new Function(`(function(){\n${code}\n}).call(this);`).call(window);
}
