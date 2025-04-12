package com.agnelle.backend.entity;

public record UserDTO(Long id, String username, String email, String profilePicture, UserRole role, String bio, String slug) {
}
