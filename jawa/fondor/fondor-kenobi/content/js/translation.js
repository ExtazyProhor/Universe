const escape = (text) =>
    String(text).replace(
        /[&<>"']/g,
        (c) => ({
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#39;",
        }[c]),
    );

export function renderTranslation(word) {
    if (word.translations?.length) {
        return word.translations.map((group) =>
            `<article class="meaning-group"><h3>${
                group.translation.map(escape).join(", ")
            }</h3><p lang="en">${
                escape(group.example)
            }</p><p class="example-translation">${
                escape(group.exampleTranslation)
            }</p></article>`
        ).join("");
    }
    return word.translates.map((t) => `<span>${escape(t)}</span>`).join("");
}
