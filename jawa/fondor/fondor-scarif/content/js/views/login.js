import {API_URL} from '../app.js'
import {escapeHtml} from '../utils.js';

function getLoginError() {
    const params = new URLSearchParams(window.location.search);
    const type = params.get('type');
    const message = params.get('message');

    if (!type || !message) {
        return null;
    }
    const allowedTypes = new Set([
        'client-error',
        'server-error'
    ]);
    if (!allowedTypes.has(type)) {
        return null;
    }
    return {
        type,
        message
    };
}

export function renderLogin(container) {
    document.body.classList.add('login-page');
    document.body.classList.remove('profile-page');

    const error = getLoginError();
    const errorBanner = error
        ? `
        <div class="login-error login-error-${error.type}" role="alert">
            <div class="login-error-icon" aria-hidden="true">
                !
            </div>

            <div class="login-error-content">
                <strong>
                    ${error.type === 'server-error'
            ? 'Ошибка сервера'
            : 'Ошибка аутентификации'}
                </strong>

                <span class="login-error-message">
                    ${escapeHtml(error.message)}
                </span>
            </div>

            <button
                class="login-error-close"
                type="button"
                aria-label="Закрыть сообщение"
                id="closeLoginError"
            >
                ×
            </button>
        </div>
    ` : '';

    container.innerHTML = `
        <div class="page-container">
            <section class="auth-card">
            ${errorBanner}
                <div class="brand">
                    <div class="brand-mark">S</div>
                    <div class="brand-name">
                        Scarif
                    </div>
                </div>
                <div class="auth-header">
                    <h1 class="login-title">Scarif</h1>
                    <p>
                        Единый сервис аутентификации для сервисов
                        Universe. Управляйте своей учётной записью,
                        сессиями и безопасностью в одном месте.
                    </p>
                </div>
                <div class="login-actions">
                    <a class="google-button" href="${API_URL}/api/oauth/google">
                        <img class="google-icon" src="/icon/google_icon.svg" alt="" aria-hidden="true">
                        <span>Войти через Google</span>
                    </a>
                </div>
                <div class="auth-footer">
                    <span>
                        Безопасная авторизация через OAuth
                    </span>
                </div>
            </section>
        </div>
    `;

    const closeLoginError = container.querySelector('#closeLoginError');

    if (closeLoginError) {
        closeLoginError.addEventListener('click', () => {
            history.replaceState({}, '', '/');
            const banner = container.querySelector('.login-error');
            if (banner) {
                banner.remove();
            }
        });
    }
}
