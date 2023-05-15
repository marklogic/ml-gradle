const test = require("/test/test-helper.xqy");
const example = require("/example.sjs");

const assertions = [];
assertions.push(test.assertEqual("Echo: Hello", example.echo("Hello")));
assertions;
