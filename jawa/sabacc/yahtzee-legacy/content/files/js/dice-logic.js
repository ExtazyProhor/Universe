const categories = {
    'units': () => simple(1),
    'twos': () => simple(2),
    'threes': () => simple(3),
    'fours': () => simple(4),
    'fives': () => simple(5),
    'sixes': () => simple(6),
    'pair': () => count(2),
    'two-pairs': () => twoPairs(),
    'three-of-kind': () => count(3),
    'four-of-kind': () => count(4),
    'full-house': () => fullHouse(),
    'small-straight': () => smallStraight(),
    'large-straight': () => largeStraight(),
    'yahtzee': () => count(5),
    'chance': () => chance()
};

function rand(fromInclusive, toInclusive) {
    return fromInclusive + Math.floor(Math.random() * (toInclusive - fromInclusive + 1));
}

function createDice(value, isActive) {
    return { value, isActive };
}

function simple(value) {
    const list = [];
    const count = rand(1, 4);
    for (let i = 0; i < count; i++) {
        list.push(createDice(value, true));
    }
    while (list.length < 5) {
        let val = value;
        while (val === value) {
            val = rand(1, 6);
        }
        list.push(createDice(val, false));
    }
    return list;
}

function count(countNeeded) {
    const list = [];
    const value = rand(1, 6);
    const usedValues = new Set([value]);
    for (let i = 0; i < countNeeded; i++) {
        list.push(createDice(value, true));
    }
    while (list.length < 5) {
        let val = value;
        while (usedValues.has(val)) {
            val = rand(1, 6);
        }
        usedValues.add(val);
        list.push(createDice(val, false));
    }
    return list;
}

function twoPairs() {
    const first = rand(1, 6);
    const second = rand(1, 6);
    let last = second;
    while (last === first || last === second) {
        last = rand(1, 6);
    }
    return [
        createDice(first, true),
        createDice(first, true),
        createDice(second, true),
        createDice(second, true),
        createDice(last, false)
    ];
}

function fullHouse() {
    const first = rand(1, 6);
    const second = rand(1, 6);
    return [
        createDice(first, true),
        createDice(first, true),
        createDice(second, true),
        createDice(second, true),
        createDice(second, true)
    ];
}

function smallStraight() {
    const start = rand(1, 3);
    const list = [];
    for (let i = start; i < start + 4; i++) {
        list.push(createDice(i, true));
    }
    list.push(createDice(start + rand(0, 3), false));
    return list;
}

function largeStraight() {
    const start = rand(1, 2);
    const list = [];
    for (let i = start; i < start + 5; i++) {
        list.push(createDice(i, true));
    }
    return list;
}

function chance() {
    const list = [1, 2, 3, 4, 5, 6];
    list.splice(rand(2, 3), 1);
    for (let i = list.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [list[i], list[j]] = [list[j], list[i]];
    }
    return list.map(i => createDice(i, false));
}

function renderDice(dice) {
    const diceEl = document.createElement('div');
    diceEl.className = `dice ${dice.isActive ? 'active' : 'passive'}`;
    diceEl.setAttribute('data-value', dice.value);

    for (let i = 0; i < 9; i++) {
        const dot = document.createElement('div');
        dot.className = 'dice-dot empty';
        diceEl.appendChild(dot);
    }

    return diceEl;
}

function updateDiceImages() {
    const rows = document.querySelectorAll('tr[data-category]');
    rows.forEach(row => {
        const category = row.dataset.category;
        if (categories[category]) {
            const diceList = categories[category]();
            const diceContainer = row.querySelector('.dice-images');
            if (diceContainer) {
                diceContainer.innerHTML = '';
                diceList.forEach(dice => {
                    const diceEl = renderDice(dice);
                    diceContainer.appendChild(diceEl);
                });
            }
        }
    });
}
