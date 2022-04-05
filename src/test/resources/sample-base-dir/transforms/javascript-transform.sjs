function insertTimestamp(context, params, content)
{
  if (context.inputType.search('json') >= 0) {
    let result = content.toObject();
    if (context.acceptTypes) {                 /* read */
      result.readTimestamp = fn.currentDateTime();
    } else {                                   /* write */
      result.writeTimestamp = fn.currentDateTime();
    }
    return result;
  } else {
    /* Pass thru for non-JSON documents */
    return content;
  }
}

exports.transform = insertTimestamp;
