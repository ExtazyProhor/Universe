/*  Общая утилита для работы аутентификации в сервисах universe */

let accessToken = null;
let refreshPromise = null;

function getApiUrl() {
    const {hostname, protocol} = window.location;
    if (hostname === 'localhost' || hostname === '127.0.0.1') {
        return `${protocol}//${hostname}:7001`;
    }
    return `${protocol}//api.scarif.universe-apps.ru`;
}

const apiUrl = getApiUrl();

async function _refreshAccessToken() {
    if (refreshPromise) {
        return refreshPromise;
    }

    refreshPromise = (async () => {
        try {
            const response = await fetch(`${apiUrl}/api/auth/refresh`, {
                method: 'POST',
                credentials: 'include',
            });

            if (response.status === 200) {
                const data = await response.json();
                accessToken = data.access_token;
            } else if (response.status === 401) {
                accessToken = null;
            } else {
                throw new Error(`Refresh failed, status: ${response.status}`);
            }

            return response;
        } finally {
            refreshPromise = null;
        }
    })();

    return refreshPromise;
}

async function universeFetch(url, options = {}) {
    const doFetch = async () => {
        const headers = {
            ...(options.headers || {}),
        };
        if (accessToken) {
            headers['Authorization'] = `Bearer ${accessToken}`;
        }

        console.log('[universeFetch] ->', url);
        return fetch(url, {
            ...options,
            headers,
            credentials: 'include',
        });
    };

    let response = await doFetch();
    if (response.status !== 401) {
        return response;
    }

    const refreshResponse = await _refreshAccessToken();
    if (refreshResponse.status !== 200) {
        console.log(
            'Failed to refresh access token:',
            refreshResponse.status
        );
        return response;
    }

    return doFetch();
}

async function initAuth() {
    try {
        const refreshResponse = await _refreshAccessToken();
        return refreshResponse.status === 200;
    } catch (e) {
        console.error('Init auth failed', e);
        return false;
    }
}

export {
    universeFetch,
    initAuth,
};
