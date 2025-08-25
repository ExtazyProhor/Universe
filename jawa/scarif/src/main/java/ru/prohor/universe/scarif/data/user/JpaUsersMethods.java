package ru.prohor.universe.scarif.data.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaUsersMethods extends JpaRepository<UserDto, Long> {
    Optional<UserDto> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<UserDto> findByUsername(String username);

    boolean existsByUsername(String username);
}
