package org.lrp.aqa_training_engine.integration_tests.tasks;

import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
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
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EditTaskTest extends AbstractTaskTest {

    @SneakyThrows
    @Test
    public void whenTaskIsUpdatedWithValidDataThenUpdateIsSavedInDb() {
        User user = randomEntryGenerator.generateRandomUser();
        userService.save(user);

        Task task = randomEntryGenerator.generateRandomTaskInState("new");
        user.getTasks().add(task);
        task.getUsers().add(user);
        taskService.save(task);

        task.setTitle("New-" + RandomStringUtils.randomNumeric(10));
        task.setDescription("New-" + RandomStringUtils.randomNumeric(20));

        mockMvc.perform(post("/tasks/edit")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(convertTaskToEncodedUrlParams(task, true))
                                .with(csrf())
                                .with(SecurityMockMvcRequestPostProcessors.user(user.getUsername())))
               .andExpect(status().is(302))
               .andExpect(view().name("redirect:/tasks"))
               .andExpect(flash().attributeCount(0));

        Set<Task> foundTasks = taskService.findByUsernameAndState(user.getUsername(), State.NEW);

        assertThat(foundTasks.size(), equalTo(1));
        assertThat(foundTasks.iterator().next().getTitle(), equalTo(task.getTitle()));
        assertThat(foundTasks.iterator().next().getDescription(), equalTo(task.getDescription()));
    }

    @SneakyThrows
    @Test
    public void whenTaskIsUpdatedWithInvalidDataThenUpdateIsNotSavedInDb() {
        User user = randomEntryGenerator.generateRandomUser();
        userService.save(user);

        Task task = randomEntryGenerator.generateRandomTaskInState("new");
        user.getTasks().add(task);
        task.getUsers().add(user);
        taskService.save(task);

        String originTitle = task.getTitle();
        String originDescription = task.getDescription();

        task.setTitle(StringUtils.EMPTY);
        task.setDescription("New-" + RandomStringUtils.randomNumeric(20));

        mockMvc.perform(post("/tasks/edit")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(convertTaskToEncodedUrlParams(task, true))
                                .with(csrf())
                                .with(SecurityMockMvcRequestPostProcessors.user(user.getUsername())))
               .andExpect(status().is(302))
               .andExpect(view().name("redirect:/edit-task?uuid=" + task.getUuid()))
               .andExpect(flash().attributeCount(2))
               .andExpect(flash().attributeExists("task"))
               .andExpect(flash().attributeExists("lastFailedTaskErrors"));

        Set<Task> foundTasks = taskService.findByUsernameAndState(user.getUsername(), State.NEW);

        assertThat(foundTasks.size(), equalTo(1));
        assertThat(foundTasks.iterator().next().getTitle(), equalTo(originTitle));
        assertThat(foundTasks.iterator().next().getDescription(), equalTo(originDescription));
    }

    @SneakyThrows
    @Test
    public void whenTaskStateIsUpdatedThenItIsCorrectlySavedInDb() {
        User user = randomEntryGenerator.generateRandomUser();
        userService.save(user);

        Task task = randomEntryGenerator.generateRandomTaskInState("new");
        user.getTasks().add(task);
        task.getUsers().add(user);
        taskService.save(task);

        State newState = State.DONE;

        Map<String, String> params = new HashMap<>();
        params.put("task_uuid", task.getUuid().toString());
        params.put("new_state", newState.name());

        mockMvc.perform(post("/tasks/edit-state")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(urlEncoderUtil.convertToEncodedUrl(params))
                                .with(csrf())
                                .with(SecurityMockMvcRequestPostProcessors.user(user.getUsername())))
               .andExpect(status().is(302))
               .andExpect(view().name("redirect:/tasks"))
               .andExpect(flash().attributeCount(0));

        Optional<Task> foundTask = taskService.findByUuid(task.getUuid().toString());

        assertTrue(foundTask.isPresent());
        assertThat(foundTask.get().getState(), equalTo(newState.name()));
    }

    @SneakyThrows
    @Test
    public void whenEditTaskViewIsOpenedThenItShouldContainCorrectTask() {
        User user = randomEntryGenerator.generateRandomUser();
        userService.save(user);

        Task task = randomEntryGenerator.generateRandomTaskInState("new");
        user.getTasks().add(task);
        task.getUsers().add(user);
        taskService.save(task);

        mockMvc.perform(get("/edit-task")
                                .param("uuid", task.getUuid().toString())
                                .with(csrf())
                                .with(SecurityMockMvcRequestPostProcessors.user(user.getUsername())))
               .andExpect(status().isOk())
               .andExpect(view().name("edit-task"))
               .andExpect(model().attribute("task", task));
    }
}
