package ru.prohor.universe.venator.fs

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.file.Path

@Component
class Repository(
    @Value($$"${UNIVERSE_HOME}") universeHome: String
) : FileSystemComponent(Path.of(universeHome))
