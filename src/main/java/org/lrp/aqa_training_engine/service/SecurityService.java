package org.lrp.aqa_training_engine.service;

public interface SecurityService {

    String findLoggedInUserEmail();

    void login(String username, String password);
}
