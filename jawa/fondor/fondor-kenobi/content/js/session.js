export function createSession(words) {
    const cards = [...words];
    return {
        cards,
        queue: cards.map((_, i) => i),
        learned: [],
        difficult: [],
        answers: 0,
    };
}

export function answer(session, remembered) {
    if (!session.queue.length) return session;
    const next = {
        ...session,
        queue: [...session.queue],
        learned: [...session.learned],
        difficult: [...session.difficult],
        answers: session.answers + 1,
    };
    const id = next.queue.shift();
    if (remembered) next.learned.push(id);
    else {
        if (!next.difficult.includes(id)) next.difficult.push(id);
        next.queue.splice(Math.min(3, next.queue.length), 0, id);
    }
    return next;
}
