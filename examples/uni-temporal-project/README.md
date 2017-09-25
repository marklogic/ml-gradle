This project shows how to deploy a sample uni-temporal config (without lsqt configuration)

Note that (as of this version), the deployed temporal config cannot be changed except for the temporal collection option properties (refer to https://docs.marklogic.com/temporal:collection-set-options for possible values)


Once you have deployed the code, you can insert documents uni-temporally by doing the following 

```javascript
'use strict';
declareUpdate();
const temporal = require("/MarkLogic/temporal.xqy");
const root =
    {"tempdoc": 
       {"content": "content here",
        systemStart: null,
        systemEnd: null}
    };
temporal.documentInsert("uni-temporal-collection", "doc.json", root);
```

