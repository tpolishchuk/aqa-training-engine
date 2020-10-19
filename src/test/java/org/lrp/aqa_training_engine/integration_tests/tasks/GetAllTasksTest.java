package org.lrp.aqa_training_engine.integration_tests.tasks;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lrp.aqa_training_engine.model.Task;
import org.lrp.aqa_training_engine.model.User;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class GetAllTasksTest extends AbstractTaskTest {

    @SneakyThrows
    @Test
    public void whenUnauthorizedThenCannotObserveTasks() {
        mockMvc.perform(get("/tasks")
                                .with(csrf()))
               .andExpect(status().is(302))
               .andExpect(redirectedUrl("http://localhost/login"));
    }

    @SneakyThrows
    @Test
    public void whenAuthorizedThenCanObserveTasks() {
        User user = randomEntryGenerator.generateRandomUser();
        userService.save(user);

        mockMvc.perform(get("/tasks")
                                .with(csrf())
                                .with(SecurityMockMvcRequestPostProcessors.user(user.getUsername())))
               .andExpect(status().isOk())
               .andExpect(view().name("tasks"))
               .andExpect(model().attributeExists("newTasksScope",
                                                  "inProgressTasksScope",
                                                  "doneTasksScope",
                                                  "task"))
               .andExpect(flash().attributeCount(0));
    }

    @SneakyThrows
    @Test
    public void whenGetAllTasksThenNewTasksScopeIsValid() {
        User user = randomEntryGenerator.generateRandomUser();
        userService.save(user);

        Task task1 = randomEntryGenerator.generateRandomTaskInState("new");
        user.getTasks().add(task1);
        task1.getUsers().add(user);
        taskService.save(task1);

        Task task2 = randomEntryGenerator.generateRandomTaskInState("new");
        user.getTasks().add(task2);
        task2.getUsers().add(user);
        taskService.save(task2);

        Set<Task> expectedTasks = new HashSet<>();
        expectedTasks.add(task1);
        expectedTasks.add(task2);

        MvcResult mvcResult = mockMvc.perform(get("/tasks")
                                                      .with(csrf())
                                                      .with(SecurityMockMvcRequestPostProcessors.user(user.getUsername())))
                                     .andExpect(status().isOk())
                                     .andExpect(view().name("tasks"))
                                     .andExpect(flash().attributeCount(0))
                                     .andReturn();

        Set<Task> actualNewTasks = (Set<Task>) mvcResult.getModelAndView().getModel().get("newTasksScope");
        assertThat(actualNewTasks.stream().map(Task::getUuid).collect(Collectors.toSet()),
                   equalTo(expectedTasks.stream().map(Task::getUuid).collect(Collectors.toSet())));

        assertThat((Set<Task>) mvcResult.getModelAndView().getModel().get("inProgressTasksScope"), hasSize(0));
        assertThat((Set<Task>) mvcResult.getModelAndView().getModel().get("doneTasksScope"), hasSize(0));
    }

    @SneakyThrows
    @Test
    public void whenGetAllTasksThenInProgressTasksScopeIsValid() {
        User user = randomEntryGenerator.generateRandomUser();
        userService.save(user);

        Task task1 = randomEntryGenerator.generateRandomTaskInState("in_progress");
        user.getTasks().add(task1);
        task1.getUsers().add(user);
        taskService.save(task1);

        Task task2 = randomEntryGenerator.generateRandomTaskInState("in_progress");
        user.getTasks().add(task2);
        task2.getUsers().add(user);
        taskService.save(task2);

        Set<Task> expectedTasks = new HashSet<>();
        expectedTasks.add(task1);
        expectedTasks.add(task2);

        MvcResult mvcResult = mockMvc.perform(get("/tasks")
                                                      .with(csrf())
                                                      .with(SecurityMockMvcRequestPostProcessors.user(user.getUsername())))
                                     .andExpect(status().isOk())
                                     .andExpect(view().name("tasks"))
                                     .andExpect(flash().attributeCount(0))
                                     .andReturn();

        Set<Task> foundTasks = (Set<Task>) mvcResult.getModelAndView().getModel().get("inProgressTasksScope");
        assertThat(foundTasks.stream().map(Task::getUuid).collect(Collectors.toSet()),
                   equalTo(expectedTasks.stream().map(Task::getUuid).collect(Collectors.toSet())));

        assertThat((Set<Task>) mvcResult.getModelAndView().getModel().get("newTasksScope"), hasSize(0));
        assertThat((Set<Task>) mvcResult.getModelAndView().getModel().get("doneTasksScope"), hasSize(0));
    }

    @SneakyThrows
    @Test
    public void whenGetAllTasksThenDoneTasksScopeIsValid() {
        User user = randomEntryGenerator.generateRandomUser();
        userService.save(user);

        Task task1 = randomEntryGenerator.generateRandomTaskInState("done");
        user.getTasks().add(task1);
        task1.getUsers().add(user);
        taskService.save(task1);

        Task task2 = randomEntryGenerator.generateRandomTaskInState("done");
        user.getTasks().add(task2);
        task2.getUsers().add(user);
        taskService.save(task2);

        Set<Task> expectedTasks = new HashSet<>();
        expectedTasks.add(task1);
        expectedTasks.add(task2);

        MvcResult mvcResult = mockMvc.perform(get("/tasks")
                                                      .with(csrf())
                                                      .with(SecurityMockMvcRequestPostProcessors.user(user.getUsername())))
                                     .andExpect(status().isOk())
                                     .andExpect(view().name("tasks"))
                                     .andExpect(flash().attributeCount(0))
                                     .andReturn();

        Set<Task> foundTasks = (Set<Task>) mvcResult.getModelAndView().getModel().get("doneTasksScope");
        assertThat(foundTasks.stream().map(Task::getUuid).collect(Collectors.toSet()),
                   equalTo(expectedTasks.stream().map(Task::getUuid).collect(Collectors.toSet())));

        assertThat((Set<Task>) mvcResult.getModelAndView().getModel().get("inProgressTasksScope"), hasSize(0));
        assertThat((Set<Task>) mvcResult.getModelAndView().getModel().get("newTasksScope"), hasSize(0));
    }
}
