package org.lrp.aqa_training_engine.integration_tests.tasks;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lrp.aqa_training_engine.model.State;
import org.lrp.aqa_training_engine.model.Task;
import org.lrp.aqa_training_engine.model.User;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DeleteTaskTest extends AbstractTaskTest {

    @SneakyThrows
    //TODO: investigate why it fails with H2 @Test
    public void whenTaskIsDeletedThenItDoesNotExistInDbButUserStillExists() {
        User user = randomEntryGenerator.generateRandomUser();
        userService.save(user);

        Task task = randomEntryGenerator.generateRandomTaskInState("new");
        user.getTasks().add(task);
        task.getUsers().add(user);
        taskService.save(task);

        State newState = State.DONE;

        Map<String, String> params = new HashMap<>();
        params.put("task_uuid", task.getUuid().toString());

        mockMvc.perform(post("/tasks/delete")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(urlEncoderUtil.convertToEncodedUrl(params))
                                .with(csrf())
                                .with(SecurityMockMvcRequestPostProcessors.user(user.getUsername())))
               .andExpect(status().is(302))
               .andExpect(view().name("redirect:/tasks"))
               .andExpect(flash().attributeCount(0));

        User foundUser = userService.findByUsername(user.getUsername());
        assertThat(foundUser, notNullValue());

        Set<Task> foundTasksOfUser = taskService.findByUsernameAndState(user.getUsername(), newState);
        assertThat(foundTasksOfUser.size(), equalTo(0));

        Optional<Task> foundTaskByUuid = taskService.findByUuid(task.getUuid().toString());
        assertThat(foundTaskByUuid.isPresent(), is(false));
    }

    @SneakyThrows
    @Test
    public void whenDeleteAllTasksThenUserShouldHaveNoAssociatedTasks() {
        User user = randomEntryGenerator.generateRandomUser();
        userService.save(user);

        State state = State.NEW;

        Task task = randomEntryGenerator.generateRandomTaskInState(state.name());
        user.getTasks().add(task);
        task.getUsers().add(user);
        taskService.save(task);

        mockMvc.perform(delete("/api/tasks/delete-all-user-tasks")
                                .with(csrf())
                                .with(SecurityMockMvcRequestPostProcessors.user(user.getUsername())))
               .andExpect(status().isNoContent());

        User foundUser = userService.findByUsername(user.getUsername());
        assertThat(foundUser, notNullValue());

        Set<Task> foundTasksOfUser = taskService.findByUsernameAndState(user.getUsername(), state);
        assertThat(foundTasksOfUser.size(), equalTo(0));

        Optional<Task> foundTaskByUuid = taskService.findByUuid(task.getUuid().toString());
        assertThat(foundTaskByUuid.isPresent(), is(false));
    }
}
