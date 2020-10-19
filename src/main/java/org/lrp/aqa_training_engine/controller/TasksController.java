package org.lrp.aqa_training_engine.controller;

import org.lrp.aqa_training_engine.model.State;
import org.lrp.aqa_training_engine.model.Task;
import org.lrp.aqa_training_engine.model.User;
import org.lrp.aqa_training_engine.service.TaskService;
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
public class TasksController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @GetMapping("/tasks")
    public String getTasks(Model model,
                           HttpServletRequest request) {
        String username = request.getUserPrincipal().getName();

        model.addAttribute("newTasksScope",
                           taskService.findByUsernameAndState(username, State.NEW));
        model.addAttribute("inProgressTasksScope",
                           taskService.findByUsernameAndState(username, State.IN_PROGRESS));
        model.addAttribute("doneTasksScope",
                           taskService.findByUsernameAndState(username, State.DONE));

        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);

        if (inputFlashMap != null &&
            inputFlashMap.containsKey("reopenAddTaskModal") &&
            (Boolean) inputFlashMap.get("reopenAddTaskModal")) {
            model.addAttribute("reopenAddTaskModal", true);
            model.addAttribute("task", inputFlashMap.get("task"));
            model.addAttribute("lastFailedTaskErrors", inputFlashMap.get("lastFailedTaskErrors"));
        } else {
            model.addAttribute("task", Task.builder().build());
        }

        return "tasks";
    }

    @PostMapping("/tasks/add")
    public String addTask(Principal principal,
                          @Valid @ModelAttribute("task") Task task,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes) {
        if (bindingResult.getErrorCount() > 0) {
            redirectAttributes.addFlashAttribute("task", task);
            redirectAttributes.addFlashAttribute("lastFailedTaskErrors", bindingResult);
            redirectAttributes.addFlashAttribute("reopenAddTaskModal", true);
            return "redirect:/tasks";
        }

        User user = userService.findByUsername(principal.getName());
        user.getTasks().add(task);
        task.getUsers().add(user);

        taskService.save(task);

        return "redirect:/tasks";
    }

    @PostMapping("/tasks/edit")
    public String editTask(@Valid @ModelAttribute("task") Task task,
                           Errors errors,
                           RedirectAttributes redirectAttributes) {
        if (errors != null && errors.getErrorCount() > 0) {
            redirectAttributes.addFlashAttribute("task", task);
            redirectAttributes.addFlashAttribute("lastFailedTaskErrors", errors);
            return "redirect:/edit-task?uuid=" + task.getUuid();
        }

        taskService.save(task);

        return "redirect:/tasks";
    }

    @PostMapping("/tasks/edit-state")
    public String editTaskState(@RequestParam("task_uuid") String taskUuid,
                                @RequestParam("new_state") String newState) {
        Optional<Task> task = taskService.findByUuid(taskUuid);

        if (task.isPresent()) {
            task.get().setState(newState);
            taskService.save(task.get());
        }

        return "redirect:/tasks";
    }

    @GetMapping("/edit-task")
    public String getEditTaskView(Model model,
                                  @RequestParam("uuid") String uuid) {
        if (model.containsAttribute("task")) {
            return "edit-task";
        }

        Optional<Task> task = taskService.findByUuid(uuid);
        model.addAttribute("task", task.get());

        return "edit-task";
    }

    @PostMapping("/tasks/delete")
    public String deleteTask(@RequestParam("task_uuid") String taskUuid,
                             Principal principal) {
        Optional<Task> task = taskService.findByUuid(taskUuid);

        if (task.isPresent()) {
            taskService.deleteTask(principal.getName(), taskUuid);

            if (userService.getAssociatedUsers(taskUuid).isEmpty()) {
                taskService.delete(task.get());
            }
        }

        return "redirect:/tasks";
    }
}
