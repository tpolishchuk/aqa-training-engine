package org.lrp.aqa_training_engine.utils;

import com.github.javafaker.Faker;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.lrp.aqa_training_engine.model.Note;
import org.lrp.aqa_training_engine.model.Task;
import org.lrp.aqa_training_engine.model.User;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Component
public class RandomEntryGenerator {

    private Faker faker = new Faker();

    public Task generateRandomTaskInState(String state) {
        String title = new StringBuilder(StringUtils.capitalize(faker.color().name()))
                .append(" ")
                .append(StringUtils.capitalize(faker.animal().name()))
                .append(" ")
                .append(StringUtils.capitalize(faker.name().firstName()))
                .toString();
        String description = faker.lorem().characters(1, 200, true);

        Calendar deadlineCalendar = Calendar.getInstance();
        deadlineCalendar.add(Calendar.DATE, RandomUtils.nextInt(1, 3650));
        deadlineCalendar.add(Calendar.MINUTE, RandomUtils.nextInt(1, 1440));
        Date deadline = deadlineCalendar.getTime();

        return Task.builder()
                   .uuid(UUID.randomUUID())
                   .state(state)
                   .title(title)
                   .description(description)
                   .deadline(deadline)
                   .build();
    }

    public Note generateRandomNote() {
        String body = faker.lorem().characters(1, 500, true);

        return Note.builder()
                   .uuid(UUID.randomUUID())
                   .body(body)
                   .createdAt(new Date())
                   .updatedAt(new Date()).build();
    }

    public User generateRandomUser() {

        return User.builder()
                   .uuid(UUID.randomUUID())
                   .email("user-" + RandomStringUtils.randomNumeric(20) + "@gmail.com")
                   .username(RandomStringUtils.randomAlphabetic(20).toUpperCase())
                   .password("12345678")
                   .passwordConfirm("12345678")
                   .build();
    }
}
