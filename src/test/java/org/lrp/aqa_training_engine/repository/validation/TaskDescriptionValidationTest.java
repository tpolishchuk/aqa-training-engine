package org.lrp.aqa_training_engine.repository.validation;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.lrp.aqa_training_engine.model.Task;
import org.lrp.aqa_training_engine.model.User;
import org.lrp.aqa_training_engine.repository.JpaTestConfiguration;
import org.lrp.aqa_training_engine.repository.TaskRepository;
import org.lrp.aqa_training_engine.utils.RandomEntryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;

import javax.validation.ConstraintViolationException;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ContextConfiguration(classes = {JpaTestConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TaskDescriptionValidationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private RandomEntryGenerator randomEntryGenerator;

    @Test
    public void whenSaveTaskWithTooLongDescriptionThenReturnError() {
        Task task = randomEntryGenerator.generateRandomTaskInState("new");
        task.setDescription(RandomStringUtils.randomAlphabetic(201));

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            entityManager.persist(task);
            entityManager.flush();
        });

        assertThat(exception.getConstraintViolations().size(), equalTo(1));
        assertThat(exception.getConstraintViolations().iterator().next().getMessageTemplate(),
                   equalTo("{task.description.size}"));
    }

    @Test
    public void whenSaveTaskWithEmptyDescriptionThenTaskIsPresentInDb() {
        String state = "new";
        User user = randomEntryGenerator.generateRandomUser();

        entityManager.persist(user);

        Task task = randomEntryGenerator.generateRandomTaskInState(state);
        task.setDescription(StringUtils.EMPTY);
        user.getTasks().add(task);
        task.getUsers().add(user);

        entityManager.persist(task);
        entityManager.flush();
        entityManager.clear();

        Set<Task> foundTasks = taskRepository.findByUsernameAndState(user.getUsername(), state);

        assertThat(foundTasks.size(), equalTo(1));
        assertThat(foundTasks.iterator().next().getUuid(), equalTo(task.getUuid()));
        assertThat(foundTasks.iterator().next().getDescription(), is(emptyString()));
    }
}
