package org.lrp.aqa_training_engine.repository;

import org.lrp.aqa_training_engine.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface NoteRepository extends JpaRepository<Note, UUID> {

    Set<Note> findByUserUuidOrderByUpdatedAtDesc(@Param("user_uuid") UUID userUuid);
}
