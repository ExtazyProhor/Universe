export function createRouter(render, browser = window) {
    const dispatch = () => render(browser.location.pathname);
    const navigate = (path, {replace = false} = {}) => {
        if (browser.location.pathname !== path) {
            browser.history[replace ? "replaceState" : "pushState"](null, "", path);
        }
        dispatch();
    };
    browser.addEventListener("popstate", dispatch);
    return {navigate, start: dispatch};
}
