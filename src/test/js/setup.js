import { readFileSync } from "fs";
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
