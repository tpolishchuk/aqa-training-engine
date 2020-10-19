package org.lrp.aqa_training_engine.integration_tests.notes;

import org.lrp.aqa_training_engine.model.Note;
import org.lrp.aqa_training_engine.service.NoteService;
import org.lrp.aqa_training_engine.service.UserService;
import org.lrp.aqa_training_engine.utils.RandomEntryGenerator;
import org.lrp.aqa_training_engine.utils.UrlEncoderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractNoteTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected UserService userService;

    @Autowired
    protected NoteService noteService;

    @Autowired
    protected RandomEntryGenerator randomEntryGenerator;

    @Autowired
    protected UrlEncoderUtil urlEncoderUtil;

    protected String convertNoteToEncodedUrlParams(Note note) {
        return convertNoteToEncodedUrlParams(note, false);
    }

    protected String convertNoteToEncodedUrlParams(Note note, boolean includeUuid) {
        Map<String, String> params = new HashMap<>();
        params.put("body", note.getBody());

        if(includeUuid) {
            params.put("uuid", note.getUuid().toString());
        }

        return urlEncoderUtil.convertToEncodedUrl(params);
    }
}
