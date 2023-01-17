package in.onexdev;

import in.onexdev.testScenarios.UserRepository;
import in.onexdev.testScenarios.UserService;
import in.onexdev.testScenarios.model.User;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SimpleBeanFactoryTest {
    BeanFactory beanFactory = null;

    @Before
    public void configure() {
        beanFactory = new SimpleBeanFactory();
        // Registering beans
        beanFactory.registerBean("UserService", new UserService());
        beanFactory.registerBean("UserRepository", new UserRepository());
        // Wiring beans
        UserRepository userRepository = (UserRepository) beanFactory.getBean("UserRepository").instance;
        UserService userService = (UserService) beanFactory.getBean("UserService").instance;
        userService.setUserRepository(userRepository);
    }

    @Test
    public void testUserService() {
        UserService userService = (UserService) beanFactory.getBean("UserService").instance;
        User userDetails = userService.getUserDetails(10L);
        assertEquals(userDetails.getAge(), 25);
    }

}
