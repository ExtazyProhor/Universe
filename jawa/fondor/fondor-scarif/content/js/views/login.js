import {SCARIF_API_URL} from '../api.js';
import {escapeHtml, renderBrand} from '../utils.js';

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
    document.title = 'Scarif - вход';
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
                <div class="auth-header">
                    ${renderBrand('h1')}
                    <p>
                        Единый сервис аутентификации для сервисов
                        <a href="https://universe-apps.ru" target="_blank" rel="noopener noreferrer">Universe</a>.
                        Управляйте своей учётной записью, сессиями и безопасностью в одном месте.
                    </p>
                    <p class="auth-legal">
                        Нажимая на кнопку «Войти через Google», вы соглашаетесь с
                        <a href="/terms-and-policies/privacy-policy" target="_blank" rel="noopener">политикой конфиденциальности</a>
                        и
                        <a href="/terms-and-policies/user-agreement" target="_blank" rel="noopener">пользовательским соглашением</a>,
                        а также даёте согласие на
                        <a href="/terms-and-policies/cookie-policy" target="_blank" rel="noopener">обработку файлов cookie</a>
                        для авторизации.
                    </p>
                </div>
                <div class="login-actions">
                    <a class="google-button" href="${SCARIF_API_URL}/api/oauth/google">
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
