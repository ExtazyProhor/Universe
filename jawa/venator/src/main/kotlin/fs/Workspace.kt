package ru.prohor.universe.venator.fs

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.file.Path

@Component
class Workspace(
    @Value($$"${UNIVERSE_WORKSPACE}") universeHome: String
) : FileSystemComponent(Path.of(universeHome))
