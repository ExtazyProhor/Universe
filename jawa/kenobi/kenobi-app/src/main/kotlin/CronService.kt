package ru.prohor.universe.kenobi.app

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import ru.prohor.universe.kenobi.core.Task

@Service
class CronService(private val tasks: List<Task>) {
    @Scheduled(cron = "0 0/1 * * * ?") // TODO
    fun execute() {
        tasks.forEach { task -> task.execute() }
    }
}
