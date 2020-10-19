package org.lrp.aqa_training_engine.integration_tests;

import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RegistrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private RandomEntryGenerator randomEntryGenerator;

    @Autowired
    private UrlEncoderUtil urlEncoderUtil;

    private String convertUserToEncodedUrlParams(User user) {
        Map<String, String> params = new HashMap<>();
        params.put("username", user.getUsername());
        params.put("email", user.getEmail());
        params.put("password", user.getPassword());
        params.put("passwordConfirm", user.getPasswordConfirm());

        return urlEncoderUtil.convertToEncodedUrl(params);
    }

    @SneakyThrows
    @Test
    public void whenRegistrationHappyPathPassesThenUserShouldBeCreated() {
        User user = randomEntryGenerator.generateRandomUser();

        mockMvc.perform(post("/registration")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(convertUserToEncodedUrlParams(user))
                                .with(csrf()))
               .andExpect(status().is(302))
               .andExpect(view().name("redirect:/home"))
               .andExpect(flash().attribute("successfulRegMsg",
                                            "Thanks for the registration! You are successfully logged in"));

        User foundUser = userService.findByUsername(user.getUsername());

        assertThat(foundUser, notNullValue());
        assertThat(foundUser.getUsername(), equalTo(user.getUsername()));
    }

    @SneakyThrows
    @Test
    public void whenPasswordDoesNotMatchConfirmationThenErrorShouldBeReturned() {
        User user = randomEntryGenerator.generateRandomUser();
        user.setPasswordConfirm(RandomStringUtils.randomAlphabetic(10));

        mockMvc.perform(post("/registration")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(convertUserToEncodedUrlParams(user))
                                .with(csrf()))
               .andExpect(status().isOk())
               .andExpect(view().name("registration"))
               .andExpect(model().attribute("generalError",
                                            "Password confirmation and password should be equal"));

        User foundUser = userService.findByUsername(user.getUsername());

        assertThat(foundUser, nullValue());
    }

    @SneakyThrows
    @Test
    public void whenUsernameIsAlreadyRegisteredThenErrorShouldBeReturned() {
        User originalUser = randomEntryGenerator.generateRandomUser();
        userService.save(originalUser);

        User user = randomEntryGenerator.generateRandomUser();
        user.setUsername(originalUser.getUsername());

        mockMvc.perform(post("/registration")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(convertUserToEncodedUrlParams(user))
                                .with(csrf()))
               .andExpect(status().isOk())
               .andExpect(view().name("registration"))
               .andExpect(model().attribute("generalError",
                                            "User with a given username already exists"));
    }
}
