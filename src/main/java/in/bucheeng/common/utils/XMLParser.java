/*
 *  @version     1.0, Feb 1, 2012
 *  @author sunny
 */
package in.bucheeng.common.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class XMLParser {

    public enum JsonPrimitiveTypes {
        NUMBER
    }

    private Document   document;
    private final File file;

    /**
     * @param filePath
     */
    public XMLParser(String filePath) {
        this(new File(filePath));
    }

    /**
     * @param file
     */
    public XMLParser(File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException("No such File Exists.File Path :" + file.getAbsolutePath());
        }
        this.file = file;
    }

    /**
     * @param text
     * @return
     */
    public static Element parse(String text) {
        return parse(text, false);
    }

    public static Element parse(String text, boolean ignoreNamespacePrefixes) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(ignoreNamespacePrefixes);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(new ByteArrayInputStream(text.trim().getBytes("UTF-8")));
            return new Element(document.getDocumentElement(), ignoreNamespacePrefixes);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return
     */
    public Element parse() {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(false);
            DocumentBuilder db = dbf.newDocumentBuilder();
            document = db.parse(new FileInputStream(file));
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return getRoot();
    }

    public Element getRoot() {
        return new Element(document.getDocumentElement(), false);
    }

    public static class Element {
        private final org.w3c.dom.Element element;
        private final boolean             ignoreNamespacePrefixes;

        private Element(org.w3c.dom.Element element, boolean ignoreNamespacePrefixes) {
            this.element = element;
            this.ignoreNamespacePrefixes = ignoreNamespacePrefixes;
        }

        public List<Element> list() {
            NodeList nList = element.getChildNodes();
            return getElementList(element, nList, true);
        }

        private List<Element> getElementList(org.w3c.dom.Element parentElement, NodeList nList, boolean directChildren) {
            List<Element> elements = new ArrayList<Element>(nList.getLength());
            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE && (!directChildren || nNode.getParentNode().isSameNode(parentElement))) {
                    elements.add(new Element((org.w3c.dom.Element) nNode, ignoreNamespacePrefixes));
                }
            }
            return elements;
        }

        private NodeList getElementsByTagName(String name) {
            if (ignoreNamespacePrefixes) {
                return element.getElementsByTagNameNS("*", name);
            } else {
                return element.getElementsByTagName(name);
            }
        }

        public List<Element> list(String name) {
            return getElementList(element, getElementsByTagName(name), false);
        }

        public List<Element> list(String name, boolean directChildren) {
            return getElementList(element, getElementsByTagName(name), directChildren);
        }

        public String attribute(String name) {
            return element.getAttribute(name);
        }

        public String attribute(String name, boolean required) {
            String value = element.getAttribute(name);
            if (StringUtils.isNotBlank(value)) {
                return value;
            }
            throw new RuntimeException("missing required attribute : [" + name + "], parent: [" + this.element.getTagName() + "]");
        }

        public Element get(String name) {
            NodeList nList = getElementsByTagName(name);
            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    return new Element((org.w3c.dom.Element) nNode, ignoreNamespacePrefixes);
                }
            }
            return null;
        }

        public Element get(String name, boolean required) {
            Element element = get(name);
            if (element != null) {
                return element;
            }
            throw new RuntimeException("missing required element : [" + name + "], parent: [" + this.element.getTagName() + "]");
        }

        public Element elementAt(String nameOrPath) {
            StringTokenizer tokenizer = new StringTokenizer(nameOrPath, "/");
            Element e = this;
            while (tokenizer.hasMoreTokens() && e != null) {
                e = e.get(tokenizer.nextToken());
            }
            return e;
        }

        public String text(String nameOrPath) {
            Element e = elementAt(nameOrPath);
            return e != null ? e.toString() : null;
        }

        public String text() {
            return element.getFirstChild() != null ? element.getFirstChild().getNodeValue() : StringUtils.EMPTY_STRING;
        }

        public String text(String nameOrPath, boolean required) {
            String value = text(nameOrPath);
            if (StringUtils.isNotBlank(value)) {
                return value;
            }
            throw new RuntimeException("missing required element : [" + nameOrPath + "], parent: [" + this.element.getTagName() + "]");
        }

        @Override
        public String toString() {
            return element.getTextContent();
        }

        public String getName() {
            return element.getNodeName();
        }
    }

    public static JsonElement toJson(String xml) {
        Element rootElement = XMLParser.parse(xml);
        return toJson(rootElement);
    }

    private static JsonElement toJson(Element element) {
        List<Element> childElements = element.list();
        if (childElements.size() == 0) {
            if (JsonPrimitiveTypes.NUMBER.name().equalsIgnoreCase(element.attribute("type"))) {
                return new JsonPrimitive(StringUtils.parseNumber(element.text()));
            } else {
                return new JsonPrimitive(element.text());
            }
        }
        if (isJsonArray(element)) {
            JsonArray jsonArray = new JsonArray();
            for (Element childElement : childElements) {
                jsonArray.add(toJson(childElement));
            }
            return jsonArray;
        } else {
            JsonObject jsonObject = new JsonObject();
            Map<String, JsonElement> jsonElementsMap = new HashMap<String, JsonElement>(1);
            Map<String, JsonArray> jsonArrayMap = new HashMap<String, JsonArray>(0);
            for (Element childElement : childElements) {
                JsonElement jsonElement = toJson(childElement);
                if (jsonArrayMap.containsKey(childElement.getName())) {
                    jsonArrayMap.get(childElement.getName()).add(jsonElement);
                } else if (jsonElementsMap.containsKey(childElement.getName())) {
                    JsonArray elementList = new JsonArray();
                    elementList.add(jsonElementsMap.remove(childElement.getName()));
                    elementList.add(jsonElement);
                    jsonArrayMap.put(childElement.getName(), elementList);
                } else {
                    jsonElementsMap.put(childElement.getName(), jsonElement);
                }
            }
            for (Map.Entry<String, JsonElement> entry : jsonElementsMap.entrySet()) {
                jsonObject.add(entry.getKey(), entry.getValue());
            }
            for (Map.Entry<String, JsonArray> entry : jsonArrayMap.entrySet()) {
                jsonObject.add(entry.getKey(), entry.getValue());
            }
            return jsonObject;
        }
    }

    private static boolean isJsonArray(Element element) {
        String parentName = element.getName();
        for (Element childElement : element.list()) {
            if (!childElement.getName().concat("s").equals(parentName)) {
                return false;
            }
        }
        return true;
    }
}
