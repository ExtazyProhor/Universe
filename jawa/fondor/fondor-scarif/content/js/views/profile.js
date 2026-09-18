import {universeFetch} from '../api.js';
import {API_URL} from '../app.js'
import {escapeHtml} from '../utils.js';

export function renderProfile(container, sessions, {reload}) {
    document.body.classList.add('profile-page');
    document.body.classList.remove('login-page');

    const currentSession = sessions.find(session => session.current === true) || sessions[0];
    const otherSessions = sessions.filter(
        session => session !== currentSession
    );

    const currentSessionCard = renderSessionCard(currentSession, true)
    const sessionsListHtml = otherSessions.length > 0
        ? otherSessions.map(session => renderSessionCard(session, false)).join('')
        : `<div class="empty-sessions">Других активных сессий нет.</div>`;

    container.innerHTML = `
        <div class="profile-container">
            <header class="profile-header">
                <div class="profile-brand">
                    <div class="brand-mark small">S</div>

                    <div>
                        <div class="profile-brand-name">
                            Scarif
                        </div>
                        <div class="profile-brand-subtitle">
                            Сервис аутентификации
                        </div>
                    </div>
                </div>
                <div class="profile-page-title">
                    Управление профилем
                </div>
            </header>
            <main class="profile-content">
                <section class="sessions-section">
                    <div class="section-heading">
                        <h1>Текущая сессия</h1>
                        <p>
                            Устройство, с которого вы сейчас
                            используете Scarif.
                        </p>
                    </div>
                    <div id="currentSession">
                    ${currentSession ? currentSessionCard : ''}
                    </div>
                </section>

                <section class="sessions-section">
                    <div class="section-heading">
                        <h2>Другие сессии</h2>
                        <p>
                            Здесь отображаются остальные активные
                            подключения к вашему аккаунту.
                        </p>
                        <button class="secondary-button" type="button" id="refreshSessions">
                            Обновить
                        </button>
                    </div>

                    <div class="sessions-list">
                        ${sessionsListHtml}
                    </div>
                </section>
            </main>
        </div>
    `;

    container.querySelector('#refreshSessions').addEventListener('click', reload);
    bindSessionActions(container, reload);
}

function renderSessionCard(session, current) {
    const id = escapeHtml(String(session.id ?? ''));

    return `
        <article
            class="session-card ${current ? 'current-session' : ''}"
            data-session-id="${id}"
        >
            <div class="session-main">
                <div class="session-icon">
                    ${getDeviceIcon(session)}
                </div>

                <div class="session-info">
                    <div class="session-title">
                        ${escapeHtml(getSessionTitle(session))}
                    </div>

                    <div class="session-details">
                        ${renderSessionDetails(session)}
                    </div>
                </div>
            </div>

            <div class="session-actions">
                ${
        current
            ? `
                            <button
                                class="secondary-button logout-button"
                                type="button"
                                data-action="logout"
                            >
                                Выйти из аккаунта
                            </button>
                        `
            : `
                            <button
                                class="danger-button terminate-button"
                                type="button"
                                data-action="terminate"
                                data-session-id="${id}"
                            >
                                Завершить сессию
                            </button>
                        `
    }
            </div>
        </article>
    `;
}

function getSessionTitle(session) {
    return session.userAgent || 'Неизвестное устройство';
}

function renderSessionDetails(session) {
    const fields = [
        ['IP-адрес', session.ip],
        ['Создана', session.createdAt]
    ];

    return fields
        .filter(([, value]) => value !== undefined && value !== null)
        .map(
            ([label, value]) => `
                <div class="session-detail">
                    <span class="session-detail-label">
                        ${escapeHtml(label)}
                    </span>

                    <span class="session-detail-value">
                        ${escapeHtml(String(value))}
                    </span>
                </div>
            `
        )
        .join('');
}

function getDeviceIcon(session) {
    const device = String(session.userAgent || '').toLowerCase();
    if (
        device.includes('iphone') ||
        device.includes('android') ||
        device.includes('mobile')
    ) {
        return `
            <svg viewBox="0 0 24 24" aria-hidden="true">
                <rect x="6" y="2" width="12" height="20" rx="2"/>
                <line x1="10" y1="18" x2="14" y2="18"/>
            </svg>
        `;
    }

    return `
        <svg viewBox="0 0 24 24" aria-hidden="true">
            <rect x="3" y="4" width="18" height="13" rx="2"/>
            <line x1="8" y1="21" x2="16" y2="21"/>
            <line x1="12" y1="17" x2="12" y2="21"/>
        </svg>
    `;
}

function bindSessionActions(container, reload) {
    container
        .querySelectorAll('[data-action="terminate"]')
        .forEach(button => {
            button.addEventListener('click', async () => {
                const sessionId = button.dataset.sessionId;

                if (!sessionId) {
                    return;
                }

                button.disabled = true;
                button.textContent = 'Завершение...';

                try {
                    const response = await universeFetch(`${API_URL}/api/auth/close_session`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify({
                            sessionId
                        })
                    });

                    if (response.status === 401) {
                        window.location.reload();
                        return;
                    }

                    if (!response.ok) {
                        throw new Error(
                            `Failed to terminate session: ${response.status}`
                        );
                    }

                    await reload();
                } catch (error) {
                    console.error(
                        'Failed to terminate session:',
                        error
                    );

                    button.disabled = false;
                    button.textContent = 'Завершить сессию';

                    alert('Не удалось завершить сессию.');
                }
            });
        });

    const logoutButton = container.querySelector(
        '[data-action="logout"]'
    );

    if (logoutButton) {
        logoutButton.addEventListener('click', async () => {
            logoutButton.disabled = true;
            logoutButton.textContent = 'Выход...';

            try {
                const response = await universeFetch(
                    `${API_URL}/api/auth/logout`,
                    {
                        method: 'POST'
                    }
                );

                if (!response.ok && response.status !== 401) {
                    throw new Error(
                        `Logout failed: ${response.status}`
                    );
                }

                window.location.reload();
            } catch (error) {
                console.error('Logout failed:', error);

                logoutButton.disabled = false;
                logoutButton.textContent = 'Выйти из аккаунта';

                alert('Не удалось выполнить выход.');
            }
        });
    }
}
