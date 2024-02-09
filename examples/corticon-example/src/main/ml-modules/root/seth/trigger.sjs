const decisionService = require("/seth/decisionServiceBundle.js");

var uri;
console.log("Received URI: " + uri);

const data = cts.doc(uri).toObject();

const configuration = { logLevel: 1, logFunction: console.log };
const result = decisionService.execute(data, configuration);

result["original-uri"] = uri;
result["dateTime"] = fn.currentDateTime();

const newUri = "/seth/results/" + fn.currentDateTime() + ".json";

console.log("Writing", newUri);

xdmp.documentInsert(newUri, result, {
	"collections": ["seth-results", uri],
	"permissions": [xdmp.permission("rest-reader", "read"), xdmp.permission("rest-writer", "update")]
});
