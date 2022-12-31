package in.onexdev;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class XMLBeanFactory implements BeanFactory {

    private SimpleBeanFactory simpleBeanFactory;

    public XMLBeanFactory(String xmlPath) {
        try {
            simpleBeanFactory = new SimpleBeanFactory();
            Document document = parseXmlDocument(xmlPath);
            NodeList beans = document.getElementsByTagName("Bean");
            registerBeansFromNodeList(beans);
        } catch (ParserConfigurationException | IOException | SAXException | InstantiationException |
                 IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static Document parseXmlDocument(String xmlPath) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        String beanXmlConfigPath = classloader.getResource(xmlPath).getPath();
        Document document = documentBuilder.parse(beanXmlConfigPath);
        return document;
    }

    private void registerBeansFromNodeList(NodeList beans) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
        for(int i = 0; i< beans.getLength(); i++){
            Node bean = beans.item(i);
            NamedNodeMap attributes = bean.getAttributes();
            String classname = attributes.getNamedItem("classname").getTextContent();
            Object instance = Class.forName(classname).getConstructor().newInstance();
            String beanName = attributes.getNamedItem("name").getTextContent();
            simpleBeanFactory.registerBean(beanName,instance);
        }
    }

    @Override
    public void registerBean(String name, Object instance) {
        simpleBeanFactory.registerBean(name, instance);
    }

    @Override
    public Object getBean(String beanName) {
        return simpleBeanFactory.getBean(beanName);
    }
}
