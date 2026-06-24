document.addEventListener('DOMContentLoaded', function () {
    const toggleBtn = document.querySelector('[data-toggle-password]');
    const passwordInput = document.getElementById('password');

    if (!toggleBtn || !passwordInput) {
        return;
    }

    toggleBtn.addEventListener('click', function () {
        const isHidden = passwordInput.getAttribute('type') === 'password';

        passwordInput.setAttribute('type', isHidden ? 'text' : 'password');
        toggleBtn.innerHTML = isHidden
            ? '<i class="fa-regular fa-eye-slash"></i>'
            : '<i class="fa-regular fa-eye"></i>';

        toggleBtn.setAttribute(
            'aria-label',
            isHidden ? 'Ẩn mật khẩu' : 'Hiện mật khẩu'
        );
    });
});