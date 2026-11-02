globalThis.window = {location: {hostname: "localhost", protocol: "http:"}};
const {getKenobiApiUrl, startTraining, completeTraining} = await import(
    "../content/js/api.js"
    );

Deno.test("API address follows the existing fondor convention", () => {
    const cases = [
        [{hostname: "localhost", protocol: "http:"}, "http://localhost:7008"],
        [{hostname: "127.0.0.1", protocol: "http:"}, "http://127.0.0.1:7008"],
        [
            {hostname: "kenobi.universe-apps.ru", protocol: "https:"},
            "https://api.kenobi.universe-apps.ru",
        ],
        [
            {hostname: "english.example.com", protocol: "https:"},
            "https://api.english.example.com",
        ],
    ];
    for (const [location, expected] of cases) {
        if (getKenobiApiUrl(location) !== expected) {
            throw new Error(expected);
        }
    }
});

Deno.test("training refreshes JWT and sends one bounded completion to the backend", async () => {
    const originalFetch = globalThis.fetch;
    const calls = [];
    let failCompletion = true;
    globalThis.fetch = async (url, options) => {
        calls.push({url, options});
        if (url.includes("/api/auth/refresh")) {
            return Response.json({access_token: "test-token"});
        }
        if (!options.headers.Authorization) {
            return new Response(null, {status: 401});
        }
        if (url.endsWith("/complete")) {
            if (failCompletion) {
                failCompletion = false;
                return new Response(null, {status: 503});
            }
            return new Response(null, {status: 204});
        }
        return Response.json({
            id: "batch",
            cards: [{englishForm: "word", translates: ["слово"], translations: []}],
        });
    };
    try {
        const training = await startTraining();
        if (training.cards.length !== 1 || calls.length !== 3) {
            throw Error("JWT refresh flow failed");
        }
        const body = {
            id: training.id,
            results: [{englishForm: "word", difficult: true}],
        };
        let failed = false;
        try {
            await completeTraining(body);
        } catch {
            failed = true;
        }
        if (!failed) throw Error("Save failure must propagate");
        await completeTraining(body);
        const completion = calls.at(-1);
        if (
            completion.options.body !== JSON.stringify(body) ||
            completion.options.headers.Authorization !== "Bearer test-token"
        ) throw Error("Completion was changed");
    } finally {
        globalThis.fetch = originalFetch;
    }
});

Deno.test("unauthorized refresh is reported as authentication failure without retrying forever", async () => {
    const api = await import('../content/js/api.js?unauthorized');
    const originalFetch = globalThis.fetch;
    let calls = 0;
    globalThis.fetch = async () => {
        calls++;
        return new Response(null, {status: 401});
    };
    try {
        let error;
        try {
            await api.startTraining();
        } catch (caught) {
            error = caught;
        }
        if (!(error instanceof api.AuthenticationError) || calls !== 2) throw Error('Expected bounded authentication failure');
    } finally {
        globalThis.fetch = originalFetch;
    }
});

Deno.test("parallel unauthorized requests share one in-flight refresh", async () => {
    const api = await import('../content/js/api.js?parallel');
    const originalFetch = globalThis.fetch;
    let refreshes = 0;
    globalThis.fetch = async (url, options) => {
        if (url.endsWith('/api/auth/refresh')) {
            refreshes++;
            await new Promise(resolve => setTimeout(resolve, 10));
            return Response.json({access_token: 'parallel-token'});
        }
        if (options.headers.Authorization !== 'Bearer parallel-token') return new Response(null, {status: 401});
        return Response.json({id: 'batch', cards: []});
    };
    try {
        await Promise.all([api.startTraining(), api.startTraining()]);
        if (refreshes !== 1) throw Error('Refresh was duplicated');
    } finally {
        globalThis.fetch = originalFetch;
    }
});

Deno.test("refresh server failure propagates and a subsequent attempt can recover", async () => {
    const api = await import('../content/js/api.js?refresh-failure');
    const originalFetch = globalThis.fetch;
    let failing = true;
    globalThis.fetch = async (url, options) => {
        if (url.endsWith('/api/auth/refresh')) return failing ? new Response(null, {status: 503}) : Response.json({access_token: 'recovered'});
        if (!options.headers.Authorization) return new Response(null, {status: 401});
        return Response.json({id: 'recovered', cards: []});
    };
    try {
        let failed = false;
        try {
            await api.startTraining();
        } catch (error) {
            failed = !(error instanceof api.AuthenticationError);
        }
        if (!failed) throw Error('Refresh server failure must be distinct from logged-out state');
        failing = false;
        if ((await api.startTraining()).id !== 'recovered') throw Error('Refresh promise remained stuck');
    } finally {
        globalThis.fetch = originalFetch;
    }
});
