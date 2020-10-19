package org.lrp.aqa_training_engine.integration_tests.notes;

import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lrp.aqa_training_engine.model.Note;
import org.lrp.aqa_training_engine.model.User;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EditNoteTest extends AbstractNoteTest {

    @SneakyThrows
    @Test
    public void whenNoteIsUpdatedWithValidDataThenUpdateIsSavedInDb() {
        User user = randomEntryGenerator.generateRandomUser();
        userService.save(user);

        Note note = randomEntryGenerator.generateRandomNote();
        note.setUser(user);
        noteService.save(note);

        String newBody = "New-" + RandomStringUtils.randomNumeric(10);
        note.setBody(newBody);

        mockMvc.perform(post("/notes/edit")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(convertNoteToEncodedUrlParams(note, true))
                                .with(csrf())
                                .with(SecurityMockMvcRequestPostProcessors.user(user.getUsername())))
               .andExpect(status().is(302))
               .andExpect(view().name("redirect:/notes"))
               .andExpect(flash().attributeCount(0));

        Set<Note> foundNotes = noteService.findByUserUuid(user.getUuid().toString());

        assertThat(foundNotes.size(), equalTo(1));
        assertThat(foundNotes.iterator().next().getBody(), equalTo(newBody));
    }

    @SneakyThrows
    @Test
    public void whenNoteIsUpdatedWithInvalidDataThenUpdateIsNotSavedInDb() {
        User user = randomEntryGenerator.generateRandomUser();
        userService.save(user);

        Note note = randomEntryGenerator.generateRandomNote();
        note.setUser(user);
        noteService.save(note);

        String originBody = note.getBody();

        note.setBody(StringUtils.EMPTY);

        mockMvc.perform(post("/notes/edit")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(convertNoteToEncodedUrlParams(note, true))
                                .with(csrf())
                                .with(SecurityMockMvcRequestPostProcessors.user(user.getUsername())))
               .andExpect(status().is(302))
               .andExpect(view().name("redirect:/edit-note?uuid=" + note.getUuid()))
               .andExpect(flash().attributeCount(2))
               .andExpect(flash().attributeExists("note"))
               .andExpect(flash().attributeExists("lastFailedNoteErrors"));

        Set<Note> foundNotes = noteService.findByUserUuid(user.getUuid().toString());

        assertThat(foundNotes.size(), equalTo(1));
        assertThat(foundNotes.iterator().next().getBody(), equalTo(originBody));
    }

    @SneakyThrows
    @Test
    public void whenEditNoteViewIsOpenedThenItShouldContainCorrectNote() {
        User user = randomEntryGenerator.generateRandomUser();
        userService.save(user);

        Note note = randomEntryGenerator.generateRandomNote();
        note.setUser(user);
        noteService.save(note);

        mockMvc.perform(get("/edit-note")
                                .param("uuid", note.getUuid().toString())
                                .with(csrf())
                                .with(SecurityMockMvcRequestPostProcessors.user(user.getUsername())))
               .andExpect(status().isOk())
               .andExpect(view().name("edit-note"))
               .andExpect(model().attribute("note", note));
    }
}
