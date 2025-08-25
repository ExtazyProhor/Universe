package ru.prohor.universe.scarif.data.session;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface JpaSessionMethods extends JpaRepository<SessionDto, Long> {
    List<SessionDto> findByUserIdAndClosedFalseAndExpiresAtAfter(long userId, Instant now);
}
