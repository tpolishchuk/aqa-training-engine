package org.lrp.aqa_training_engine.service;

import org.lrp.aqa_training_engine.model.State;
import org.lrp.aqa_training_engine.model.Task;

import java.util.Optional;
import java.util.Set;

public interface TaskService {

    void save(Task task);

    void delete(Task task);

    Optional<Task> findByUuid(String uuid);

    Set<Task> findByUsernameAndState(String username, State state);

    void deleteTask(String username, String taskUuid);

    void deleteTasks(Set<Task> tasks);
}
