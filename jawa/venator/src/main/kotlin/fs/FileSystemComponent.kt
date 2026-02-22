package ru.prohor.universe.venator.fs

import java.nio.file.Path
import java.util.concurrent.locks.ReentrantLock

abstract class FileSystemComponent(val path: Path) {
    private val lock = ReentrantLock()

    fun tryPerform(operation: (path: Path) -> Unit, timeout: Timeout? = null): Boolean {
        if (timeout?.let { !lock.tryLock(it.timeout, it.unit) } ?: !lock.tryLock()) return false
        try {
            operation.invoke(path)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            // TODO log e
            return false
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
