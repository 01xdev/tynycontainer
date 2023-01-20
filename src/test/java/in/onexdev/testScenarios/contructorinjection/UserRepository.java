package in.onexdev.testScenarios.contructorinjection;

import in.onexdev.testScenarios.model.User;

public class UserRepository {
    public User findById(Long id){
        return new User("John","Doe", 25);
    }
}
