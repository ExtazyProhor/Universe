(function() {
    const script = document.createElement("script");
    script.async = true;

    if (location.hostname === "localhost") {
        script.src = "http://localhost:7001/files/js/requests.js";
    } else {
        script.src = "https://scarif.tima-prohorov.ru/files/js/requests.js";
    }

    script.onerror = () => console.error("Failed to load script:", script.src);
    document.head.appendChild(script);
})();
