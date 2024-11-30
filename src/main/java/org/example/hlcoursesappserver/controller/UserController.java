package org.example.hlcoursesappserver.controller;

import org.example.hlcoursesappserver.dto.UserDTO;
import org.example.hlcoursesappserver.model.Listener;
import org.example.hlcoursesappserver.model.Specialist;
import org.example.hlcoursesappserver.service.ListenerService;
import org.example.hlcoursesappserver.service.SpecialistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final SpecialistService specialistService;

    private final ListenerService listenerService;

    public UserController(ListenerService listenerService, SpecialistService specialistService) {
        this.listenerService = listenerService;
        this.specialistService = specialistService;
    }

    @GetMapping("/listener/{id}")
    public ResponseEntity<Listener> getListenerById(@PathVariable("id") Long id) {
        Listener listener = listenerService.getListenerById(id);
        return ResponseEntity.ok(listener);
    }

    @GetMapping("/specialist/{id}")
    public ResponseEntity<Specialist> getSpecialistById(@PathVariable("id") Long id) {
        Specialist specialist = specialistService.getSpecialistById(id);
        return ResponseEntity.ok(specialist);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable("id") Long id,
                                              @RequestParam("userType") String userType) {
        if ("specialist".equalsIgnoreCase(userType)) {
            Specialist specialist = specialistService.getSpecialistById(id);
            return ResponseEntity.ok(specialist);
        } else if ("listener".equalsIgnoreCase(userType)) {
            Listener listener = listenerService.getListenerById(id);
            return ResponseEntity.ok(listener);
        }

        return ResponseEntity.status(400).body("Invalid user type");
    }


    // Контроллер для обновления пользователя
    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(
            @PathVariable("id") Long id,
            @RequestBody @Valid UserDTO userDTO,
            @RequestParam("userType") String userType,
            Authentication authentication) {

        // Получаем email пользователя из токена
        String emailFromToken = authentication.getName();

        if ("specialist".equalsIgnoreCase(userType)) {
            if (specialistService.isUserAuthorizedToUpdate(id, emailFromToken)) {
                specialistService.updateSpecialist(id, convertToSpecialist(userDTO));
                return ResponseEntity.ok("Specialist successfully updated.");
            }
        } else if ("listener".equalsIgnoreCase(userType)) {
            if (listenerService.isUserAuthorizedToUpdate(id, emailFromToken)) {
                listenerService.updateListener(id, convertToListener(userDTO));
                return ResponseEntity.ok("Listener successfully updated.");
            }
        }

        return ResponseEntity.status(403).body("You are not authorized to update this user.");
    }

    private Specialist convertToSpecialist(UserDTO userDTO) {
        Specialist specialist = new Specialist();
        specialist.setFirstName(userDTO.getFirstName());
        specialist.setLastName(userDTO.getLastName());
        specialist.setEmail(userDTO.getEmail());
        specialist.setProfilePhotoUrl(userDTO.getProfilePhotoUrl());
        specialist.setDescription(userDTO.getDescription());
        specialist.setCertificationDocumentUrl(userDTO.getCertificationDocumentUrl());
        specialist.setBirthDate(userDTO.getBirthDate());
        return specialist;
    }

    private Listener convertToListener(UserDTO userDTO) {
        Listener listener = new Listener();
        listener.setFirstName(userDTO.getFirstName());
        listener.setLastName(userDTO.getLastName());
        listener.setEmail(userDTO.getEmail());
        listener.setProfilePhotoUrl(userDTO.getProfilePhotoUrl());
        listener.setBirthDate(userDTO.getBirthDate());
        return listener;
    }

    // Удаление пользователя
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @PathVariable("id") Long id,
            @RequestParam("userType") String userType,
            Authentication authentication) {

        // Получаем email пользователя из токена
        String emailFromToken = authentication.getName(); // email, извлеченный из токена

        if ("specialist".equalsIgnoreCase(userType)) {
            if (specialistService.isUserAuthorizedToDelete(id, emailFromToken)) {
                specialistService.deleteSpecialist(id);
                return ResponseEntity.ok("Specialist successfully deleted.");
            }
        } else if ("listener".equalsIgnoreCase(userType)) {
            if (listenerService.isUserAuthorizedToDelete(id, emailFromToken)) {
                listenerService.deleteListener(id);
                return ResponseEntity.ok("Listener successfully deleted.");
            }
        }

        return ResponseEntity.status(403).body("You are not authorized to delete this user.");
    }
}
