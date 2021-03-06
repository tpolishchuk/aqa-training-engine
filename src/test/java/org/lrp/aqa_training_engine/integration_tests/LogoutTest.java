package org.lrp.aqa_training_engine.integration_tests;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lrp.aqa_training_engine.model.User;
import org.lrp.aqa_training_engine.service.UserService;
import org.lrp.aqa_training_engine.utils.RandomEntryGenerator;
import org.lrp.aqa_training_engine.utils.UrlEncoderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LogoutTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private RandomEntryGenerator randomEntryGenerator;

    @Autowired
    private UrlEncoderUtil urlEncoderUtil;

    @SneakyThrows
    @Test
    public void whenUserExistsThenShouldBeSuccessfullyLoggedIn() {
        User user = randomEntryGenerator.generateRandomUser();
        userService.save(user);

        mockMvc.perform(post("/logout")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .with(SecurityMockMvcRequestPostProcessors.user(user.getUsername()))
                                .with(csrf()))
               .andExpect(status().is(302))
               .andExpect(redirectedUrl("/login?logout"));
    }
}
