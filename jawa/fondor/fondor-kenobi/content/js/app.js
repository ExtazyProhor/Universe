import {answer, createSession} from "./session.js";
import {
    AuthenticationError,
    completeTraining,
    SCARIF_API_URL,
    startTraining,
} from "./api.js";
import {renderTranslation} from "./translation.js";
import {createRouter} from "./router.js";

const app = document.querySelector("#app");
let trainingId = null;
let completionSaved = false;
let session = null, revealed = false, busy = false, view = "home";
let routeVersion = 0;
let trainingRequest = null;
let completionRequest = null;
const router = createRouter(renderRoute);
const goHome = () => router.navigate("/");
const goTraining = () => router.navigate("/training");

function renderRoute(path) {
    routeVersion++;
    if (path === "/") {
        document.title = "Английский";
        home();
    } else if (path === "/training") {
        document.title = "Тренировка — Английский";
        if (session && !session.queue.length && !completionSaved) {
            router.navigate("/result", {replace: true});
        } else if (session?.queue.length) study();
        else load();
    } else if (path === "/result") {
        document.title = "Результат — Английский";
        if (session && !session.queue.length) complete();
        else router.navigate("/", {replace: true});
    } else {
        document.title = "Страница не найдена — Английский";
        setView(
            `<section class="status-view"><h1>Страница не найдена</h1>${
                button("home", "На главную")
            }</section>`,
            "not-found",
        );
        document.querySelector("#home").onclick = goHome;
    }
}

