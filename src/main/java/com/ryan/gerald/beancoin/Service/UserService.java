package com.ryan.gerald.beancoin.Service;

import com.ryan.gerald.beancoin.Dao.UserDao;
import com.ryan.gerald.beancoin.entity.User;
import com.ryan.gerald.beancoin.entity.UserRepository;
import com.ryan.gerald.beancoin.entity.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;


    public User getUserByUsername(String username) {
        return userRepository.findById(username).get();
    }

    public Optional<User> getUserOptionalByName(String username) {
        return userRepository.findById(username);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

}
