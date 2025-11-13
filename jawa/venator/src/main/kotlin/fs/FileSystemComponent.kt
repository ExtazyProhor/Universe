package ru.prohor.universe.venator.fs

import java.nio.file.Path
import java.util.concurrent.locks.ReentrantLock

abstract class FileSystemComponent(val path: Path) {
    private val lock = ReentrantLock()

    fun <T> tryPerform(operation: (path: Path) -> T): T? {
        if (!lock.tryLock()) return null
        try {
            return operation.invoke(path)
        } finally {
            lock.unlock()
        }
    }

    fun <T> perform(operation: (path: Path) -> T): T {
        lock.lock()
        try {
            return operation.invoke(path)
        } finally {
            lock.unlock()
        }
    }
}
