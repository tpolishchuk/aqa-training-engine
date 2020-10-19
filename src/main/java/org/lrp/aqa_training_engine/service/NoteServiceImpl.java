package org.lrp.aqa_training_engine.service;

import org.lrp.aqa_training_engine.model.Note;
import org.lrp.aqa_training_engine.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class NoteServiceImpl implements NoteService {

    @Autowired
    private NoteRepository noteRepository;

    @Override
    public void save(Note note) {
        if(note.getUuid() == null) {
            note.setUuid(UUID.randomUUID());
        }

        noteRepository.save(note);
    }

    @Override
    public void delete(Note note) {
        noteRepository.delete(note);
    }

    @Override
    public void deleteByUserUuid(String userUuid) {
        Set<Note> notes = findByUserUuid(userUuid);
        deleteNotes(notes);
    }

    @Override
    public void deleteNotes(Set<Note> notes) {
        noteRepository.deleteInBatch(notes);
    }

    @Override
    public Optional<Note> findByUuid(String uuid) {
       return noteRepository.findById(UUID.fromString(uuid));
    }

    @Override
    public Set<Note> findByUserUuid(String userUuid) {
        return noteRepository.findByUserUuidOrderByUpdatedAtDesc(UUID.fromString(userUuid));
    }
}
