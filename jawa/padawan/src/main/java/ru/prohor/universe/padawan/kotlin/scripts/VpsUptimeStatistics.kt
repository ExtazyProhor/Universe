package ru.prohor.universe.padawan.kotlin.scripts

import ru.prohor.universe.jocasta.core.utils.FileSystemUtils
import ru.prohor.universe.padawan.PadawanKt
import java.nio.file.Path
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.readLines

private val LOG_REGEX = Regex("""^(\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2})\s+(\S+)\s+:\s+(\S+)$""")

fun main() {
    val benchmarkDir = FileSystemUtils.downloads().asPath().resolve("benchmark")
    val servers = parseServers(benchmarkDir)
    val collectedLogs = benchmarkDir.resolve("collected_logs")

    val logs = mutableMapOf<String, MutableMap<String, Ping>>()

    for (server in servers) {
        val serverDir = collectedLogs.resolve(server.name)
        if (!serverDir.toFile().exists()) {
            println("WARNING: нет директории логов для ${server.name}")
            continue
        }

        val serverLogs = logs.getOrPut(server.name) { mutableMapOf() }

        serverDir.listDirectoryEntries()
            .filter { path ->
                path.fileName.toString() != "ping_2026-08-11.log"
            }
            .forEach { path ->
                path.readLines()
                    .asSequence()
                    .filter { it.isNotBlank() }
                    .forEach { line ->
                        val match = LOG_REGEX.matchEntire(line.trim()) ?: return@forEach

                        val timestamp = match.groupValues[1].substring(0, 16)
                        val targetIp = match.groupValues[2]
                        val value = match.groupValues[3]

                        serverLogs["$timestamp|$targetIp"] = Ping(
                            timestamp = timestamp,
                            targetIp = targetIp,
                            latency = value.toDoubleOrNull()
                        )
                    }
            }
    }

    val allMinutes = logs.values
        .flatMap { it.values }
        .map { it.timestamp }
        .distinct()
        .sorted()

    if (allMinutes.isEmpty()) {
        println("Логи не найдены.")
        return
    }

    println()
    println("=".repeat(100))
    println("ОБЩАЯ СТАТИСТИКА")
    println("=".repeat(100))

    for (server in servers) {
        val expectedTargets = expectedTargets(server, servers)
        val serverLog = logs[server.name].orEmpty()

        val activeMinutes = allMinutes.count { minute ->
            expectedTargets.any { target ->
                serverLog["$minute|${target.ip}"] != null
            }
        }

        val downMinutes = allMinutes.count { minute ->
            expectedTargets.isNotEmpty() &&
                    expectedTargets.none { target ->
                        serverLog["$minute|${target.ip}"] != null
                    }
        }

        val totalMinutes = allMinutes.size
        val uptime = if (totalMinutes == 0) {
            0.0
        } else {
            activeMinutes * 100.0 / totalMinutes
        }

        println()
        println("${server.name} (${server.ip})")
        println("-".repeat(100))
        println("  Всего минут наблюдения : $totalMinutes")
        println("  Работал                : $activeMinutes мин")
        println("  Не пинговал никого     : $downMinutes мин")
        println("  Аптайм                 : ${"%.3f".format(uptime)}%")

        if (downMinutes > 0) {
            val intervals = findIntervals(
                allMinutes.filter { minute ->
                    expectedTargets.none { target ->
                        serverLog["$minute|${target.ip}"] != null
                    }
                }
            )

            println("  Периоды падения:")
            intervals.forEach {
                println("    ${formatInterval(it)}")
            }
        }
    }

    println()
    println()
    println("=".repeat(100))
    println("СЕТЕВАЯ ДОСТУПНОСТЬ ПО НАПРАВЛЕНИЯМ")
    println("=".repeat(100))

    for (source in servers) {
        val targets = expectedTargets(source, servers)
        val sourceLog = logs[source.name].orEmpty()

        if (targets.isEmpty()) {
            continue
        }

        println()
        println("${source.name} (${source.ip})")
        println("-".repeat(100))

        for (target in targets) {
            val results = allMinutes.map { minute ->
                sourceLog["$minute|${target.ip}"]
            }

            val observed = results.count { it != null }
            val successful = results.count { it?.latency != null }
            val unavailable = results.count {
                it != null && it.latency == null
            }

            val networkUnavailable = allMinutes.count { minute ->
                val ping = sourceLog["$minute|${target.ip}"]

                ping != null &&
                        ping.latency == null &&
                        expectedTargets(source, servers).any {
                            sourceLog["$minute|${it.ip}"]?.latency != null
                        }
            }

            val availability = if (observed == 0) {
                0.0
            } else {
                successful * 100.0 / observed
            }

            println(
                "  -> %-12s | успешных: %5d | '-' : %5d | сетевых проблем: %5d | доступность: %8.3f%%"
                    .format(target.name, successful, unavailable, networkUnavailable, availability)
            )

            val unavailableMinutes = allMinutes.filter { minute ->
                val ping = sourceLog["$minute|${target.ip}"]
                ping != null && ping.latency == null && expectedTargets(source, servers).any {
                    sourceLog["$minute|${it.ip}"]?.latency != null
                }
            }

            if (unavailableMinutes.isNotEmpty()) {
                findIntervals(unavailableMinutes)
                    .map { it.size }
                    .groupingBy { it }
                    .eachCount()
                    .toList()
                    .sortedByDescending { it.first }
                    .forEach { (minutes, times) -> println("\t\t$times раз недоступен в течении $minutes мин") }
            }
        }
    }

    println()
    println()
    println("=".repeat(100))
    println("АНАЛИЗ ПРОБЛЕМ")
    println("=".repeat(100))

    val problems = mutableListOf<Problem>()

    for (minute in allMinutes) {
        for (source in servers) {
            val targets = expectedTargets(source, servers)
            if (targets.isEmpty()) continue

            val sourceLog = logs[source.name].orEmpty()

            val sourceIsDown = targets.none {
                sourceLog["$minute|${it.ip}"] != null
            }

            if (sourceIsDown) {
                problems += Problem.ServerDown(
                    minute = minute,
                    server = source
                )
                continue
            }

            for (target in targets) {
                val ping = sourceLog["$minute|${target.ip}"] ?: continue

                if (ping.latency == null) {
                    problems += Problem.NetworkUnavailable(
                        minute = minute,
                        source = source,
                        target = target
                    )
                }
            }
        }
    }

    println()
    println("ПОДОЗРЕНИЕ НА ПРОБЛЕМУ САМИХ СЕРВЕРОВ")
    println("-".repeat(100))

    for (target in servers) {
        val incomingProblems = problems.count {
            it is Problem.NetworkUnavailable && it.target.name == target.name
        }
        val targetDown = problems.count {
            it is Problem.ServerDown && it.server.name == target.name
        }

        println(
            "  %-12s | падал: %5d мин | был недоступен для других: %5d мин"
                .format(target.name, targetDown, incomingProblems)
        )
    }

    println()
    println()
    println("=".repeat(100))
    println("МАТРИЦА СЕТЕВОЙ НЕДОСТУПНОСТИ")
    println("(строка -> столбец, количество минут)")
    println("=".repeat(100))

    val nonHomeServers = servers.filter { it.name != "home" }

    print("%-15s".format(""))
    for (target in nonHomeServers) {
        print("%12s".format(target.name))
    }
    println()

    for (source in nonHomeServers) {
        print("%-15s".format(source.name))

        for (target in nonHomeServers) {
            if (source.name == target.name) {
                print("%12s".format("-"))
                continue
            }

            val count = allMinutes.count { minute ->
                val sourceTargets = expectedTargets(source, servers)
                val sourceLog = logs[source.name].orEmpty()

                val sourceAlive = sourceTargets.any {
                    sourceLog["$minute|${it.ip}"] != null
                }

                val failed = sourceLog["$minute|${target.ip}"]?.latency == null &&
                        sourceLog["$minute|${target.ip}"] != null

                sourceAlive && failed
            }
            print("%12d".format(count))
        }
        println()
    }

    println()
    println()
    println("=".repeat(100))
    println("ИТОГ: ГДЕ СКОРЕЕ ВСЕГО ПРОБЛЕМА")
    println("=".repeat(100))

    for (server in servers) {
        val downCount = problems.count {
            it is Problem.ServerDown && it.server.name == server.name
        }
        val incoming = problems.count {
            it is Problem.NetworkUnavailable && it.target.name == server.name
        }
        val outgoing = problems.count {
            it is Problem.NetworkUnavailable && it.source.name == server.name
        }

        println()
        println(server.name)
        println("  Собственные падения       : $downCount мин")
        println("  Недоступен для других     : $incoming мин")
        println("  Сам не мог достучаться    : $outgoing мин")

        when {
            downCount > 0 && incoming > 0 && incoming >= outgoing -> {
                println("  => ОСНОВНАЯ ПОДОЗРЕВАЕМАЯ ПРИЧИНА: сам сервер")
            }

            outgoing > 0 && incoming == 0 -> {
                println("  => ОСНОВНАЯ ПОДОЗРЕВАЕМАЯ ПРИЧИНА: исходящая/маршрутизация")
            }

            incoming > 0 && outgoing > 0 -> {
                println("  => Наблюдается двусторонняя сетевая проблема")
            }

            else -> {
                println("  => Явных проблем не обнаружено")
            }
        }
    }
}

