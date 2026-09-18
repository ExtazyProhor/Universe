import {initAuth, universeFetch} from './api.js';
import {renderLogin} from './views/login.js';
import {renderProfile} from './views/profile.js';

const app = document.querySelector('#app');

const API_URL = getApiUrl();

function getApiUrl() {
    const {hostname, protocol} = window.location;

    if (hostname === 'localhost' || hostname === '127.0.0.1') {
        return `${protocol}//${hostname}:7001`;
    }
    return `${protocol}//api.${hostname}`;
}

function normalizeUrl() {
    if (window.location.pathname !== '/') {
        history.replaceState({}, '', '/');
    }
}

async function loadSessions() {
    const response = await universeFetch(`${API_URL}/api/auth/get_sessions`);
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
        reload: showApplication
    });
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
    container.innerHTML = `
        <div class="page-container">
            <section class="glass-card error-card">
                <h1>Что-то пошло не так</h1>
                <p>${message}</p>

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

start().catch(error => {
    console.error('Application startup failed:', error);

    renderError(
        app,
        'Не удалось запустить приложение.'
    );
});

export {API_URL}
