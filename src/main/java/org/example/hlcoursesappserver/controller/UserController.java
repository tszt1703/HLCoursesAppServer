package org.example.hlcoursesappserver.controller;

import org.example.hlcoursesappserver.dto.UserDTO;
import org.example.hlcoursesappserver.mapper.UserMapper;
import org.example.hlcoursesappserver.model.Listener;
import org.example.hlcoursesappserver.model.Specialist;
import org.example.hlcoursesappserver.service.ListenerService;
import org.example.hlcoursesappserver.service.SpecialistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final SpecialistService specialistService;
    private final ListenerService listenerService;
    private final UserMapper userMapper;

    @Autowired
    public UserController(SpecialistService specialistService, ListenerService listenerService, UserMapper userMapper) {
        this.specialistService = specialistService;
        this.listenerService = listenerService;
        this.userMapper = userMapper;
    }

    @PostMapping("/specialist")
    public ResponseEntity<UserDTO> createSpecialist(@RequestBody UserDTO userDTO) {
        Specialist specialist = userMapper.toSpecialist(userDTO);
        Specialist createdSpecialist = specialistService.createUser(specialist);
        return ResponseEntity.ok(userMapper.toUserDTO(createdSpecialist, "Specialist"));
    }

    @PostMapping("/listener")
    public ResponseEntity<UserDTO> createListener(@RequestBody UserDTO userDTO) {
        Listener listener = userMapper.toListener(userDTO);
        Listener createdListener = listenerService.createUser(listener);
        return ResponseEntity.ok(userMapper.toUserDTO(createdListener, "Listener"));
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<Specialist> specialists = specialistService.getAllUsers();
        List<Listener> listeners = listenerService.getAllUsers();
        List<UserDTO> users = userMapper.toUserDTOList(specialists, listeners);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        Optional<Specialist> specialistOpt = specialistService.getUserByEmail(email);
        if (specialistOpt.isPresent()) {
            Specialist specialist = specialistOpt.get();
            return ResponseEntity.ok(userMapper.toUserDTO(specialist, "Specialist"));
        }

        Optional<Listener> listenerOpt = listenerService.getUserByEmail(email);
        if (listenerOpt.isPresent()) {
            Listener listener = listenerOpt.get();
            return ResponseEntity.ok(userMapper.toUserDTO(listener, "Listener"));
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody UserDTO userDTO,
            @RequestHeader("email") String email) {

        if (userDTO.getRole() == null || userDTO.getRole().isEmpty()) {
            return ResponseEntity.badRequest().body("Ошибка: Роль пользователя не указана.");
        }

        if (userDTO.getRole().equals("Specialist")) {
            if (!specialistService.isUserAuthorizedToUpdate(id, email)) {
                return ResponseEntity.status(403).build();
            }
            Specialist updatedSpecialist = userMapper.toSpecialist(userDTO);
            specialistService.updateUser(id, updatedSpecialist);
            return ResponseEntity.ok().build();
        } else if (userDTO.getRole().equals("Listener")) {
            if (!listenerService.isUserAuthorizedToUpdate(id, email)) {
                return ResponseEntity.status(403).build();
            }
            Listener updatedListener = userMapper.toListener(userDTO);
            listenerService.updateUser(id, updatedListener);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body("Ошибка: Неверная роль пользователя.");
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            @PathVariable Long id,
            @RequestHeader("email") String email,
            @RequestParam String role) {
        if (role.equals("Specialist")) {
            if (!specialistService.isUserAuthorizedToDelete(id, email)) {
                return ResponseEntity.status(403).build();
            }
            specialistService.deleteUser(id);
            return ResponseEntity.ok().build();
        } else if (role.equals("Listener")) {
            if (!listenerService.isUserAuthorizedToDelete(id, email)) {
                return ResponseEntity.status(403).build();
            }
            listenerService.deleteUser(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
}
