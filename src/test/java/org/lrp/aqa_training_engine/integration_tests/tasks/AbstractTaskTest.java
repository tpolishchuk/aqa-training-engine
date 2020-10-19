package org.lrp.aqa_training_engine.integration_tests.tasks;

import org.lrp.aqa_training_engine.model.Task;
import org.lrp.aqa_training_engine.service.TaskService;
import org.lrp.aqa_training_engine.service.UserService;
import org.lrp.aqa_training_engine.utils.RandomEntryGenerator;
import org.lrp.aqa_training_engine.utils.UrlEncoderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractTaskTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected UserService userService;

    @Autowired
    protected TaskService taskService;

    @Autowired
    protected RandomEntryGenerator randomEntryGenerator;

    @Autowired
    protected UrlEncoderUtil urlEncoderUtil;

    protected String convertTaskToEncodedUrlParams(Task task) {
        return convertTaskToEncodedUrlParams(task, false);
    }

    protected String convertTaskToEncodedUrlParams(Task task, boolean includeUuid) {
        Map<String, String> params = new HashMap<>();
        params.put("title", task.getTitle());
        params.put("description", task.getDescription());
        params.put("state", task.getState());
        params.put("deadline", new SimpleDateFormat("dd-MM-yyyy HH:mm").format(task.getDeadline()));

        if(includeUuid) {
            params.put("uuid", task.getUuid().toString());
        }

        return urlEncoderUtil.convertToEncodedUrl(params);
    }
}
