const root = new URL("../content/", import.meta.url);
const server = Deno.serve(
    {
        hostname: "127.0.0.1", port: 0, onListen() {
        }
    },
    async (request) => {
        const path = new URL(request.url).pathname;
        const asset = /\.(js|css)$/.test(path);
        try {
            const body = await Deno.readTextFile(
                new URL(asset ? path.slice(1) : "index.html", root),
            );
            return new Response(body, {
                headers: {
                    "Content-Type": path.endsWith(".js")
                        ? "text/javascript"
                        : path.endsWith(".css")
                            ? "text/css"
                            : "text/html",
                },
            });
        } catch {
            return new Response(null, {status: 404});
        }
    },
);
const profile = await Deno.makeTempDir({prefix: "kenobi-browser-check-"});
let chrome, ws;
const pause = (ms) => new Promise((resolve) => setTimeout(resolve, ms));
try {
    chrome = new Deno.Command(
        "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome",
        {
            args: [
                "--headless=new",
                "--disable-gpu",
                "--no-first-run",
                "--no-default-browser-check",
                "--remote-debugging-port=0",
                `--user-data-dir=${profile}`,
                "about:blank",
            ],
            stdout: "null",
            stderr: "null",
        },
    ).spawn();
    let port;
    for (let i = 0; i < 100; i++) {
        try {
            port = (await Deno.readTextFile(`${profile}/DevToolsActivePort`))
                .split("\n")[0];
            break;
        } catch {
            await pause(100);
        }
    }
    if (!port) throw Error("Chrome did not start");
    const target =
        await (await fetch(`http://127.0.0.1:${port}/json/new?about:blank`, {
            method: "PUT",
        })).json();
    ws = new WebSocket(target.webSocketDebuggerUrl);
    await new Promise((resolve, reject) => {
        ws.onopen = resolve;
        ws.onerror = reject;
    });
    let id = 0;
    const pending = new Map(), errors = [];
    ws.onmessage = (event) => {
        const message = JSON.parse(event.data);
        if (message.id) {
            const handler = pending.get(message.id);
            if (!handler) return;
            pending.delete(message.id);
            clearTimeout(handler.timeout);
            message.error
                ? handler.reject(message.error)
                : handler.resolve(message.result);
        } else if (message.method === "Runtime.exceptionThrown") {
            errors.push(message.params);
        }
    };
    const call = (method, params = {}) =>
        new Promise((resolve, reject) => {
            const requestId = ++id;
            const timeout = setTimeout(() => {
                pending.delete(requestId);
                reject(Error(`CDP timeout: ${method}`));
            }, 10000);
            pending.set(requestId, {resolve, reject, timeout});
            ws.send(JSON.stringify({id: requestId, method, params}));
        });
    const evaluate = async (expression) => {
        const result = await call("Runtime.evaluate", {
            expression,
            returnByValue: true,
            awaitPromise: true,
        });
        if (result.exceptionDetails) {
            throw Error(JSON.stringify(result.exceptionDetails));
        }
        return result.result.value;
    };
    const wait = async (expression) => {
        for (let i = 0; i < 100; i++) {
            if (await evaluate(expression)) return;
            await pause(30);
        }
        throw Error(`Timeout: ${expression}`);
    };
    const check = async (expression, label) => {
        if (!await evaluate(expression)) throw Error(label);
        console.log(`PASS ${label}`);
    };
    const click = (selector) =>
        evaluate(`document.querySelector(${JSON.stringify(selector)}).click()`);
    await call("Page.enable");
    await call("Runtime.enable");
    await call("Emulation.setEmulatedMedia", {
        features: [{name: "prefers-reduced-motion", value: "reduce"}],
    });
    // Only network boundary is mocked: DOM, routing, events and rendering are real.
    await call("Page.addScriptToEvaluateOnNewDocument", {
        source: `
        window.qa = {calls: [], saveStatus: 503, delay: 0, empty: false, refreshStatus: 200};
        const cards = [
            {englishForm:'yield',translates:['уступать'],translations:[
                {translation:['урожай','доход'],example:'The field yields a good harvest.',exampleTranslation:'Поле даёт хороший урожай.'},
                {translation:['уступать'],example:'Yield to pedestrians.',exampleTranslation:'Уступайте пешеходам.'}]},
            {englishForm:'wrist',translates:['запястье'],translations:[]}
        ];
        window.fetch = async (url, options = {}) => {
            qa.calls.push({url, options});
            if(url.endsWith('/api/auth/refresh')) return qa.refreshStatus===200 ? Response.json({access_token:'test-token'}) : new Response(null,{status:qa.refreshStatus});
            if(!options.headers.Authorization) return new Response(null,{status:401});
            if(url.endsWith('/complete')) return new Response(null,{status:qa.saveStatus});
            await new Promise(resolve=>setTimeout(resolve,qa.delay));
            return Response.json({id:qa.empty?null:'test-batch',cards:qa.empty?[]:cards});
        };
    `,
    });
    const origin = `http://127.0.0.1:${server.addr.port}`;
    await call("Page.navigate", {url: origin});
    await wait('!!document.querySelector("#start")');
    await check("qa.calls.length===0", "home performs no API requests");
    for (const width of [1440, 768, 375, 320]) {
        await call("Emulation.setDeviceMetricsOverride", {
            width,
            height: 900,
            deviceScaleFactor: 1,
            mobile: width < 500,
        });
        await check(
            "document.documentElement.scrollWidth<=innerWidth",
            `home fits ${width}px`,
        );
        await check(
            'Math.abs(document.querySelector("h1").getBoundingClientRect().left+document.querySelector("h1").getBoundingClientRect().width/2-innerWidth/2)<2',
            `home centered at ${width}px`,
        );
    }
    await click("#start");
    await wait('!!document.querySelector("#reveal")');
    await check(
        'location.pathname==="/training" && document.querySelector("#answer").hidden',
        "training URL and hidden answer",
    );
    await click("#reveal");
    await check(
        'document.querySelectorAll(".meaning-group").length===2 && document.querySelector("h3").textContent==="урожай, доход"',
        "array translations and examples render",
    );
    await check(
        "document.documentElement.scrollWidth<=innerWidth",
        "enriched card fits mobile",
    );
    await click("#back");
    await check('location.pathname==="/"', "home navigation updates URL");
    await evaluate("history.back()");
    await wait('!!document.querySelector("#again")');
    await evaluate("history.forward()");
    await wait('!!document.querySelector("#start")');
    await click("#start");
    await click("#again");
    await wait('!!document.querySelector("#reveal")');
    await click("#reveal");
    await check(
        'document.querySelector(".translations").textContent==="запястье"',
        "legacy translation fallback",
    );
    await click("#remember");
    await wait('!!document.querySelector("#reveal")');
    await click("#reveal");
    await click("#remember");
    await wait('!!document.querySelector("#retry")');
    await check(
        'location.pathname==="/result" && document.querySelector("p").textContent.includes("Не удалось сохранить")',
        "failed completion remains on result URL",
    );
    await evaluate("qa.saveStatus=204");
    await click("#retry");
    await wait('!!document.querySelector("#next")');
    await check(
        '(()=>{const calls=qa.calls.filter(c=>c.url.endsWith("/complete"));return calls.length===2 && calls[0].options.body===calls[1].options.body && JSON.parse(calls[1].options.body).results[0].difficult;})()',
        "completion retry preserves results",
    );
    await click("#home");
    await evaluate("qa.delay=200");
    await click("#start");
    await evaluate("history.back()");
    await wait('!!document.querySelector("#start")');
    await pause(300);
    await check(
        'location.pathname==="/" && !!document.querySelector("#start")',
        "late training response cannot replace home",
    );
    await call("Page.navigate", {url: origin + "/training"});
    await wait('!!document.querySelector("#reveal")');
    await check('location.pathname==="/training"', "direct training URL works");
    await call("Page.navigate", {url: origin + "/result"});
    await wait('!!document.querySelector("#start")');
    await check(
        'location.pathname==="/"',
        "result without session redirects home",
    );
    await call("Page.navigate", {url: origin + "/missing"});
    await wait('!!document.querySelector("#home")');
    await check(
        'document.querySelector("h1").textContent.includes("не найдена")',
        "unknown route has a not-found page",
    );
    await click("#home");
    await evaluate("qa.refreshStatus=401");
    await click("#start");
    await wait('!!document.querySelector("#retry")');
    await check(
        'document.querySelector("a")?.href==="http://127.0.0.1:7009/"',
        "unauthorized user receives Scarif link",
    );
    await evaluate("qa.refreshStatus=200;qa.empty=true");
    await click("#retry");
    await wait('document.querySelector("h1").textContent.includes("скоро")');
    await check(
        'location.pathname==="/training"',
        "empty dictionary state stays on training URL",
    );
    if (errors.length) throw Error(JSON.stringify(errors));
    console.log("PASS no uncaught browser errors");
} finally {
    ws?.close();
    if (chrome) {
        try {
            chrome.kill("SIGTERM");
        } catch { /* Already exited. */
        }
        await chrome.status;
    }
    await server.shutdown();
    await Deno.remove(profile, {recursive: true});
}
