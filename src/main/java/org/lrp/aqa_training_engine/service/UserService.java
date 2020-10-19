package org.lrp.aqa_training_engine.service;

import org.lrp.aqa_training_engine.model.User;

import java.util.Set;

public interface UserService {

    void save(User user);

    User findByUsername(String username);

    Set<User> getAssociatedUsers(String taskUuid);
}
