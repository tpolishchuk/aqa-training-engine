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

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

@DataJpaTest
@ContextConfiguration(classes = {JpaTestConfiguration.class })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RandomEntryGenerator randomEntryGenerator;

    @Test
    public void whenFindByUsernameThenReturnUser() {
        User user = randomEntryGenerator.generateRandomUser();

        entityManager.persist(user);
        entityManager.flush();

        User foundUser = userRepository.findByUsername(user.getUsername());

        assertThat(foundUser, equalTo(user));
    }

    @Test
    public void whenGetAssociatedUsersThenUserShouldBeReturned() {
        String state = "new";

        User user = randomEntryGenerator.generateRandomUser();
        Task task = randomEntryGenerator.generateRandomTaskInState(state);

        entityManager.persist(user);

        user.getTasks().add(task);
        task.getUsers().add(user);

        entityManager.persist(task);

        Set<User> foundUsers = userRepository.getAssociatedUsers(task.getUuid().toString());

        assertThat(foundUsers.size(), equalTo(1));
        assertThat(foundUsers, hasItem(user));
    }
}
