package in.onexdev;

import in.onexdev.testScenarios.UserRepository;
import in.onexdev.testScenarios.UserService;
import in.onexdev.testScenarios.model.User;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class XMLBeanFactoryTest {

    BeanFactory beanFactory = null;

    @Test
    public void testUserService() {
        beanFactory = new XMLBeanFactory("beans.xml");
        UserService userService = (UserService) beanFactory.getBean("UserService").instance;
        User userDetails = userService.getUserDetails(10L);
        assertEquals(userDetails.getAge(), 25);
    }
}
