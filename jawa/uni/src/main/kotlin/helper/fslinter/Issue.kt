package ru.prohor.universe.uni.cli.helper.fslinter

enum class IssueCode {
    EMPTY_OR_DOT,
    FORBIDDEN_CHARS,
    CONTROL_CHARS,
    TRAILING_SPACE_OR_DOT,
    LEADING_SPACE,
    RESERVED_NAME,
    NOT_NFC,
    COMPONENT_TOO_LONG,
}

data class Issue(val code: IssueCode, val message: String)
