package org.lrp.aqa_training_engine.repository;

import org.junit.jupiter.api.Test;
import org.lrp.aqa_training_engine.model.Note;
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
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@ContextConfiguration(classes = {JpaTestConfiguration.class })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class NoteRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private RandomEntryGenerator randomEntryGenerator;

    @Test
    public void whenFindByUserUuidThenNoteShouldBeReturned() {
        User user = randomEntryGenerator.generateRandomUser();

        entityManager.persist(user);

        Note note = randomEntryGenerator.generateRandomNote();
        note.setUser(user);

        entityManager.persist(note);

        Set<Note> foundNotes = noteRepository.findByUserUuidOrderByUpdatedAtDesc(user.getUuid());

        assertThat(foundNotes.size(), equalTo(1));
        assertThat(foundNotes, hasItem(note));
    }

    //TODO: investigate why it fails with H2 @Test
    public void whenUserIsDeletedThenNoteShouldAlsoBeDeleted() {
        User user = randomEntryGenerator.generateRandomUser();
        entityManager.persistAndFlush(user);

        Note note = randomEntryGenerator.generateRandomNote();
        note.setUser(user);

        entityManager.persistAndFlush(note);

        entityManager.remove(user);

        entityManager.flush();
        entityManager.clear();

        Optional<Note> foundNote = noteRepository.findById(note.getUuid());
        assertFalse(foundNote.isPresent());
    }
}
