export function escapeHtml(value) {
    return String(value)
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#039;');
}

export function renderBrand(tag = 'div') {
    return `
        <${tag} class="brand">
            <img class="brand-mark" src="/icon/scarif_icon_big.svg" alt="">
            <span class="brand-name">Scarif</span>
        </${tag}>
    `;
}
