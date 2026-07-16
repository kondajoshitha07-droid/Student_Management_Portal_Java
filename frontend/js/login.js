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
                if (data.role === 'faculty' || data.role === 'principal') {
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

    // Registration UI Switching
    const showRegisterBtn = document.getElementById('showRegisterBtn');
    const showLoginBtn = document.getElementById('showLoginBtn');
    const registerForm = document.getElementById('registerForm');
    const roleGroup = document.querySelector('.role-group');

    if (showRegisterBtn && showLoginBtn && registerForm) {
        showRegisterBtn.addEventListener('click', (e) => {
            e.preventDefault();
            loginForm.style.display = 'none';
            roleGroup.style.display = 'none';
            registerForm.style.display = 'block';
            serverError.style.display = 'none';
        });

        showLoginBtn.addEventListener('click', (e) => {
            e.preventDefault();
            registerForm.style.display = 'none';
            loginForm.style.display = 'block';
            roleGroup.style.display = 'flex';
            serverError.style.display = 'none';
        });

        // Registration Submission
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            serverError.style.display = 'none';
            const password = document.getElementById('regPassword').value;
            const confirm = document.getElementById('regConfirmPassword').value;
            const pwError = document.getElementById('regPasswordError');

            if (password !== confirm) {
                pwError.style.display = 'block';
                return;
            }
            pwError.style.display = 'none';

            const payload = {
                name: document.getElementById('regName').value,
                rollNo: document.getElementById('regRollNo').value,
                email: document.getElementById('regEmail').value,
                department: document.getElementById('regDept').value,
                year: parseInt(document.getElementById('regYear').value) || 1,
                passwordHash: password
            };

            const regBtn = document.getElementById('registerBtn');
            const regBtnText = document.getElementById('regBtnText');
            const regBtnLoader = document.getElementById('regBtnLoader');

            regBtn.disabled = true;
            regBtnText.style.display = 'none';
            regBtnLoader.style.display = 'inline-block';

            try {
                const response = await fetch('/students', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });
                const data = await response.json();
                
                regBtn.disabled = false;
                regBtnText.style.display = 'inline-block';
                regBtnLoader.style.display = 'none';

                if (data.success) {
                    alert('Registration successful! Please login.');
                    showLoginBtn.click();
                    registerForm.reset();
                } else {
                    serverError.textContent = data.message || 'Registration failed.';
                    serverError.style.display = 'block';
                }
            } catch (err) {
                regBtn.disabled = false;
                regBtnText.style.display = 'inline-block';
                regBtnLoader.style.display = 'none';
                serverError.textContent = 'Network error during registration.';
                serverError.style.display = 'block';
            }
        });
    }
});
