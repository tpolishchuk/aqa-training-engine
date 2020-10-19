package org.lrp.aqa_training_engine.service;

import org.lrp.aqa_training_engine.model.User;
import org.lrp.aqa_training_engine.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void save(User user) {
        if(user.getUuid() == null) {
            user.setUuid(UUID.randomUUID());
        }

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Set<User> getAssociatedUsers(String taskUuid) {
        return userRepository.getAssociatedUsers(taskUuid);
    }
}
