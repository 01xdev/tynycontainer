package in.onexdev;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class XMLBeanFactory implements BeanFactory {

    private SimpleBeanFactory simpleBeanFactory;
    private static XMLBeanFactory instance = null;

    public static XMLBeanFactory getInstance(){
        if(null == instance)
            throw new RuntimeException("BeanFactory not initialized");
        return instance;
    }
    public XMLBeanFactory(String xmlPath) {
        try {
            simpleBeanFactory = new SimpleBeanFactory();
            Document document = parseXmlDocument(xmlPath);
            NodeList beans = document.getElementsByTagName("Bean");
            registerBeansFromNodeList(beans);
            instance = this;
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

    private void registerBeansFromNodeList(NodeList beanNodes) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
        for(int i = 0; i< beanNodes.getLength(); i++){
            Node beanNode = beanNodes.item(i);
            NamedNodeMap attributes = beanNode.getAttributes();
            String classname = attributes.getNamedItem("classname").getTextContent();
            Object instance = Class.forName(classname).getConstructor().newInstance();
            injectSetterDependencies(instance, beanNode);
            String beanName = attributes.getNamedItem("name").getTextContent();
            simpleBeanFactory.registerBean(beanName,instance);
        }
    }

    private  void injectSetterDependencies(Object instance, Node beanNode) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        NodeList setterNodes = ((Element)beanNode).getElementsByTagName("setter");
        for(int i=0; i<setterNodes.getLength(); i++){
            Node setterNode = setterNodes.item(i);
            NamedNodeMap attributes = setterNode.getAttributes();
            String methodName = attributes.getNamedItem("name").getTextContent();
            String dependencyName = attributes.getNamedItem("bean").getTextContent();
            Bean dependency = simpleBeanFactory.getBean(dependencyName);
            Method setterMethod = instance.getClass().getMethod(methodName,Class.forName(dependency.className));
            setterMethod.invoke(instance,dependency.instance);
        }
    }

    @Override
    public void registerBean(String name, Object instance) {
        simpleBeanFactory.registerBean(name, instance);
    }

    @Override
    public Bean getBean(String beanName) {
        return simpleBeanFactory.getBean(beanName);
    }
}
