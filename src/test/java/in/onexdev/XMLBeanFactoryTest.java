package in.onexdev;

import in.onexdev.testScenarios.UserRepository;
import in.onexdev.testScenarios.UserService;
import in.onexdev.testScenarios.model.User;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class XMLBeanFactoryTest {

    BeanFactory beanFactory = null;
    @Before
    public void configure(){
        beanFactory = new XMLBeanFactory("beans.xml");
        UserRepository userRepository = (UserRepository) beanFactory.getBean("UserRepository");
        UserService userService = (UserService)beanFactory.getBean("UserService");
        userService.setUserRepository(userRepository);
    }

    @Test
    public void testUserService() {
        UserService userService = (UserService) beanFactory.getBean("UserService");
        User userDetails = userService.getUserDetails(10L);
        assertEquals(userDetails.getAge(), 25);
    }
}
