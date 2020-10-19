package org.lrp.aqa_training_engine.integration_tests;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lrp.aqa_training_engine.model.User;
import org.lrp.aqa_training_engine.service.UserService;
import org.lrp.aqa_training_engine.utils.RandomEntryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StaticResourcesAccessTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected UserService userService;

    @Autowired
    protected RandomEntryGenerator randomEntryGenerator;

    private User user;

    //TODO: investigate @BeforeTestClass failure
    public User getUser() {
        if (user == null) {
            user = randomEntryGenerator.generateRandomUser();
            userService.save(user);
        }
        return user;
    }

    @SneakyThrows
    @Test
    public void whenUnauthorizedThenCanObserveHarold() {
        mockMvc.perform(get("/img/hide-the-pain-harold.jpg"))
               .andExpect(status().isOk())
               .andExpect(content().contentType("image/jpeg"));
    }

    @SneakyThrows
    @Test
    public void whenAuthorizedThenCanObserveHarold() {
        mockMvc.perform(get("/img/hide-the-pain-harold.jpg")
                                .with(csrf())
                                .with(SecurityMockMvcRequestPostProcessors.user(getUser().getUsername())))
               .andExpect(status().isOk())
               .andExpect(content().contentType("image/jpeg"));
    }

    @SneakyThrows
    @Test
    public void whenUnauthorizedThenCanObserveFavicon() {
        mockMvc.perform(get("/favicon.ico"))
               .andExpect(status().isOk())
               .andExpect(content().contentType("image/x-icon"));
    }

    @SneakyThrows
    @Test
    public void whenAuthorizedThenCanObserveFavicon() {
        mockMvc.perform(get("/favicon.ico")
                                .with(csrf())
                                .with(SecurityMockMvcRequestPostProcessors.user(getUser().getUsername())))
               .andExpect(status().isOk())
               .andExpect(content().contentType("image/x-icon"));
    }

}
