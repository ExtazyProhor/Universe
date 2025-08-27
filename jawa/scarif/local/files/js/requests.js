function _buildUrl(sub, path) {
    return `http://localhost:7001/${path}`;
}

function _fetchWith(method, url, body) {
    const options = {
        method,
        credentials: 'include',
        headers: {},
    };
    if (method === 'POST') {
        options.headers['Content-Type'] = 'application/json';
        options.body = JSON.stringify(body);
    }
    return fetch(url, options);
}

async function _handleAuthRequest(method, sub, path, body) {
    const url = _buildUrl(sub, path);
    let response = await _fetchWith(method, url, body);

    if (response.status !== 401) {
        return response;
    }

    const refreshUrl = `http://localhost:7001/api/auth/refresh`;
    const refreshResp = await fetch(refreshUrl, {
        method: 'POST',
        credentials: 'include',
    });

    if (refreshResp.status === 200) {
        response = await _fetchWith(method, url, body);
        if (response.status === 401) {
            throw new Error('Unauthorized after refresh');
        }
        return response;
    }
    if (refreshResp.status === 401) {
        window.location = `http://localhost:7001/pages/login`;
        throw new Error('Redirecting to login');
    }
    throw refreshResp;
}

window.universeGet = async function authGet(sub, path) {
    return _handleAuthRequest('GET', sub, path);
}

window.universePost = async function authPost(sub, path, body) {
    return _handleAuthRequest('POST', sub, path, body);
}

/*

<script src="https://scarif.tima-prohorov.ru/js/requests.js"></script>

universeGet('sub', 'public/api/data')
    .then(r => r.json())
    .then(console.log)
    .catch(console.error);

universePost('sub', 'private/api/data', { foo: 123 })
    .then(r => r.json())
    .then(console.log)
    .catch(console.error);

*/
