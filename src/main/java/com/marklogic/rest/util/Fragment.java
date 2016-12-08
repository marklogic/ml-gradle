package com.marklogic.rest.util;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

public class Fragment {

    private Document internalDoc;
    private Namespace[] namespaces;

    public Fragment(Fragment other) {
        this.internalDoc = other.internalDoc;
        this.namespaces = other.namespaces;
    }

    public Fragment(String xml, Namespace... namespaces) {
        try {
            internalDoc = new SAXBuilder().build(new StringReader(xml));
            List<Namespace> list = new ArrayList<Namespace>();
            list.add(Namespace.getNamespace("c", "http://marklogic.com/manage/clusters"));
            list.add(Namespace.getNamespace("db", "http://marklogic.com/manage/databases"));
            list.add(Namespace.getNamespace("f", "http://marklogic.com/manage/forests"));
            list.add(Namespace.getNamespace("h", "http://marklogic.com/manage/hosts"));
            list.add(Namespace.getNamespace("m", "http://marklogic.com/manage"));
            list.add(Namespace.getNamespace("s", "http://marklogic.com/manage/servers"));
            list.add(Namespace.getNamespace("msec", "http://marklogic.com/manage/security"));
            list.add(Namespace.getNamespace("sec", "http://marklogic.com/xdmp/security"));
            list.add(Namespace.getNamespace("arp", "http://marklogic.com/manage/alert-rule/properties"));
			list.add(Namespace.getNamespace("ts", "http://marklogic.com/manage/task-server"));
			list.add(Namespace.getNamespace("req", "http://marklogic.com/manage/requests"));
            for (Namespace n : namespaces) {
                list.add(n);
            }
            this.namespaces = list.toArray(new Namespace[] {});
        } catch (Exception e) {
            throw new RuntimeException(String.format("Unable to parse XML, cause: %s; XML: %s", e.getMessage(), xml), e);
        }
    }

    public void prettyPrint() {
        System.out.println(getPrettyXml());
    }

    public String getPrettyXml() {
        return new XMLOutputter(Format.getPrettyFormat()).outputString(internalDoc);
    }

    public boolean elementExists(String xpath) {
        return evaluateForElements(xpath).size() > 0;
    }

    public List<String> getElementValues(String xpath) {
        List<String> values = new ArrayList<String>();
        for (Element el : evaluateForElements(xpath)) {
            values.add(el.getText());
        }
        return values;
    }

    public String getElementValue(String xpath) {
        List<String> values = getElementValues(xpath);
        return values.isEmpty() ? null : values.get(0);
    }

    public List<Element> getElements(String xpath) {
        return evaluateForElements(xpath);
    }

    protected List<Element> evaluateForElements(String xpath) {
        XPathFactory f = XPathFactory.instance();
        XPathExpression<Element> expr = f.compile(xpath, Filters.element(), new HashMap<String, Object>(), namespaces);
        return expr.evaluate(internalDoc);
    }

    public Document getInternalDoc() {
        return internalDoc;
    }
}
