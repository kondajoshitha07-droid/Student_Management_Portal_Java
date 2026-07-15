document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('loginForm');
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');
    const usernameError = document.getElementById('usernameError');
    const passwordError = document.getElementById('passwordError');
    const serverError = document.getElementById('serverError');
    const loginBtn = document.getElementById('loginBtn');
    const btnText = document.getElementById('btnText');
    const btnLoader = document.getElementById('btnLoader');

    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        // Reset errors
        usernameError.style.display = 'none';
        passwordError.style.display = 'none';
        serverError.style.display = 'none';
        usernameInput.classList.remove('error-input');
        passwordInput.classList.remove('error-input');

        const role = document.querySelector('input[name="role"]:checked').value;
        const username = usernameInput.value.trim();
        const password = passwordInput.value.trim();

        let isValid = true;

        if (!username) {
            usernameError.style.display = 'block';
            usernameInput.classList.add('error-input');
            isValid = false;
        }

        if (!password) {
            passwordError.style.display = 'block';
            passwordInput.classList.add('error-input');
            isValid = false;
        }

        if (!isValid) return;

        // UI Loading State
        loginBtn.disabled = true;
        btnText.style.display = 'none';
        btnLoader.style.display = 'inline-block';

        const payload = {
            role: role,
            username: username,
            password: password
        };

        try {
            const response = await fetch('/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(payload)
            });

            const data = await response.json();

            if (data.success) {
                localStorage.setItem('sessionId', data.sessionId);
                localStorage.setItem('role', data.role);
                if (data.role === 'admin') {
                    window.location.href = '/admin.html';
                } else {
                    window.location.href = '/student.html';
                }
            } else {
                serverError.textContent = data.message || 'Invalid Credentials';
                serverError.style.display = 'block';
                // Reset Button
                loginBtn.disabled = false;
                btnText.style.display = 'inline-block';
                btnLoader.style.display = 'none';
            }
        } catch (error) {
            serverError.textContent = 'A network error occurred. Please try again.';
            serverError.style.display = 'block';
            // Reset Button
            loginBtn.disabled = false;
            btnText.style.display = 'inline-block';
            btnLoader.style.display = 'none';
        }
    });
});
