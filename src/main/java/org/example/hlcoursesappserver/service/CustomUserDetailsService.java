package org.example.hlcoursesappserver.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public abstract class CustomUserDetailsService implements UserDetailsService {
//
//    @Autowired
//    private SpecialistRepository specialistRepository;
//
//    @Autowired
//    private ListenerRepository listenerRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Specialist specialist = specialistRepository.findByUsername(username).orElse(null);
//        if (specialist != null) {
//            return specialist;
//        }
//
//        Listener listener = listenerRepository.findByUsername(username).orElse(null);
//        if (listener != null) {
//            return listener;
//        }
//
//        throw new UsernameNotFoundException("User not found with username: " + username);
//    }
}
