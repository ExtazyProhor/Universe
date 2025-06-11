const list = document.getElementById('service-list');

function render(services) {
    list.innerHTML = '';
    if (!services.length) {
        list.innerHTML = '<div>Нет активных сервисов</div>';
        return;
    }

    services.forEach(service => {
        const div = document.createElement('div');
        div.className = 'service';

        const name = document.createElement('div');
        name.className = 'service-name';
        name.textContent = service.name;

        const pid = document.createElement('div');
        pid.className = 'pid';
        pid.textContent = 'PID: ' + service.pid;

        const status = document.createElement('div');
        status.className = 'status ' + (service.alive ? 'alive' : 'dead');
        status.textContent = service.alive ? '🟢 Активен' : '🔴 Выключен';

        div.appendChild(name);
        div.appendChild(pid);
        div.appendChild(status);

        list.appendChild(div);
    });
}

const sse = new EventSource('/stream');
sse.onmessage = (e) => {
    try {
        const data = JSON.parse(e.data);
        render(data);
    } catch (err) {
        console.error('Ошибка при обработке SSE:', err);
    }
};

sse.onerror = () => {
    list.innerHTML = '<div style="color:red;">Потеряно соединение с сервером</div>';
};

document.getElementById('start-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const name = document.getElementById('service-name').value;
    const path = document.getElementById('service-path').value;

    try {
        const res = await fetch('/services/start?name=' + encodeURIComponent(name) + '&path=' + encodeURIComponent(path), {
            method: 'POST'
        });
        const text = await res.text();
        alert('Ответ: ' + text);
    } catch (err) {
        alert('Ошибка запуска: ' + err);
    }
});

document.getElementById('stop-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const name = document.getElementById('stop-name').value;

    try {
        const res = await fetch('/services/stop?name=' + encodeURIComponent(name), {
            method: 'POST'
        });
        const text = await res.text();
        alert('Ответ: ' + text);
    } catch (err) {
        alert('Ошибка остановки: ' + err);
    }
});
