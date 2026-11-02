import {createRouter} from "../content/js/router.js";

Deno.test("SPA dispatches direct paths and Back/Forward without adding duplicate history", () => {
    const entries = ["/training"];
    let index = 0, onPop;
    const rendered = [];
    const browser = {
        location: {pathname: entries[0]},
        addEventListener: (_, handler) => {
            onPop = handler;
        },
        history: {
            pushState: (_, __, path) => {
                entries.splice(index + 1);
                entries.push(path);
                index++;
                browser.location.pathname = path;
            },
            replaceState: (_, __, path) => {
                entries[index] = path;
                browser.location.pathname = path;
            },
        },
    };
    const router = createRouter((path) => rendered.push(path), browser);
    router.start();
    router.navigate("/result");
    router.navigate("/result");
    if (entries.length !== 2) throw Error("Duplicate history");
    index--;
    browser.location.pathname = entries[index];
    onPop();
    index++;
    browser.location.pathname = entries[index];
    onPop();
    router.navigate("/", {replace: true});
    if (
        entries.length !== 2 || entries[1] !== "/" ||
        rendered.join(",") !== "/training,/result,/result,/training,/result,/"
    ) throw Error("Incorrect route dispatch");
});
