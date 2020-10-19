package org.lrp.aqa_training_engine.controller;

import org.lrp.aqa_training_engine.model.Note;
import org.lrp.aqa_training_engine.model.User;
import org.lrp.aqa_training_engine.service.NoteService;
import org.lrp.aqa_training_engine.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;

@Controller
public class NotesController {

    @Autowired
    private NoteService noteService;

    @Autowired
    private UserService userService;

    @GetMapping("/notes")
    public String getNotes(Model model,
                           HttpServletRequest request) {
        String username = request.getUserPrincipal().getName();
        User user = userService.findByUsername(username);

        model.addAttribute("notesScope",
                           noteService.findByUserUuid(user.getUuid().toString()));

        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);

        if (inputFlashMap != null &&
            inputFlashMap.containsKey("reopenAddNoteModal") &&
            (Boolean) inputFlashMap.get("reopenAddNoteModal")) {
            model.addAttribute("reopenAddNoteModal", true);
            model.addAttribute("note", inputFlashMap.get("note"));
            model.addAttribute("lastFailedNoteErrors", inputFlashMap.get("lastFailedNoteErrors"));
        } else {
            model.addAttribute("note", Note.builder().build());
        }

        return "notes";
    }

    @PostMapping("/notes/add")
    public String addNote(Principal principal,
                          @Valid @ModelAttribute("note") Note note,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes) {
        if (bindingResult.getErrorCount() > 0) {
            redirectAttributes.addFlashAttribute("note", note);
            redirectAttributes.addFlashAttribute("lastFailedNoteErrors", bindingResult);
            redirectAttributes.addFlashAttribute("reopenAddNoteModal", true);
            return "redirect:/notes";
        }

        User user = userService.findByUsername(principal.getName());
        note.setUser(user);

        noteService.save(note);

        return "redirect:/notes";
    }

    @PostMapping("/notes/edit")
    public String editNote(@Valid @ModelAttribute("note") Note note,
                           Errors errors,
                           RedirectAttributes redirectAttributes) {
        if (errors != null && errors.getErrorCount() > 0) {
            redirectAttributes.addFlashAttribute("note", note);
            redirectAttributes.addFlashAttribute("lastFailedNoteErrors", errors);
            return "redirect:/edit-note?uuid=" + note.getUuid();
        }

        Optional<Note> noteForUpdate = noteService.findByUuid(note.getUuid().toString());

        if(noteForUpdate.isPresent()) {
            noteForUpdate.get().setBody(note.getBody());
            noteService.save(noteForUpdate.get());
        }

        return "redirect:/notes";
    }

    @GetMapping("/edit-note")
    public String getEditNoteView(Model model,
                                  @RequestParam("uuid") String uuid) {
        if (model.containsAttribute("note")) {
            return "edit-note";
        }

        Optional<Note> note = noteService.findByUuid(uuid);
        model.addAttribute("note", note.get());

        return "edit-note";
    }

    @PostMapping("/notes/delete")
    public String deleteNote(@RequestParam("note_uuid") String noteUuid) {
        Optional<Note> note = noteService.findByUuid(noteUuid);

        note.ifPresent(value -> noteService.delete(value));

        return "redirect:/notes";
    }
}
