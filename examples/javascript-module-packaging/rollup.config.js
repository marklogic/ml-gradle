import resolve from 'rollup-plugin-node-resolve';
import commonjs from 'rollup-plugin-commonjs';
import replace from 'rollup-plugin-replace';
// import babel from 'rollup-plugin-babel';

/**
 * Usage:
 *   $(npm bin)/rollup -c rollup.config.js && du -h dist/bundle.js
 */

export default {
	//input: 'browser/src/main.js',
	input: 'src/main.sjs',
	output: {
		// name: 'getRoles',
		file: 'dist/bundle.js',
		format: 'cjs',
		sourcemap: false // Won’t do anything in MarkLogic. Probably should for this to be useful in development.
	},

	plugins: [
		replace({
			'process.env.NODE_ENV': JSON.stringify('development')
		}),
		resolve({
			extensions: ['.mjs', '.js', '.sjs']
		}),
		commonjs()
		// babel({
		//   exclude: 'node_modules/**',
		// }),
	]
};
