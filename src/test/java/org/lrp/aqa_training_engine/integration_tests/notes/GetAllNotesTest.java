package org.lrp.aqa_training_engine.integration_tests.notes;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lrp.aqa_training_engine.model.Note;
import org.lrp.aqa_training_engine.model.User;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class GetAllNotesTest extends AbstractNoteTest {

    @SneakyThrows
    @Test
    public void whenUnauthorizedThenCannotObserveNotes() {
        mockMvc.perform(get("/notes")
                                .with(csrf()))
               .andExpect(status().is(302))
               .andExpect(redirectedUrl("http://localhost/login"));
    }

    @SneakyThrows
    @Test
    public void whenAuthorizedThenCanObserveNotes() {
        User user = randomEntryGenerator.generateRandomUser();
        userService.save(user);

        mockMvc.perform(get("/notes")
                                .with(csrf())
                                .with(SecurityMockMvcRequestPostProcessors.user(user.getUsername())))
               .andExpect(status().isOk())
               .andExpect(view().name("notes"))
               .andExpect(model().attributeExists("notesScope",
                                                  "note"))
               .andExpect(flash().attributeCount(0));
    }

    @SneakyThrows
    @Test
    public void whenGetAllNotesThenNewNotesScopeIsValid() {
        User user = randomEntryGenerator.generateRandomUser();
        userService.save(user);

        Note note1 = randomEntryGenerator.generateRandomNote();
        note1.setUser(user);
        noteService.save(note1);

        Note note2 = randomEntryGenerator.generateRandomNote();
        note2.setUser(user);
        noteService.save(note2);

        Set<Note> expectedNotes = new HashSet<>();
        expectedNotes.add(note1);
        expectedNotes.add(note2);

        MvcResult mvcResult = mockMvc.perform(get("/notes")
                                                      .with(csrf())
                                                      .with(SecurityMockMvcRequestPostProcessors.user(user.getUsername())))
                                     .andExpect(status().isOk())
                                     .andExpect(view().name("notes"))
                                     .andExpect(flash().attributeCount(0))
                                     .andReturn();

        Set<Note> actualNotes = (Set<Note>) mvcResult.getModelAndView().getModel().get("notesScope");
        assertThat(actualNotes, equalTo(expectedNotes));
    }
}
