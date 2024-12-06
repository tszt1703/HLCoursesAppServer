package org.example.hlcoursesappserver.mapper;

import org.example.hlcoursesappserver.dto.UserDTO;
import org.example.hlcoursesappserver.model.Listener;
import org.example.hlcoursesappserver.model.Specialist;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapper {

    // Convert UserDTO to Specialist
    public Specialist toSpecialist(UserDTO userDTO) {
        Specialist specialist = new Specialist();
        specialist.setFirstName(userDTO.getFirstName());
        specialist.setLastName(userDTO.getLastName());
        specialist.setEmail(userDTO.getEmail());
        specialist.setProfilePhotoUrl(userDTO.getProfilePhotoUrl());
        specialist.setBirthDate(userDTO.getBirthDate());

        // Only set Specialist-specific fields if they are not null
        if ("Specialist".equals(userDTO.getRole())) {
            specialist.setDescription(userDTO.getDescription());
            specialist.setCertificationDocumentUrl(userDTO.getCertificationDocumentUrl());
        }
        return specialist;
    }

    // Convert UserDTO to Listener
    public Listener toListener(UserDTO userDTO) {
        Listener listener = new Listener();
        listener.setFirstName(userDTO.getFirstName());
        listener.setLastName(userDTO.getLastName());
        listener.setEmail(userDTO.getEmail());
        listener.setProfilePhotoUrl(userDTO.getProfilePhotoUrl());
        listener.setBirthDate(userDTO.getBirthDate());
        return listener;
    }

    // Convert Specialist to UserDTO
    public UserDTO toUserDTO(Specialist specialist, String role) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(specialist.getSpecialistId());
        userDTO.setEmail(specialist.getEmail());
        userDTO.setRole(role);
        userDTO.setFirstName(specialist.getFirstName());
        userDTO.setLastName(specialist.getLastName());
        userDTO.setProfilePhotoUrl(specialist.getProfilePhotoUrl());
        userDTO.setBirthDate(specialist.getBirthDate());
        userDTO.setDescription(specialist.getDescription());
        userDTO.setCertificationDocumentUrl(specialist.getCertificationDocumentUrl());
        return userDTO;
    }

    // Convert Listener to UserDTO
    public UserDTO toUserDTO(Listener listener, String role) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(listener.getListenerId());
        userDTO.setEmail(listener.getEmail());
        userDTO.setRole(role);
        userDTO.setFirstName(listener.getFirstName());
        userDTO.setLastName(listener.getLastName());
        userDTO.setProfilePhotoUrl(listener.getProfilePhotoUrl());
        userDTO.setBirthDate(listener.getBirthDate());
        return userDTO;
    }

    // Convert List of Specialists and Listeners to UserDTO List
    public List<UserDTO> toUserDTOList(List<Specialist> specialists, List<Listener> listeners) {
        List<UserDTO> userDTOList = new ArrayList<>();
        for (Specialist specialist : specialists) {
            userDTOList.add(toUserDTO(specialist, "Specialist"));
        }
        for (Listener listener : listeners) {
            userDTOList.add(toUserDTO(listener, "Listener"));
        }
        return userDTOList;
    }
}



