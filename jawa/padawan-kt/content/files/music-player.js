const trackListEl = document.getElementById("trackList");
const audio = document.getElementById("audio");
const nowPlaying = document.getElementById("nowPlaying");
const player = document.getElementById("player");
const searchInput = document.getElementById("search");
const playPauseBtn = document.getElementById("playPause");

const prevBtn = document.getElementById("prev");
const nextBtn = document.getElementById("next");

let tracks = [];
let filtered = [];
let currentIndex = -1;

let page = 0;
const size = 50;
let loading = false;
let allLoaded = false;

const preloader = new Audio();

async function loadTracks() {
    if (loading || allLoaded) return;

    loading = true;

    const res = await fetch(`/api/tracks?page=${page}&size=${size}`);
    const data = await res.json();

    if (data.length === 0) {
        allLoaded = true;
        return;
    }

    tracks = [...tracks, ...data];
    filtered = tracks;

    renderAppend(data);

    page++;
    loading = false;
}

function renderAppend(newTracks) {
    newTracks.forEach((track, index) => {
        const li = document.createElement("li");

        li.className = "track";

        li.textContent = `${track.authors.join(", ")} — ${track.name}`;

        const realIndex = tracks.length - newTracks.length + index;

        li.onclick = () => playTrack(realIndex);

        trackListEl.appendChild(li);
    });
}

function highlightActive() {
    document.querySelectorAll(".track").forEach((el, i) => {
        el.classList.toggle("active", i === currentIndex);
    });
}

function playTrack(index) {
    currentIndex = index;
    const track = filtered[index];

    audio.src = `/api/tracks/${track.track}`;
    audio.play();
    highlightActive();

    nowPlaying.textContent =
    `${track.authors.join(", ")} — ${track.name}`;

    player.classList.remove("hidden");

    preloadNext();
}

function preloadNext() {
    const nextTrack = filtered[currentIndex + 1];
    if (!nextTrack) return;

    preloader.src = `/api/tracks/${nextTrack.track}`;
}

function next() {
    if (currentIndex < filtered.length - 1) {
        playTrack(currentIndex + 1);
    }
}

function prev() {
    if (currentIndex > 0) {
        playTrack(currentIndex - 1);
    }
}

audio.addEventListener("ended", () => {
    next();
});

searchInput.addEventListener("input", () => {
    const q = searchInput.value.toLowerCase();

    filtered = tracks.filter(t =>
    t.name.toLowerCase().includes(q) ||
    t.authors.join(" ").toLowerCase().includes(q)
    );

    trackListEl.innerHTML = "";

    filtered.forEach((track, index) => {
        const li = document.createElement("li");

        const btn = document.createElement("button");
        btn.textContent = `${track.authors.join(", ")} — ${track.name}`;

        btn.onclick = () => playTrack(index);

        li.appendChild(btn);
        trackListEl.appendChild(li);
    });
});

nextBtn.onclick = next;
prevBtn.onclick = prev;

window.addEventListener("scroll", () => {
    const nearBottom =
    window.innerHeight + window.scrollY >=
    document.body.offsetHeight - 200;

    if (nearBottom) {
        loadTracks();
    }
});

playPauseBtn.onclick = () => {
    if (audio.paused) {
        audio.play();
    } else {
        audio.pause();
    }
};

audio.addEventListener("play", () => {
    playPauseBtn.textContent = "⏸";
});

audio.addEventListener("pause", () => {
    playPauseBtn.textContent = "▶";
});

loadTracks();
