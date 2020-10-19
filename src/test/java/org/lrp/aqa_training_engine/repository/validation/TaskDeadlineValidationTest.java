package org.lrp.aqa_training_engine.repository.validation;

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
import java.util.Calendar;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ContextConfiguration(classes = {JpaTestConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TaskDeadlineValidationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RandomEntryGenerator randomEntryGenerator;

    @Test
    public void whenSaveTaskWithNullDeadlineThenReturnError() {
        Task task = randomEntryGenerator.generateRandomTaskInState("new");
        task.setDeadline(null);

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            entityManager.persist(task);
            entityManager.flush();
        });

        assertThat(exception.getConstraintViolations().size(), equalTo(1));
        assertThat(exception.getConstraintViolations().iterator().next().getMessageTemplate(),
                   equalTo("{task.deadline.not_null}"));
    }

    @Test
    public void whenSaveTaskWithNotFutureDeadlineThenReturnError() {
        Task task = randomEntryGenerator.generateRandomTaskInState("new");
        task.setDeadline(Calendar.getInstance().getTime());

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            entityManager.persist(task);
            entityManager.flush();
        });

        assertThat(exception.getConstraintViolations().size(), equalTo(1));
        assertThat(exception.getConstraintViolations().iterator().next().getMessageTemplate(),
                   equalTo("{task.deadline.future}"));
    }
}
