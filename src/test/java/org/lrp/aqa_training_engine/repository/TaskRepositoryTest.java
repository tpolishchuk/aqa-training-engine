package org.lrp.aqa_training_engine.repository;

import org.junit.jupiter.api.Test;
import org.lrp.aqa_training_engine.model.Task;
import org.lrp.aqa_training_engine.model.User;
import org.lrp.aqa_training_engine.utils.RandomEntryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ContextConfiguration(classes = {JpaTestConfiguration.class })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaskRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private RandomEntryGenerator randomEntryGenerator;

    @Test
    public void whenFindByUsernameAndStateThenTaskShouldBeReturned() {
        String state = "new";

        User user = randomEntryGenerator.generateRandomUser();

        entityManager.persist(user);

        Task task = randomEntryGenerator.generateRandomTaskInState(state);
        user.getTasks().add(task);
        task.getUsers().add(user);

        entityManager.persist(task);
        entityManager.flush();
        entityManager.clear();

        Set<Task> foundTasks = taskRepository.findByUsernameAndState(user.getUsername(), state);

        assertThat(foundTasks.size(), equalTo(1));
        assertThat(foundTasks.iterator().next().getUuid(), equalTo(task.getUuid()));
    }

    //TODO: investigate why it fails with H2 @Test
    public void whenDetachedFromUserThenTaskShouldExist() {
        String state = "new";

        User user = randomEntryGenerator.generateRandomUser();

        entityManager.persist(user);

        Task task = randomEntryGenerator.generateRandomTaskInState(state);
        user.getTasks().add(task);
        task.getUsers().add(user);

        entityManager.persist(task);

        taskRepository.deleteTaskOfUser(user.getUsername(), task.getUuid().toString());

        entityManager.flush();
        entityManager.clear();

        Optional<Task> foundTask = taskRepository.findById(task.getUuid());

        assertTrue(foundTask.isPresent());
        assertThat(foundTask.get().getUuid(), equalTo(task.getUuid()));
        assertThat(foundTask.get().getUsers(), empty());
    }
}
