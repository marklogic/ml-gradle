xquery version "1.0-ml";

import module namespace op="http://marklogic.com/optic" at "/MarkLogic/optic.xqy";

op:from-view("HR", "employees")
   => op:where(op:eq(op:col('Department'), "Sales"))
   => op:generate-view("HR", "Sales")
