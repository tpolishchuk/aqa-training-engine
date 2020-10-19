package org.lrp.aqa_training_engine.service;

import org.apache.commons.lang3.StringUtils;
import org.lrp.aqa_training_engine.model.State;
import org.lrp.aqa_training_engine.model.Task;
import org.lrp.aqa_training_engine.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public void save(Task task) {
        if(task.getUuid() == null) {
            task.setUuid(UUID.randomUUID());
        }

        if(StringUtils.isBlank(task.getState())) {
            task.setState(State.NEW.name().toLowerCase());
        }

        taskRepository.save(task);
    }

    @Override
    public Optional<Task> findByUuid(String uuid) {
        return taskRepository.findById(UUID.fromString(uuid));
    }

    @Override
    public void delete(Task task) {
        taskRepository.delete(task);
    }

    @Override
    public Set<Task> findByUsernameAndState(String username, State state) {
        return taskRepository.findByUsernameAndState(username, state.name().toLowerCase());
    }

    @Override
    public void deleteTask(String username, String taskUuid) {
        taskRepository.deleteTaskOfUser(username, taskUuid);
    }

    @Override
    public void deleteTasks(Set<Task> tasks) {
        taskRepository.deleteInBatch(tasks);
    }
}
