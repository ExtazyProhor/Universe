let refreshUrl = null;
let refreshPromise = null;
let accessToken = null;

async function _loadConfig() {
    const resp = await fetch('/api/scarif/config');
    const data = await resp.json();
    refreshUrl = data.refresh_url;
}

async function _refreshAccessToken() {
    if (refreshPromise) return refreshPromise;

    refreshPromise = (async () => {
        const response = await fetch(refreshUrl, {
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

        refreshPromise = null;
        return response;
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
    if (refreshResponse.status === 200 || refreshResponse.status === 409) {
        response = await doFetch();
    }

    return response;
}

async function initAuth() {
    try {
        if (!refreshUrl) {
            await _loadConfig();
        }
        const refreshResponse = await _refreshAccessToken();
        return refreshResponse.status === 200;
    } catch (e) {
        console.error('Init auth failed', e);
        return false;
    }
}

window.universeFetch = universeFetch;
window.initAuth = initAuth;
