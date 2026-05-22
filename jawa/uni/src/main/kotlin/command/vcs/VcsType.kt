package ru.prohor.universe.uni.cli.command.vcs

enum class VcsType(
    val command: String,
    val mainBranch: String
) {
    GIT("git", "main"),
    ARC("arc", "trunk")
}
