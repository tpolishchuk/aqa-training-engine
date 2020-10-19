package org.lrp.aqa_training_engine.integration_tests.tasks;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AddTaskTest extends AbstractTaskTest{

    @SneakyThrows
    @Test
    public void whenValidTaskIsAddedThenItIsSavedInDb() {
        User user = randomEntryGenerator.generateRandomUser();
        userService.save(user);

        Task task = randomEntryGenerator.generateRandomTaskInState("new");

        mockMvc.perform(post("/tasks/add")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(convertTaskToEncodedUrlParams(task))
                                .with(csrf())
                                .with(SecurityMockMvcRequestPostProcessors.user(user.getUsername())))
               .andExpect(status().is(302))
               .andExpect(view().name("redirect:/tasks"))
               .andExpect(flash().attributeCount(0));

        Set<Task> foundTasks = taskService.findByUsernameAndState(user.getUsername(), State.NEW);

        assertThat(foundTasks.size(), equalTo(1));
        assertThat(foundTasks.iterator().next().getTitle(), equalTo(task.getTitle()));
    }

    @SneakyThrows
    @Test
    public void whenInvalidTaskIsAddedThenItIsNotSavedInDb() {
        User user = randomEntryGenerator.generateRandomUser();
        userService.save(user);

        Task task = randomEntryGenerator.generateRandomTaskInState("new");
        task.setTitle(StringUtils.EMPTY);

        mockMvc.perform(post("/tasks/add")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(convertTaskToEncodedUrlParams(task))
                                .with(csrf())
                                .with(SecurityMockMvcRequestPostProcessors.user(user.getUsername())))
               .andExpect(status().is(302))
               .andExpect(view().name("redirect:/tasks"))
               .andExpect(flash().attributeCount(3))
               .andExpect(flash().attribute("reopenAddTaskModal", true))
               .andExpect(flash().attributeExists("task"))
               .andExpect(flash().attributeExists("lastFailedTaskErrors"));

        Set<Task> foundTasks = taskService.findByUsernameAndState(user.getUsername(), State.NEW);

        assertThat(foundTasks.size(), equalTo(0));
    }

    @SneakyThrows
    @Test
    public void whenGenerateRandomTasksThenUserShouldBeAssociated() {
        User user = randomEntryGenerator.generateRandomUser();
        userService.save(user);

        State state = State.NEW;
        int amount = 3;

        Map<String, String> params = new HashMap<>();
        params.put("state", state.name().toLowerCase());
        params.put("amount", String.valueOf(amount));

        mockMvc.perform(post("/api/tasks/generate-random-entries")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(urlEncoderUtil.convertToEncodedUrl(params))
                                .with(csrf())
                                .with(SecurityMockMvcRequestPostProcessors.user(user.getUsername())))
               .andExpect(status().isNoContent());

        User foundUser = userService.findByUsername(user.getUsername());
        assertThat(foundUser, notNullValue());

        Set<Task> foundTasksOfUser = taskService.findByUsernameAndState(user.getUsername(), state);
        assertThat(foundTasksOfUser.size(), equalTo(3));
    }
}
