class ProfilePage {
    constructor() {
        this.logoutButton = document.getElementById('logoutButton');
        this.init();
    }

    init() {
        this.logoutButton.addEventListener('click', this.handleLogout.bind(this));
    }

    async handleLogout() {
        try {
            const response = await universeFetch('/api/auth/logout', {
                method: 'POST',
            });

            if (response.ok) {
                window.location.href = '/login';
                return;
            }

            if (response.status === 401) {
                window.location.href = '/login';
                return;
            }

            console.error('Logout failed', response.status);
        } catch (e) {
            console.error('Logout error', e);
        }
    }
}

document.addEventListener('DOMContentLoaded', async () => {
    const ok = await window.initAuth();

    if (!ok) {
        window.location.href = '/login';
        return;
    }

    new ProfilePage();
});
