package com.ryan.gerald.beancoin.Service;

import com.ryan.gerald.beancoin.entity.User;
import com.ryan.gerald.beancoin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public User getUserByUsername(String username) {
        return nullChecker(userRepository.findById(username));
    }

    public Optional<User> getUserOptionalByName(String username) {
        return userRepository.findById(username);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public String validateUserAndPassword(String username, String password) {
        Optional<User> user = this.getUserOptionalByName(username);
        if (user.isEmpty()) {return "user not found";}
        if (user.get().getPassword().equalsIgnoreCase(password)) {return "true";}
        return "false";
    }


    // TODO How can I extract/reuse this most elegantly?
    public <T> T nullChecker(Optional<T> o){
        if (o.isPresent()){return o.get(); }
        else { return null; }
    }
}