const escape = (text) =>
    String(text).replace(
        /[&<>"']/g,
        (c) => ({
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#39;",
        }[c]),
    );
const button = (id, label, cls = "primary") =>
    `<button type="button" class="${cls}" id="${id}">${label}</button>`;

function setView(html, name) {
    view = name;
    app.innerHTML = html;
    app.setAttribute("aria-busy", "false");
}

function home() {
    const resumable = !!session && (!completionSaved || session.queue.length > 0);
    setView(
        `<section class="training-home enter">
        <h1>Английский</h1>
        <p class="intro">Тренировка слов с интервальными повторениями.</p>
        <div class="start-row">${
            button(
                "start",
                resumable
                    ? "Продолжить практику <span>↗</span>"
                    : "Начать практику <span>↗</span>",
            )
        }<span class="duration">${
            resumable
                ? `${session.learned.length} из ${session.cards.length} закреплено`
                : "До 10 слов за подход"
        }</span></div>
        </section>`,
        "home",
    );
    document.querySelector("#start").onclick = () => {
        if (resumable && !session.queue.length) router.navigate("/result");
        else goTraining();
    };
}

function study(focus = false) {
    if (!session.queue.length) {
        return router.navigate("/result", {replace: true});
    }
    const word = session.cards[session.queue[0]];
    const done = session.learned.length, total = session.cards.length;
    setView(
        `<section class="practice enter"><div class="practice-top">${
            button("back", "← На главную", "text-button")
        }<span class="counter">${done} / ${total}</span></div>
        <div class="progress" role="progressbar" aria-label="Закреплено слов" aria-valuemin="0" aria-valuemax="${total}" aria-valuenow="${done}"><div style="width:${
            done / total * 100
        }%"></div></div>
        <div class="practice-title"><h1>Тренировка</h1><p>Достаточно вспомнить одно значение. Остальные — в ответе.</p></div>
        <div class="flashcard ${
            revealed ? "is-revealed" : ""
        }" id="flashcard"><div class="flashcard-header"><span>АНГЛИЙСКИЙ → РУССКИЙ</span></div><div class="word-area"><h2 lang="en">${
            escape(word.englishForm)
        }</h2><div class="answer-area" id="answer" ${
            revealed ? "" : "hidden"
        }><span class="eyebrow">ВОЗМОЖНЫЕ ЗНАЧЕНИЯ</span><div class="translations">${
            renderTranslation(word)
        }</div></div><p id="recall-hint" ${
            revealed ? "hidden" : ""
        }>Как это будет по-русски?</p></div><div class="flashcard-footer"><span>${
            session.difficult.includes(session.queue[0]) ? "↻ ПОВТОРЕНИЕ" : ""
        }</span><span>${String(done + 1).padStart(2, "0")}</span></div></div>
        <div class="answer-controls" id="controls">${
            revealed
                ? ratingButtons()
                : button("reveal", "Показать перевод <span>↗</span>")
        }</div>
        <p class="keyboard-note">${
            revealed
                ? "<kbd>1</kbd> повторить <span>·</span> <kbd>2</kbd> помню"
                : "<kbd>Пробел</kbd> показать перевод"
        }</p>
        <p class="save-note">Результат сохранится в вашем аккаунте после завершения подхода.</p></section>`,
        "study",
    );
    document.querySelector("#back").onclick = goHome;
    bindControls();
    if (focus) document.querySelector("#reveal")?.focus({preventScroll: true});
}

function ratingButtons() {
    return `<div class="rating-row">${
        button("again", "<span>↻</span> Нужно повторить", "secondary")
    }${
        button("remember", "Помню <span>✓</span>")
    }</div><span class="repeat-note">Сложное слово вернётся чуть позже.</span>`;
}

function bindControls() {
    document.querySelector("#reveal")?.addEventListener("click", reveal);
    document.querySelector("#again")?.addEventListener(
        "click",
        () => rate(false),
    );
    document.querySelector("#remember")?.addEventListener(
        "click",
        () => rate(true),
    );
}

function reveal() {
    if (revealed || busy) return;
    revealed = true;
    document.querySelector("#answer").hidden = false;
    document.querySelector("#recall-hint").hidden = true;
    document.querySelector("#flashcard").classList.add("is-revealed");
    document.querySelector("#controls").innerHTML = ratingButtons();
    document.querySelector(".keyboard-note").innerHTML =
        "<kbd>1</kbd> повторить <span>·</span> <kbd>2</kbd> помню";
    bindControls();
    document.querySelector("#again").focus({preventScroll: true});
}

function rate(remembered) {
    if (!revealed || busy) return;
    busy = true;
    document.querySelectorAll("#controls button").forEach((b) =>
        b.disabled = true
    );
    document.querySelector("#flashcard").classList.add(
        remembered ? "leave-right" : "leave-left",
    );
    session = answer(session, remembered);
    setTimeout(() => {
        busy = false;
        revealed = false;
        if (window.location.pathname === "/training") study(true);
    }, matchMedia("(prefers-reduced-motion: reduce)").matches ? 0 : 220);
}

async function complete() {
    const version = routeVersion;
    if (!completionSaved) {
        setView(
            '<section class="status-view"><h1>Сохраняем результат…</h1><p>Это займёт немного времени.</p></section>',
            "saving",
        );
        try {
            completionRequest ??= completeTraining({
                id: trainingId,
                results: session.cards.map((card, i) => ({
                    englishForm: card.englishForm,
                    difficult: session.difficult.includes(i),
                })),
            }).finally(() => {
                completionRequest = null;
            });
            await completionRequest;
            completionSaved = true;
        } catch (error) {
            if (version !== routeVersion) return;
            showError(
                error,
                complete,
                "Не удалось сохранить результат. Подход остаётся на странице; повторите отправку, прежде чем закрывать её.",
            );
            return;
        }
    }
    if (version !== routeVersion) return;
    setView(
        `<section class="completion enter"><h1>Тренировка завершена</h1><p>Результат сохранён.</p><div class="result-stats"><div><b>${session.cards.length}</b><span>закреплено</span></div><div><b>${session.difficult.length}</b><span>потребовали повторения</span></div></div><div class="complete-actions">${
            button("next", "Ещё один подход ↗")
        }${button("home", "На главную", "text-button")}</div></section>`,
        "complete",
    );
    document.querySelector("#next").onclick = goTraining;
    document.querySelector("#home").onclick = goHome;
}

document.addEventListener("keydown", (event) => {
    if (
        view !== "study" || busy || event.repeat || event.ctrlKey ||
        event.metaKey || event.altKey
    ) return;
    if (event.code === "Space" && !revealed) {
        event.preventDefault();
        reveal();
    } else if (
        revealed && ["Digit1", "Numpad1", "Digit2", "Numpad2"].includes(event.code)
    ) {
        event.preventDefault();
        rate(event.code.endsWith("2"));
    }
});

function showError(error, retry, message) {
    const unauthorized = error instanceof AuthenticationError;
    setView(
        `<section class="status-view enter"><h1>${
            unauthorized
                ? "Войдите в свой аккаунт"
                : "Не получилось связаться с сервером"
        }</h1><p>${
            unauthorized
                ? "Войдите в Scarif, затем вернитесь сюда и продолжите."
                : escape(message)
        }</p>${
            unauthorized
                ? `<p><a class="primary" href="${
                    SCARIF_API_URL.replace("//api.", "//").replace(":7001", ":7009")
                }" target="_blank" rel="noopener">Открыть Scarif ↗</a></p>`
                : ""
        }${button("retry", "Попробовать снова ↻")}</section>`,
        "error",
    );
    document.querySelector("#retry").onclick = retry;
}

async function load() {
    const version = routeVersion;
    setView(
        '<section class="status-view"><h1>Готовим карточки…</h1><p>Подбираем слова для повторения и примеры. Это может занять немного времени.</p></section>',
        "loading",
    );
    app.setAttribute("aria-busy", "true");
    try {
        trainingRequest ??= startTraining().finally(() => {
            trainingRequest = null;
        });
        const training = await trainingRequest;
        if (version !== routeVersion) return;
        if (!training.cards.length) {
            setView(
                `<section class="status-view"><h1>Здесь скоро появятся слова.</h1><p>Загрузите свой словарь привычным способом, затем начните тренировку.</p>${
                    button("retry", "Обновить ↻")
                }</section>`,
                "empty",
            );
            document.querySelector("#retry").onclick = load;
            return;
        }
        trainingId = training.id;
        session = createSession(training.cards);
        completionSaved = false;
        revealed = false;
        study(true);
    } catch (error) {
        if (version !== routeVersion) return;
        showError(error, load, "Проверьте соединение и попробуйте ещё раз.");
    }
}

router.start();
