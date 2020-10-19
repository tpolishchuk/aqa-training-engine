package org.lrp.aqa_training_engine.integration_tests.notes;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lrp.aqa_training_engine.model.Note;
import org.lrp.aqa_training_engine.model.User;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DeleteNoteTest extends AbstractNoteTest {

    @SneakyThrows
    @Test
    public void whenNoteIsDeletedThenItDoesNotExistInDbButUserStillExists() {
        User user = randomEntryGenerator.generateRandomUser();
        userService.save(user);

        Note note = randomEntryGenerator.generateRandomNote();
        note.setUser(user);
        noteService.save(note);

        Map<String, String> params = new HashMap<>();
        params.put("note_uuid", note.getUuid().toString());

        mockMvc.perform(post("/notes/delete")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(urlEncoderUtil.convertToEncodedUrl(params))
                                .with(csrf())
                                .with(SecurityMockMvcRequestPostProcessors.user(user.getUsername())))
               .andExpect(status().is(302))
               .andExpect(view().name("redirect:/notes"))
               .andExpect(flash().attributeCount(0));

        User foundUser = userService.findByUsername(user.getUsername());
        assertThat(foundUser, notNullValue());

        Set<Note> foundNotesOfUser = noteService.findByUserUuid(user.getUuid().toString());
        assertThat(foundNotesOfUser.size(), equalTo(0));

        Optional<Note> foundNoteByUuid = noteService.findByUuid(note.getUuid().toString());
        assertThat(foundNoteByUuid.isPresent(), is(false));
    }

    @SneakyThrows
    @Test
    public void whenDeleteAllNotesThenUserShouldHaveNoAssociatedNotes() {
        User user = randomEntryGenerator.generateRandomUser();
        userService.save(user);

        Note note = randomEntryGenerator.generateRandomNote();
        note.setUser(user);
        noteService.save(note);

        mockMvc.perform(delete("/api/notes/delete-all-user-notes")
                                .with(csrf())
                                .with(SecurityMockMvcRequestPostProcessors.user(user.getUsername())))
               .andExpect(status().isNoContent());

        User foundUser = userService.findByUsername(user.getUsername());
        assertThat(foundUser, notNullValue());

        Set<Note> foundNotesOfUser = noteService.findByUserUuid(user.getUuid().toString());
        assertThat(foundNotesOfUser.size(), equalTo(0));

        Optional<Note> foundNoteByUuid = noteService.findByUuid(note.getUuid().toString());
        assertThat(foundNoteByUuid.isPresent(), is(false));
    }
}
