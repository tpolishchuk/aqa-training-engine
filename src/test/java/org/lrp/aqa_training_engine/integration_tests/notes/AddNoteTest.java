package org.lrp.aqa_training_engine.integration_tests.notes;

import lombok.SneakyThrows;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AddNoteTest extends AbstractNoteTest {

    @SneakyThrows
    @Test
    public void whenValidNoteIsAddedThenItIsSavedInDb() {
        User user = randomEntryGenerator.generateRandomUser();
        userService.save(user);

        Note note = randomEntryGenerator.generateRandomNote();

        mockMvc.perform(post("/notes/add")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(convertNoteToEncodedUrlParams(note))
                                .with(csrf())
                                .with(SecurityMockMvcRequestPostProcessors.user(user.getUsername())))
               .andExpect(status().is(302))
               .andExpect(view().name("redirect:/notes"))
               .andExpect(flash().attributeCount(0));

        Set<Note> foundNotes = noteService.findByUserUuid(user.getUuid().toString());

        assertThat(foundNotes.size(), equalTo(1));
        assertThat(foundNotes.iterator().next().getBody(), equalTo(note.getBody()));
    }

    @SneakyThrows
    @Test
    public void whenInvalidNoteIsAddedThenItIsNotSavedInDb() {
        User user = randomEntryGenerator.generateRandomUser();
        userService.save(user);

        Note note = randomEntryGenerator.generateRandomNote();
        note.setBody(StringUtils.EMPTY);

        mockMvc.perform(post("/notes/add")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(convertNoteToEncodedUrlParams(note))
                                .with(csrf())
                                .with(SecurityMockMvcRequestPostProcessors.user(user.getUsername())))
               .andExpect(status().is(302))
               .andExpect(view().name("redirect:/notes"))
               .andExpect(flash().attributeCount(3))
               .andExpect(flash().attribute("reopenAddNoteModal", true))
               .andExpect(flash().attributeExists("note"))
               .andExpect(flash().attributeExists("lastFailedNoteErrors"));

        Set<Note> foundNotes = noteService.findByUserUuid(user.getUuid().toString());

        assertThat(foundNotes.size(), equalTo(0));
    }

    @SneakyThrows
    @Test
    public void whenGenerateRandomNotesThenUserShouldBeAssociated() {
        User user = randomEntryGenerator.generateRandomUser();
        userService.save(user);

        int amount = 3;

        Map<String, String> params = new HashMap<>();
        params.put("amount", String.valueOf(amount));

        mockMvc.perform(post("/api/notes/generate-random-entries")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(urlEncoderUtil.convertToEncodedUrl(params))
                                .with(csrf())
                                .with(SecurityMockMvcRequestPostProcessors.user(user.getUsername())))
               .andExpect(status().isNoContent());

        User foundUser = userService.findByUsername(user.getUsername());
        assertThat(foundUser, notNullValue());

        Set<Note> foundNotesOfUser = noteService.findByUserUuid(user.getUuid().toString());
        assertThat(foundNotesOfUser.size(), equalTo(3));
    }
}
