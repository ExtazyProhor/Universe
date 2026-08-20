package ru.prohor.universe.scarif.data.refresh;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaRefreshTokenMethods extends JpaRepository<RefreshTokenDto, Long> {}
