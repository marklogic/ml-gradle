See [the MarkLogic docs](http://docs.marklogic.com/REST/POST/manage/v2/databases/[id-or-name]/pipelines) for what a 
CPF pipeline JSON/XML file can contain.

Note that as of MarkLogic 8.0-3, the pipeline schema does not allow for a condition element to have a nested options
element. This will be fixed in 8.0-4. In the meantime, the best way to work around this is to create your own 
condition module that does not require an options element - in effect, move the logic expressed by the options element
into a custom condition. 
