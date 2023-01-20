package in.onexdev;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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
            Object instance = initializeBean(classname,beanNode);
            injectSetterDependencies(instance, beanNode);
            String beanName = attributes.getNamedItem("name").getTextContent();
            simpleBeanFactory.registerBean(beanName,instance);
        }
    }

    private  Object initializeBean(String classname, Node beanNode) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
        NodeList constructorNodes = ((Element)beanNode).getElementsByTagName("constructor");
        Object beanInstance;

        if(constructorNodes.getLength() == 0)
            beanInstance =  Class.forName(classname).getConstructor().newInstance();
        else if(constructorNodes.getLength() > 1)
            throw new RuntimeException("Class has more than one constructor");
        else
            beanInstance = instantiateClassUsingConstructor(classname, constructorNodes);

        return beanInstance;
    }

    private Object instantiateClassUsingConstructor(String classname, NodeList constructorNodes) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
        Object beanInstance;
        Node contructorNode = constructorNodes.item(0);
        NodeList argumentNodes = ((Element)contructorNode).getElementsByTagName("arg");
        List<Bean> argumentBeans = getArgumentBeansOfConstructor(argumentNodes);

        Class[] parameterTypes = getTypesOfArguments(argumentBeans);
        Object[] arguments = getArguments(argumentBeans);
        beanInstance = Class.forName(classname).getConstructor(parameterTypes).newInstance(arguments);

        return beanInstance;
    }

    private Object[] getArguments(List<Bean> argumentBeans) {
        Object[] arguments = argumentBeans.stream()
                .map(argumentBean -> argumentBean.instance)
                .toArray();
        return arguments;
    }

    private  Class[] getTypesOfArguments(List<Bean> argumentBeans) {
        return argumentBeans.stream()
                .map(argumentBean -> {
                    try {
                        return Class.forName(argumentBean.className);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }).toArray(Class[]::new);
    }

    private List<Bean> getArgumentBeansOfConstructor(NodeList argumentNodes) {
        List<Bean> argumentBeans = new ArrayList();
        for(int i = 0; i< argumentNodes.getLength(); i++){
            Node argumentNode = argumentNodes.item(i);
            String beanName = argumentNode.getAttributes().getNamedItem("bean").getTextContent();
            Bean bean = simpleBeanFactory.getBean(beanName);
            argumentBeans.add(bean);
        }
        return argumentBeans;
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
