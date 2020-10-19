package org.lrp.aqa_training_engine.repository.validation;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.lrp.aqa_training_engine.model.Note;
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
public class NoteBodyValidationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RandomEntryGenerator randomEntryGenerator;

    @Test
    public void whenSaveNoteWithEmptyBodyThenReturnError() {
        Note note = randomEntryGenerator.generateRandomNote();
        note.setBody(StringUtils.EMPTY);

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            entityManager.persist(note);
            entityManager.flush();
        });

        assertThat(exception.getConstraintViolations().size(), equalTo(1));
        assertThat(exception.getConstraintViolations().iterator().next().getMessageTemplate(),
                   equalTo("{note.body.not_blank}"));
    }

    @Test
    public void whenSaveNoteWithBlankBodyThenReturnError() {
        Note note = randomEntryGenerator.generateRandomNote();
        note.setBody(" ");

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            entityManager.persist(note);
            entityManager.flush();
        });

        assertThat(exception.getConstraintViolations().size(), equalTo(1));
        assertThat(exception.getConstraintViolations().iterator().next().getMessageTemplate(),
                   equalTo("{note.body.not_blank}"));
    }

    @Test
    public void whenSaveNoteWithTooLongBodyThenReturnError() {
        Note note = randomEntryGenerator.generateRandomNote();
        note.setBody(RandomStringUtils.randomAlphabetic(501));

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            entityManager.persist(note);
            entityManager.flush();
        });

        assertThat(exception.getConstraintViolations().size(), equalTo(1));
        assertThat(exception.getConstraintViolations().iterator().next().getMessageTemplate(),
                   equalTo("{note.body.size}"));
    }
}
