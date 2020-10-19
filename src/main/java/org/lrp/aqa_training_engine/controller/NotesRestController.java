package org.lrp.aqa_training_engine.controller;

import org.lrp.aqa_training_engine.model.Note;
import org.lrp.aqa_training_engine.model.User;
import org.lrp.aqa_training_engine.service.NoteService;
import org.lrp.aqa_training_engine.service.UserService;
import org.lrp.aqa_training_engine.utils.RandomEntryGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotesRestController {

    @Autowired
    private NoteService noteService;

    @Autowired
    private UserService userService;

    @Autowired
    private RandomEntryGenerator randomEntryGenerator;

    private Logger logger = LoggerFactory.getLogger(NotesRestController.class);

    @PostMapping("/api/notes/generate-random-entries")
    public ResponseEntity<Object> generateRandomEntries(@RequestParam("amount") int amount,
                                                        @CurrentSecurityContext(expression = "authentication.name")
                                                                String username) {

        while (amount > 0) {
            User user = userService.findByUsername(username);
            Note note = randomEntryGenerator.generateRandomNote();

            note.setUser(user);

            noteService.save(note);

            logger.info("Added note {} for user {}", note.getUuid(), username);

            amount--;
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/api/notes/delete-all-user-notes")
    public ResponseEntity<Object> deleteAllUserNotes(@CurrentSecurityContext(expression = "authentication.name")
                                                             String username) {

        User user = userService.findByUsername(username);

        noteService.deleteByUserUuid(user.getUuid().toString());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
