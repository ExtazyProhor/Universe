import {initAuth, universeFetch, SCARIF_API_URL} from './api.js';
import {renderLogin} from './views/login.js';
import {renderProfile} from './views/profile.js';
import {escapeHtml} from "./utils.js";

const app = document.querySelector('#app');

function normalizeUrl() {
    if (window.location.pathname !== '/') {
        history.replaceState({}, '', '/');
    }
}

async function loadSessions() {
    const response = await universeFetch(`${SCARIF_API_URL}/api/auth/sessions`);
    if (response.status === 401) {
        return null;
    }
    if (!response.ok) {
        throw new Error(`Failed to load sessions: ${response.status}`);
    }
    return response.json();
}

async function showApplication() {
    const sessions = await loadSessions();
    if (sessions === null) {
        renderLogin(app);
        return;
    }
    renderProfile(app, sessions, {
        reload: () => showApplication().catch(handleFatalError)
    });
}

function handleFatalError(error) {
    console.error('Application error:', error);
    renderError(app, 'Не удалось загрузить данные. Проверьте соединение и попробуйте снова');
}

async function start() {
    normalizeUrl();
    const authenticated = await initAuth();

    if (!authenticated) {
        renderLogin(app);
        return;
    }
    await showApplication();
}

function renderError(container, message) {
    document.title = 'Scarif - ошибка';
    container.innerHTML = `
        <div class="page-container">
            <section class="glass-card error-card">
                <h1>Что-то пошло не так</h1>
                <p>${escapeHtml(message)}</p>

                <button
                    class="primary-button"
                    type="button"
                    id="retryButton"
                >
                    Попробовать снова
                </button>
            </section>
        </div>
    `;

    container.querySelector('#retryButton').addEventListener(
        'click',
        () => location.reload()
    );
}

start().catch(handleFatalError);
