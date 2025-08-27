class LoginForm {
    constructor() {
        this.form = document.getElementById('loginForm');
        this.loginInput = document.getElementById('login');
        this.passwordInput = document.getElementById('password');
        this.submitButton = document.getElementById('submitButton');
        this.loadingSpinner = document.getElementById('loadingSpinner');
        this.buttonText = document.getElementById('buttonText');
        this.togglePassword = document.getElementById('togglePassword');

        this.isSubmitting = false;
        this.validators = {
            login: this.validateLogin.bind(this),
            password: this.validatePassword.bind(this)
        };

        this.init();
    }

    init() {
        this.form.addEventListener('submit', this.handleSubmit.bind(this));

        this.loginInput.addEventListener('input', () => this.validateField('login'));
        this.passwordInput.addEventListener('input', () => this.validateField('password'));

        this.togglePassword.addEventListener('click', this.togglePasswordVisibility.bind(this));
    }

    validateLogin(value) {
        const errors = [];

        if (!value) {
            errors.push('Email или имя пользователя обязательно');
            return errors;
        }

        if (value.includes('@')) {
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(value)) {
                errors.push('Введите корректный email адрес');
            }
        } else {
            if (value.length < 3 || value.length > 16) {
                errors.push('Имя пользователя должно содержать от 3 до 16 символов');
            } else if (!/^[a-zA-Z0-9]+$/.test(value)) {
                errors.push('Имя пользователя должно содержать только буквы и цифры');
            }
        }

        return errors;
    }

    validatePassword(value) {
        const errors = [];

        if (!value) {
            errors.push('Пароль обязателен');
            return errors;
        }

        if (value.length < 8 || value.length > 64) {
            errors.push('Пароль должен быть от 8 до 64 символов');
        }

        if (!/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/.test(value)) {
            errors.push('Пароль должен содержать заглавную букву, строчную букву и цифру');
        }

        return errors;
    }

    validateField(fieldName) {
        const input = document.getElementById(fieldName);
        const errorElement = document.getElementById(fieldName + 'Error');
        const errors = this.validators[fieldName](input.value);

        this.showFieldErrors(input, errorElement, errors);
        return errors.length === 0;
    }

    showFieldErrors(input, errorElement, errors) {
        if (errors.length > 0) {
            input.classList.add('error');
            errorElement.textContent = errors[0];
            errorElement.classList.add('show');
        } else {
            input.classList.remove('error');
            errorElement.classList.remove('show');
        }
    }

    validateForm() {
        const loginValid = this.validateField('login');
        const passwordValid = this.validateField('password');

        return loginValid && passwordValid;
    }

    async handleSubmit(e) {
        e.preventDefault();

        if (this.isSubmitting) return;

        if (!this.validateForm()) {
            const firstErrorField = this.form.querySelector('.form-input.error');
            if (firstErrorField) {
                firstErrorField.focus();
            }
            return;
        }

        this.setSubmittingState(true);

        const formData = {
            login: this.loginInput.value,
            password: this.passwordInput.value
        };

        try {
            const response = await universePost('scarif', 'api/auth/login', formData);
            await this.handleResponse(response);
        } catch (error) {
            console.error('Ошибка входа:', error);
            this.showNotification('Произошла ошибка при входе', 'error');
        } finally {
            this.setSubmittingState(false);
        }
    }

    async handleResponse(response) {
        if (response.ok) {
            this.showNotification('Вход выполнен успешно! Добро пожаловать!', 'success');
            setTimeout(() => {
                window.location.href = '/';
            }, 2000);
            return;
        }

        const data = await response.json().catch(() => ({}));

        switch (response.status) {
            case 400:
                this.handleValidationErrors(data);
                break;
            case 401:
                this.showNotification('Неверные данные для входа', 'error');
                break;
            case 409:
                this.showNotification('Вы уже авторизованы', 'error');
                break;
            case 429:
                this.showNotification('Слишком много попыток входа. Попробуйте позже', 'error');
                break;
            default:
                this.showNotification('Произошла ошибка при входе', 'error');
        }
    }

    handleValidationErrors(data) {
        if (data.errorMessage) {
            this.showNotification(data.errorMessage, 'error');
        } else {
            this.showNotification('Неверные данные для входа', 'error');
        }
    }

    setSubmittingState(isSubmitting) {
        this.isSubmitting = isSubmitting;
        this.submitButton.disabled = isSubmitting;

        if (isSubmitting) {
            this.loadingSpinner.style.display = 'inline-block';
            this.buttonText.textContent = 'Вход...';
        } else {
            this.loadingSpinner.style.display = 'none';
            this.buttonText.textContent = 'Войти';
        }
    }

    togglePasswordVisibility() {
        const isPassword = this.passwordInput.type === 'password';
        this.passwordInput.type = isPassword ? 'text' : 'password';

        const eyeIcon = this.togglePassword.querySelector('svg');
        if (isPassword) {
            eyeIcon.innerHTML = `
                <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path>
                <line x1="1" y1="1" x2="23" y2="23"></line>
            `;
        } else {
            eyeIcon.innerHTML = `
                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                <circle cx="12" cy="12" r="3"></circle>
            `;
        }
    }

    showNotification(message, type) {
        const existingNotifications = document.querySelectorAll('.notification');
        existingNotifications.forEach(n => n.remove());

        const notification = document.createElement('div');
        notification.className = `notification ${type}`;
        notification.textContent = message;

        document.body.appendChild(notification);

        setTimeout(() => notification.classList.add('show'), 100);

        setTimeout(() => {
            notification.classList.remove('show');
            setTimeout(() => notification.remove(), 300);
        }, 5000);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    new LoginForm();
});
