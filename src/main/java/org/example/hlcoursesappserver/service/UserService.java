package org.example.hlcoursesappserver.service;

import java.util.List;
import java.util.Optional;

public interface UserService<T> {
    T createUser(T user);
    List<T> getAllUsers();
    Optional<T> getUserByEmail(String email);
    T getUserById(Long id);
    void updateUser(Long id, T userDetails);
    void deleteUser(Long id);
    boolean isUserAuthorizedToUpdate(Long id, String email);
    boolean isUserAuthorizedToDelete(Long id, String email);
}

