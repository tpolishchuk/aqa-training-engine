package org.lrp.aqa_training_engine.controller;

import org.lrp.aqa_training_engine.model.Task;
import org.lrp.aqa_training_engine.model.User;
import org.lrp.aqa_training_engine.service.TaskService;
import org.lrp.aqa_training_engine.service.UserService;
import org.lrp.aqa_training_engine.utils.RandomEntryGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TasksRestController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @Autowired
    private RandomEntryGenerator randomEntryGenerator;

    private Logger logger = LoggerFactory.getLogger(TasksRestController.class);

    @PostMapping("/api/tasks/generate-random-entries")
    public ResponseEntity<Object> generateRandomEntries(@RequestParam("state") String state,
                                                        @RequestParam("amount") int amount,
                                                        @CurrentSecurityContext(expression = "authentication.name")
                                                                String username) {

        while (amount > 0) {
            User user = userService.findByUsername(username);
            Task task = randomEntryGenerator.generateRandomTaskInState(state);

            user.getTasks().add(task);
            task.getUsers().add(user);

            taskService.save(task);

            logger.info("Added task {} for user {}", task.getTitle(), username);

            amount--;
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/api/tasks/delete-all-user-tasks")
    public ResponseEntity<Object> deleteAllUserTasks(@CurrentSecurityContext(expression = "authentication.name")
                                                             String username) {

        User user = userService.findByUsername(username);

        taskService.deleteTasks(user.getTasks());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
