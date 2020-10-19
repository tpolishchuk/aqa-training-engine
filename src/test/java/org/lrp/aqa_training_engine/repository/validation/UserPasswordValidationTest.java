package org.lrp.aqa_training_engine.repository.validation;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.lrp.aqa_training_engine.model.User;
import org.lrp.aqa_training_engine.repository.JpaTestConfiguration;
import org.lrp.aqa_training_engine.utils.RandomEntryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ContextConfiguration(classes = {JpaTestConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserPasswordValidationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RandomEntryGenerator randomEntryGenerator;

    @Test
    public void whenSaveUserWithTooShortPasswordThenReturnError() {
        User user = randomEntryGenerator.generateRandomUser();
        user.setPassword(RandomStringUtils.randomAlphabetic(7));

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            entityManager.persist(user);
            entityManager.flush();
        });

        assertThat(exception.getConstraintViolations().size(), equalTo(1));
        assertThat(exception.getConstraintViolations().iterator().next().getMessageTemplate(),
                   equalTo("{user.password.size}"));
    }

    @Test
    public void whenSaveUserWithEmptyPasswordThenReturnErrors() {
        User user = randomEntryGenerator.generateRandomUser();
        user.setPassword(StringUtils.EMPTY);

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            entityManager.persist(user);
            entityManager.flush();
        });

        assertThat(exception.getConstraintViolations().size(), equalTo(2));

        Set<String> expectedMessageTemplates = new HashSet<>();
        expectedMessageTemplates.add("{user.password.not_blank}");
        expectedMessageTemplates.add("{user.password.size}");

        Set<String> actualMessageTemplates = exception.getConstraintViolations()
                                                      .stream()
                                                      .map(ConstraintViolation::getMessageTemplate)
                                                      .collect(Collectors.toSet());

        assertThat(actualMessageTemplates, equalTo(expectedMessageTemplates));
    }

    @Test
    public void whenSaveUserWithBlankPasswordThenReturnErrors() {
        User user = randomEntryGenerator.generateRandomUser();
        user.setPassword(" ");

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            entityManager.persist(user);
            entityManager.flush();
        });

        assertThat(exception.getConstraintViolations().size(), equalTo(2));

        Set<String> expectedMessageTemplates = new HashSet<>();
        expectedMessageTemplates.add("{user.password.not_blank}");
        expectedMessageTemplates.add("{user.password.size}");

        Set<String> actualMessageTemplates = exception.getConstraintViolations()
                                                      .stream()
                                                      .map(ConstraintViolation::getMessageTemplate)
                                                      .collect(Collectors.toSet());

        assertThat(actualMessageTemplates, equalTo(expectedMessageTemplates));
    }
}
