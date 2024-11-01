package org.example.hlcoursesappserver.service;

import org.example.hlcoursesappserver.dto.AuthRequest;
import org.example.hlcoursesappserver.dto.UserDTO;
import org.example.hlcoursesappserver.model.Listener;
import org.example.hlcoursesappserver.model.Specialist;
import org.example.hlcoursesappserver.repository.ListenerRepository;
import org.example.hlcoursesappserver.repository.SpecialistRepository;
import org.example.hlcoursesappserver.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
//
//    @Autowired
//    private AuthenticationManager authenticationManager;
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @Autowired
//    private ListenerRepository listenerRepository;
//
//    @Autowired
//    private SpecialistRepository specialistRepository;
//
//    public String authenticate(AuthRequest authRequest) {
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
//
//        UserDetails userDetails = loadUserByUsername(authRequest.getUsername());
//        return jwtUtil.generateToken(userDetails);
//    }
//
//    public UserDTO registerListener(Listener listener) {
//        listener = listenerRepository.save(listener);
//        return new UserDTO(listener.getId(), listener.getUsername(), "Listener");
//    }
//
//    public UserDTO registerSpecialist(Specialist specialist) {
//        specialist = specialistRepository.save(specialist);
//        return new UserDTO(specialist.getId(), specialist.getUsername(), "Specialist");
//    }
//
//    public UserDetails loadUserByUsername(String username) {
//        Listener listener = listenerRepository.findByUsername(username).orElse(null);
//        if (listener != null) {
//            return listener;
//        }
//
//        Specialist specialist = specialistRepository.findByUsername(username).orElse(null);
//        if (specialist != null) {
//            return specialist;
//        }
//
//        throw new UsernameNotFoundException("User not found");
//    }
}
