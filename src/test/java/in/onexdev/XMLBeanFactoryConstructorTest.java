package in.onexdev;

import in.onexdev.testScenarios.contructorinjection.UserService;
import in.onexdev.testScenarios.model.User;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class XMLBeanFactoryConstructorTest {

    BeanFactory beanFactory = null;

    @Test
    public void testConstructorInjection(){
        beanFactory = new XMLBeanFactory("beans-contructor-injection.xml");
        UserService userService = (UserService) beanFactory.getBean("UserService").instance;
        User userDetails = userService.getUserDetails(10L);
        assertEquals(userDetails.getAge(), 25);
    }
}
