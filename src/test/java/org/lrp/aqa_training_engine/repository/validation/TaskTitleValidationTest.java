package org.lrp.aqa_training_engine.repository.validation;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.lrp.aqa_training_engine.model.Task;
import org.lrp.aqa_training_engine.repository.JpaTestConfiguration;
import org.lrp.aqa_training_engine.utils.RandomEntryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;

import javax.validation.ConstraintViolationException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ContextConfiguration(classes = {JpaTestConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TaskTitleValidationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RandomEntryGenerator randomEntryGenerator;

    @Test
    public void whenSaveTaskWithEmptyTitleThenReturnError() {
        Task task = randomEntryGenerator.generateRandomTaskInState("new");
        task.setTitle(StringUtils.EMPTY);

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            entityManager.persist(task);
            entityManager.flush();
        });

        assertThat(exception.getConstraintViolations().size(), equalTo(1));
        assertThat(exception.getConstraintViolations().iterator().next().getMessageTemplate(),
                   equalTo("{task.title.not_blank}"));
    }

    @Test
    public void whenSaveTaskWithBlankTitleThenReturnError() {
        Task task = randomEntryGenerator.generateRandomTaskInState("new");
        task.setTitle(" ");

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            entityManager.persist(task);
            entityManager.flush();
        });

        assertThat(exception.getConstraintViolations().size(), equalTo(1));
        assertThat(exception.getConstraintViolations().iterator().next().getMessageTemplate(),
                   equalTo("{task.title.not_blank}"));
    }

    @Test
    public void whenSaveTaskWithTooLongTitleThenReturnError() {
        Task task = randomEntryGenerator.generateRandomTaskInState("new");
        task.setTitle(RandomStringUtils.randomAlphabetic(201));

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            entityManager.persist(task);
            entityManager.flush();
        });

        assertThat(exception.getConstraintViolations().size(), equalTo(1));
        assertThat(exception.getConstraintViolations().iterator().next().getMessageTemplate(),
                   equalTo("{task.title.size}"));
    }
}
