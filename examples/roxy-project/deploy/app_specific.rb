#
# Put your custom functions in this class in order to keep the files under lib untainted
#
# This class has access to all of the private variables in deploy/lib/server_config.rb
#
# any public method you create here can be called from the command line. See
# the examples below for more information.
#
class ServerConfig

  def hello_world()
    @logger.info "trying to run a custom query"
    r = execute_query %Q{
      xquery version "1.0-ml";
      ("one", "two", "three") ! element span { . }
    },
    {
      :app_name => "#{@properties['ml.app-name']}"
    }
    JSON.parse(r.body).each do |item|
      output = item['result']
      @logger.info "   " + output
    end
  end

  def get_document_count()
		r = execute_query %Q{
			xdmp:estimate(fn:doc())
		}
		@logger.info(r.body)
	end

	def delete_view()
		r = execute_query %Q{
			xquery version "1.0-ml";

			import module namespace view = "http://marklogic.com/xdmp/view"
				at "/MarkLogic/views.xqy";

			try {
				view:remove(
					"main",
					"Compliance"
				)
			} catch ($e) { () }
			(: Deletes a view, of the 'main' schema that contains columns, with a scope on the element, 'html'. :)
		},
		{ :db_name => @properties["ml.content-db"] }
	end

	def create_view()
		r = execute_query %Q{
			xquery version "1.0-ml";

			import module namespace view = "http://marklogic.com/xdmp/view"
				at "/MarkLogic/views.xqy";

			try {
				view:schema-create(
					"main",
					()
				)
			} catch ($e) {()},
			view:create(
					"main",
					"Compliance",
					view:element-view-scope(fn:QName("http://www.w3.org/1999/xhtml","html")),
					( view:column("uri", cts:uri-reference()),
						view:column("entityName", cts:path-reference('/xhtml:html/xhtml:head/xhtml:meta[@name eq "entityName"]/@content',("collation=http://marklogic.com/collation/"))),
						view:column("entityStreetAddress",  cts:path-reference('/xhtml:html/xhtml:head/xhtml:meta[@name eq "entityStreetAddress"]/@content',("collation=http://marklogic.com/collation/", ("nullable")))),
						view:column("entityCityAddress",  cts:path-reference('/xhtml:html/xhtml:head/xhtml:meta[@name eq "entityCityAddress"]/@content',("collation=http://marklogic.com/collation/", ("nullable")))),
						view:column("entityCountryAddress",  cts:path-reference('/xhtml:html/xhtml:head/xhtml:meta[@name eq "entityCountryAddress"]/@content',("collation=http://marklogic.com/collation//S2", ("nullable")))),
						view:column("foreignEntityStatus",  cts:path-reference('/xhtml:html/xhtml:head/xhtml:meta[@name eq "foreignEntityStatus"]/@content',("collation=http://marklogic.com/collation/", ("nullable")))),
						view:column("intermediaryEntityStatus",  cts:path-reference('/xhtml:html/xhtml:head/xhtml:meta[@name eq "intermediaryEntityStatus"]/@content',("collation=http://marklogic.com/collation/codepoint", ("nullable")))),
						view:column("EIN",  cts:path-reference('/xhtml:html/xhtml:head/xhtml:meta[@name eq "EIN"]/@content',("collation=http://marklogic.com/collation/", ("nullable")))),
						view:column("docType", cts:path-reference('/xhtml:html/xhtml:head/xhtml:meta[@name eq "docType"]/@content',("collation=http://marklogic.com/collation//S1", ("nullable"))))
					),
					()
			)

			(: Creates a view, of the 'main' schema that contains columns, with a scope on the element, 'html'. :)
		},
		{ :db_name => @properties["ml.content-db"] }
	end
end
