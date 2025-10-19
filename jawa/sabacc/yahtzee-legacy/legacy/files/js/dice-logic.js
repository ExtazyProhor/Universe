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

function simple(value) {
    const list = [];
    const count = rand(1, 4);
    for (let i = 0; i < count; i++) {
        list.push(`images/${value}-a.png`);
    }
    while (list.length < 5) {
        let val = value;
        while (val === value) {
            val = rand(1, 6);
        }
        list.push(`images/${val}-p.png`);
    }
    return list;
}

function count(countNeeded) {
    const list = [];
    const value = rand(1, 6);
    const usedValues = new Set([value]);

    for (let i = 0; i < countNeeded; i++) {
        list.push(`images/${value}-a.png`);
    }

    while (list.length < 5) {
        let val = value;
        while (usedValues.has(val)) {
            val = rand(1, 6);
        }
        usedValues.add(val);
        list.push(`images/${val}-p.png`);
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
        `images/${first}-a.png`,
        `images/${first}-a.png`,
        `images/${second}-a.png`,
        `images/${second}-a.png`,
        `images/${last}-p.png`
    ];
}

function fullHouse() {
    const first = rand(1, 6);
    const second = rand(1, 6);
    return [
        `images/${first}-a.png`,
        `images/${first}-a.png`,
        `images/${second}-a.png`,
        `images/${second}-a.png`,
        `images/${second}-a.png`
    ];
}

function smallStraight() {
    const start = rand(1, 3);
    const list = [];
    for (let i = start; i < start + 4; i++) {
        list.push(`images/${i}-a.png`);
    }
    list.push(`images/${start + rand(0, 3)}-p.png`);
    return list;
}

function largeStraight() {
    const start = rand(1, 2);
    const list = [];
    for (let i = start; i < start + 5; i++) {
        list.push(`images/${i}-a.png`);
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

    return list.map(i => `images/${i}-p.png`);
}

function updateDiceImages() {
    const rows = document.querySelectorAll('tr[data-category]');
    rows.forEach(row => {
        const category = row.dataset.category;
        if (categories[category]) {
            const images = categories[category]();
            const diceContainer = row.querySelector('.dice-images');
            if (diceContainer) {
                diceContainer.innerHTML = '';
                images.forEach(src => {
                    const img = document.createElement('img');
                    img.src = src;
                    img.alt = src.match(/(\d)/)?.[1] || '';
                    diceContainer.appendChild(img);
                });
            }
        }
    });
}
