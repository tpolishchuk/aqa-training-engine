package org.lrp.aqa_training_engine.service;

import org.lrp.aqa_training_engine.model.Note;

import java.util.Optional;
import java.util.Set;

public interface NoteService {

    void save(Note note);

    void delete(Note note);

    void deleteByUserUuid(String userUuid);

    void deleteNotes(Set<Note> notes);

    Optional<Note> findByUuid(String uuid);

    Set<Note> findByUserUuid(String userUuid);
}
