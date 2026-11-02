import {answer, createSession} from "../content/js/session.js";
import {renderTranslation} from "../content/js/translation.js";

const assert = (condition) => {
    if (!condition) throw new Error("Assertion failed");
};
const words = Array.from(
    {length: 5},
    (_, i) => ({
        englishForm: `word ${i}`,
        translates: ["перевод"],
        translations: [],
    }),
);
Deno.test("session uses the exact server selection and retains meaning groups", () => {
    const cards = [{
        ...words[0],
        translations: [{
            translation: ["смысл", "значение"],
            example: "example",
            exampleTranslation: "пример",
        }],
    }, words[1]];
    const session = createSession(cards);
    assert(JSON.stringify(session.cards) === JSON.stringify(cards));
    assert(session.queue.join(",") === "0,1");
});
Deno.test("difficult card returns after three others and is reported once", () => {
    const first = createSession(words);
    let current = answer(first, false);
    assert(first.queue[0] === 0);
    assert(current.queue.join(",") === "1,2,3,0,4");
    while (current.queue.length) current = answer(current, true);
    assert(
        current.learned.length === 5 && current.answers === 6 &&
        current.difficult.length === 1,
    );
});
Deno.test("one difficult card remains until remembered", () => {
    let current = createSession(words.slice(0, 1));
    current = answer(answer(current, false), false);
    assert(current.queue.join(",") === "0" && current.difficult.length === 1);
    assert(answer(current, true).queue.length === 0);
});
Deno.test("meaning groups render examples and escape every external field, with legacy fallback", () => {
    const html = renderTranslation({
        translations: [{
            translation: ["a < b", "c & d"],
            example: "<script>x</script>",
            exampleTranslation: '"пример"',
        }],
        translates: ["old"],
    });
    assert(
        html.includes("meaning-group") && html.includes("a &lt; b, c &amp; d") &&
        html.includes("&lt;script&gt;") &&
        html.includes("&quot;пример&quot;") && !html.includes("<script>"),
    );
    assert(
        renderTranslation({translations: [], translates: ["старый <перевод>"]})
            .includes("старый &lt;перевод&gt;"),
    );
});
