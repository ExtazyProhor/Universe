package ru.prohor.universe.uni.cli.helper.fslinter

enum class IssueCode(val title: String, val description: String) {
    EMPTY_OR_DOT("Empty or dot name", "Name is empty or is '.' or '..'."),
    FORBIDDEN_CHARS("Forbidden characters", "Name contains characters forbidden on Windows."),
    CONTROL_CHARS("Control characters", "Name contains control characters."),
    TRAILING_SPACE_OR_DOT("Trailing space or dot", "Name ends with a space or dot (invalid on Windows)."),
    LEADING_SPACE("Leading space", "Name starts with a space."),
    RESERVED_NAME("Reserved device name", "Name uses a reserved Windows device name."),
    NOT_NFC("Non-NFC name", "Name is not NFC-normalized (common on macOS, causes duplicates on Windows/in git)."),
    COMPONENT_TOO_LONG("Long filename", "Component exceeds the 255-byte UTF-8 limit."),
    DUPLICATE_NAME_CASEFOLD("Duplicate names", "Names collide after case folding and Unicode normalization."),
    PATH_TOO_LONG("Long path", "Path exceeds the 260-character Windows limit."),
}

data class Issue(val code: IssueCode, val detail: String? = null) {
    val message: String get() = if (detail == null) code.description else "${code.description} $detail"
}
