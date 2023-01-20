package in.onexdev.testScenarios.contructorinjection;

import in.onexdev.testScenarios.model.User;

public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserDetails(Long id){
        return userRepository.findById(id);
    }
}
