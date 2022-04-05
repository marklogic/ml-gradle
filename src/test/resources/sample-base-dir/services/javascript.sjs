// GET
//
// This function returns a document node corresponding to each
// user-defined parameter in order to demonstrate the following
// aspects of implementing REST extensions:
// - Returning multiple documents
// - Overriding the default response code
// - Setting additional response headers
//
function get(context, params) {
  let results = [];
  context.outputTypes = [];
  for (const pname in params) {
    if (params.hasOwnProperty(pname)) {
      results.push({name: pname, value: params[pname]});
      context.outputTypes.push('application/json');
    }
  }

  // Return a successful response status other than the default
  // using an array of the form [statusCode, statusMessage].
  // Do NOT use this to return an error response.
  context.outputStatus = [201, 'Yay'];

  // Set additional response headers using an object
  context.outputHeaders =
    {'X-My-Header1' : 42, 'X-My-Header2': 'h2val' };

  // Return a Sequence to return multiple documents
  return Sequence.from(results);
}

// PUT
//
// The client should pass in one or more documents, and for each
// document supplied, a value for the 'basename' request parameter.
// The function inserts the input documents into the database only
// if the input type is JSON or XML. Input JSON documents have a
// property added to them prior to insertion.
//
// Take note of the following aspects of this function:
// - The 'input' param might be a document node or a Sequence
//   over document nodes. You can normalize the values so your
//   code can always assume a Sequence.
// - The value of a caller-supplied parameter (basename, in this case)
//   might be a single value or an array.
// - context.inputTypes is always an array
// - How to return an error report to the client
//
function put(context, params, input) {
  // normalize the inputs so we don't care whether we have 1 or many
  const docs = normalizeInput(input);
  const basenames = params.basename instanceof Array
                  ? params.basename: [ params.basename ];

  // Validate inputs.
  if (docs.count > basenames.length) {
    returnErrToClient(400, 'Bad Request',
       'Insufficient number of uri basenames. Expected ' +
       docs.count + ' got ' + basenames.length + '.');
    // unreachable - control does not return from fn.error
  }

  // Do something with the input documents
  let i = 0;
  const uris = [];
  for (const doc of docs) {
    uris.push( doSomething(
        doc, context.inputTypes[i], basenames[i++]
    ));
  }

  // Set the response body MIME type and return the response data.
  context.outputTypes = ['application/json'];
  return { written: uris };
}

function post(context, params, input) {
  xdmp.log('POST invoked');
  return null;
}

function deleteFunction(context, params) {
  xdmp.log('POST invoked');
  return null;
}

// PUT helper func that demonstrates working with input documents.
//
// It inserts a (nonsense) property into the incoming document if
// it is a JSON document and simply inserts the document unchanged
// if it is an XML document. Other doc types are skipped.
//
// Input documents are imutable, so you must call toObject()
// to create a mutable copy if you want to make a change.
//
// The property added to the JSON input is set to the current time
// just so that you can easily observe it changing on each invocation.
//
function doSomething(doc, docType, basename)
{
  let uri = '/extensions/' + basename;
  if (docType == 'application/json') {
    // create a mutable version of the doc so we can modify it
    const mutableDoc = doc.toObject();
    uri += '.json';

    // add a JSON property to the input content
    mutableDoc.written = fn.currentTime();
    xdmp.documentInsert(uri, mutableDoc);
    return uri;
  } else if (docType == 'application/xml') {
    // pass thru an XML doc unchanged
    uri += '.xml';
    xdmp.documentInsert(uri, doc);
    return uri;
  } else {
    return '(skipped)';
  }
}

// Helper function that demonstrates how to normalize inputs
// that may or may not be multi-valued, such as the 'input'
// param to your methods.
//
// In cases where you might receive either a single value
// or a Sequence, depending on the request context,
// you can normalize the data type by creating a Sequence
// from the single value.
function normalizeInput(item)
{
  return (item instanceof Sequence)
         ? item                        // many
         : Sequence.from([item]);      // one
}

// Helper function that demonstrates how to return an error response
// to the client.

// You MUST use fn.error in exactly this way to return an error to the
// client. Raising exceptions or calling fn.error in another manner
// returns a 500 (Internal Server Error) response to the client.
function returnErrToClient(statusCode, statusMsg, body)
{
  fn.error(null, 'RESTAPI-SRVEXERR',
           Sequence.from([statusCode, statusMsg, body]));
  // unreachable - control does not return from fn.error.
}


// Include an export for each method supported by your extension.
exports.GET = get;
exports.POST = post;
exports.PUT = put;
exports.DELETE = deleteFunction;
