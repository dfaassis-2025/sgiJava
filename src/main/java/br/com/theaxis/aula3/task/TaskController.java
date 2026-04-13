package br.com.theaxis.aula3.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.theaxis.aula3.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        System.out.println("Chegou no controller ...  " + request.getAttribute("idUser"));
        var idUser = request.getAttribute("idUser");

        var currentDate = LocalDateTime.now();
        if (currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início deve ser no futuro.");
        }

        if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A data de início deve ser menor que data de termino.");
        }

        taskModel.setIdUser((UUID) idUser);
        var task = this.taskRepository.save(taskModel);
        return (ResponseEntity.status(HttpStatus.OK).body(task));

    }

    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request) {
        var idUser = request.getAttribute("idUser");
        var tasks = this.taskRepository.findByIdUser((UUID) idUser);
        return tasks;

    }

    // http://localhost:8080/tasks/8cf697bc-6bfb-40df-9a04-2946b3c6568a
    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody TaskModel taskModel, @PathVariable UUID id, HttpServletRequest request) {
        var task = this.taskRepository.findById(id).orElse(null);

        if (task == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Tarefa não encontrada");
        }

        var idUser = request.getAttribute("idUser");

        //if (!task.getIdUser().equals(idUser)) {
        if (!task.getIdUser().equals(idUser)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Tarefa não pertence ao usuário");
        }

        Utils.copyNonNullProperties(taskModel, task);
        var taskUpdated = this.taskRepository.save(task);
        // MEUS AJUSTES PARA ATUALIZAÇÃO DE TAREFA, PARA NÃO SOBRESCREVER OS CAMPOS COM NULL
        // var tasks = this.taskRepository.findById(id);
// 
        // utils.copyNonNullProperties(idUser, tasks);
// 
        // if (tasks.isEmpty()) {
        //     throw new RuntimeException("Tarefa não encontrada");
        // }
        // var dbidUser = tasks.get().getIdUser();
        // if (!dbidUser.equals(idUser)) {
        //     throw new RuntimeException("Tarefa não pertence ao usuário");
        // }
        // var starAt = tasks.get().getStartAt();
        // var endAt = tasks.get().getEndAt();
        // var description = tasks.get().getDescription();
        // var priority = tasks.get().getPriority();
        // var createdAt = tasks.get().getCreatedAt();
        // var status = tasks.get().getStatus();
        // var title = tasks.get().getTitle();
        //  if (taskModel.getStatus() != null) {
        //     status = taskModel.getStatus(); 
        // }   
        // if (taskModel.getTitle() != null) {
        //     title = taskModel.getTitle();
        // }
        // if (taskModel.getStartAt() != null) {
        //     starAt = taskModel.getStartAt();
        // }
        // if (taskModel.getEndAt() != null) {
        //     endAt = taskModel.getEndAt();
        // }
        // if (taskModel.getDescription() != null) {
        //     description = taskModel.getDescription();
        // }
        // if (taskModel.getPriority() != null) {
        //     priority = taskModel.getPriority();
        // }
        // if (taskModel.getCreatedAt() != null) {
        //     createdAt = taskModel.getCreatedAt();
        // }
        // taskModel.setStartAt(starAt);
        // taskModel.setEndAt(endAt);
        // taskModel.setDescription(description);
        // taskModel.setPriority(priority);
        // taskModel.setCreatedAt(createdAt);
        // taskModel.setStatus(status);
        // taskModel.setTitle(title);
        //taskModel.setIdUser((UUID) idUser);
        //taskModel.setId(id);
        return (ResponseEntity.ok().body(taskUpdated));
    }

}
