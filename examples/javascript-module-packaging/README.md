## Module Bundling and Transpiling

[Server-Side JavaScript](https://docs.marklogic.com/guide/jsref/language) was a big step forward in making MarkLogic’s in-database programming environment more accessible to developers. It also opens up the possibility of using external libraries from the broader JavaScript ecosystem. However, today managing those dependencies is tricky and MarkLogic does not yet support—or may never support—capabilities like [ES2015 modules](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Statements/import) or [TypeScript](https://www.typescriptlang.org), which are seen as table stakes in the [Node](https://nodejs.org/en/) and browser communities.

Today ml-gradle has the ability to load modules from a local directory, for example, managed by a version control system and edited in an IDE, into a Modules database. This proof-of-concept lays the groundwork to apply a packaging step between the local file system and a modules database. The current implementation uses [Rollup](https://rollupjs.org/guide/en).

Processing JavaScript before loading it into a modules database has several benefits:

- Declare and manage dependencies with npm: Rollup has CommonJS and Node modules plugins that allow packaged code to use [Node.js’s package resolution rules, including `node_modules`](https://nodejs.org/api/modules.html). This would allow MarkLogic developers to tap into [npm](https://www.npmjs.com). For example, the `src/query-builder.js` library module imports two functions from the open-source [lodash](https://lodash.com) library.
- ES2015 modules and tree shaking: Rollup defaults to ES2015 modules. Because ES2015 modules are declarative, Rollup can do optimizations around only bundling code that is used and discarding unused modules or even exports within a module. This is called “tree shaking”. The result is that developers get to use ES2015 modules and the resulting code is smaller and less complex than with manually managed CommonJS(ish) modules today. There’s also anecdotal evidence that smaller dependency graphs may improve JavaScript performance in MarkLogic modules. _(This hypothesis still needs to be tested.)_
- Rollup has a rich [plugin architecture](https://github.com/rollup/rollup/wiki/Plugins) that would allow other processing steps, such as [transpiling TypeScript](https://www.npmjs.com/package/rollup-plugin-typescript2) to vanilla ES2015 or [swapping out flags](https://github.com/rollup/rollup-plugin-replace), such as Node’s `process.env.NODE_ENV` to configure development, test, or production environments at build-time, already a concept in Gradle. This would allow developers to use the upstream tools that they’re accustomed to without MarkLogic having to explicitly support these.

## Usage

Rollup requires a recent version of Node.js. (Node.js installs `npm`, its package manager, globally as well.)

```shell
> node --version && npm --version
v8.11.4
6.4.1
```

Once you have Node and npm installed, install the dependencies for the example module. This includes the runtime and development-time dependencies.

From the `examples/javascript-module-packaging` directory,

```shell
> npm install
```

Finally, build the bundle:

```shell
> $(npm bin)/rollup -c rollup.config.js
```

Copy and paste the contents of `dist/bundle.js` into a Query Console tab to verify everything worked.

For the record, here’s the code that I used to populate the database for testing. (Ironically, I could probably set that up with ml-gradle.)

```js
'use strict';
declareUpdate();
for (let i = 0; i < 10000; i++) {
	const doc = {
		color: ['red', 'orange', 'yellow', 'green'][Math.floor(Math.random() * 4)]
	};
	xdmp.documentInsert(`/${i}.json`, doc, {
		collections: ['final', 'staging'][Math.floor(Math.random() * 2)]
	});
}
```
