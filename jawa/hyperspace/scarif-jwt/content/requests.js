const REFRESH_URL = null;
let accessToken = null;

async function _loadConfig() {
    const resp = await fetch('/api/scarif/config');
    const data = await resp.json();
    REFRESH_URL = data.refresh_url;
}

async function _refreshAccessToken() {
    const response = await fetch(REFRESH_URL, {
        method: 'POST',
        credentials: 'include',
    });

    if (response.status === 200) {
        const data = await response.json();
        accessToken = data.access_token;
        return response;
    }
    if (response.status === 401) {
        accessToken = null;
        return response;
    }
    throw new Error(`Refresh failed, status: ${response.status}`);
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
    if (refreshResponse.status === 200) {
        response = await doFetch();
    }

    return response;
}

window.universeFetch = universeFetch;

window.initAuth = async function () {
    try {
        if (!REFRESH_URL) {
            await _loadConfig();
        }
        const refreshResponse = await _refreshAccessToken();
        return refreshResponse.status === 200;
    } catch (e) {
        console.error('Init auth failed', e);
        return false;
    }
}