fun expectedTargets(
    source: Server,
    servers: List<Server>
): List<Server> {
    return if (source.name == "home") {
        servers.filter { it.name != "home" }
    } else {
        servers.filter { it.name != source.name && it.name != "home" }
    }
}

fun findIntervals(minutes: List<String>): List<List<String>> {
    if (minutes.isEmpty()) return emptyList()

    val sorted = minutes.sorted()
    val result = mutableListOf<MutableList<String>>()

    var current = mutableListOf(sorted.first())

    for (i in 1 until sorted.size) {
        val previous = java.time.LocalDateTime.parse(sorted[i - 1])
        val currentTime = java.time.LocalDateTime.parse(sorted[i])

        if (java.time.Duration.between(previous, currentTime).toMinutes() == 1L) {
            current += sorted[i]
        } else {
            result += current
            current = mutableListOf(sorted[i])
        }
    }

    result += current
    return result
}

fun formatInterval(interval: List<String>): String {
    if (interval.size == 1) {
        return "${interval.first()} (1 мин)"
    }

    val start = interval.first()
    val end = interval.last()

    return "$start -> $end (${interval.size} мин)"
}

fun parseServers(benchmarkDir: Path): List<Server> {
    return PadawanKt.Jackson.readList(
        benchmarkDir.resolve("servers.json").toString()
    )
}

data class Server(
    val name: String,
    val ip: String
)

data class Ping(
    val timestamp: String,
    val targetIp: String,
    val latency: Double?
)

sealed interface Problem {
    data class ServerDown(
        val minute: String,
        val server: Server
    ) : Problem

    data class NetworkUnavailable(
        val minute: String,
        val source: Server,
        val target: Server
    ) : Problem
}
