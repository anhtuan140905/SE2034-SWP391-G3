/**
 * EventHub — Footer Scripts
 * Phạm vi: newsletter subscription form, toast notification helper,
 *           current-year setter
 */

document.addEventListener('DOMContentLoaded', () => {

    // 1. Newsletter subscription form
    const newsletterForm = document.querySelector('.newsletter-form');
    if (newsletterForm) {
        newsletterForm.addEventListener('submit', (e) => {
            e.preventDefault();
            const emailInput = newsletterForm.querySelector('.newsletter-input');
            const submitBtn  = newsletterForm.querySelector('.btn-newsletter-submit');

            if (emailInput && emailInput.value.trim() !== '') {
                const subbedEmail = emailInput.value;
                const originalContent = submitBtn.innerHTML;

                submitBtn.innerHTML = '<i class="fa-solid fa-circle-check text-white"></i>';
                emailInput.value = '';
                emailInput.placeholder = 'Subscribed successfully!';
                emailInput.disabled = true;

                showEventToast(
                    'Subscribed!',
                    `Awesome! Event updates will now be sent to <strong>${subbedEmail}</strong>.`,
                    'success'
                );

                setTimeout(() => {
                    submitBtn.innerHTML = originalContent;
                    emailInput.disabled = false;
                    emailInput.placeholder = 'Your email address';
                }, 4000);
            }
        });
    }

    // 2. Current year setter
    const currentYearEl = document.getElementById('currentYear');
    if (currentYearEl) {
        currentYearEl.textContent = new Date().getFullYear();
    }

});

// ----------------------------------------------------------------
// Toast Notification Helper  (global — cần thiết cho cả footer lẫn home)
// Khai báo ở đây để footer.js tự đủ; home.js sẽ dùng lại nếu footer.js
// được load trước hoặc cùng trang.
// ----------------------------------------------------------------
window.showEventToast = function (title, message, type = 'info') {
    let toastContainer = document.querySelector('.eventhub-toast-container');
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.className = 'eventhub-toast-container';
        Object.assign(toastContainer.style, {
            position: 'fixed',
            bottom: '2rem',
            right: '2rem',
            zIndex: '9999',
            display: 'flex',
            flexDirection: 'column',
            gap: '0.5rem',
        });
        document.body.appendChild(toastContainer);
    }

    const borderColors = {
        success: 'rgba(0, 240, 255, 0.6)',
        danger:  'rgba(255, 42, 122, 0.6)',
        info:    'rgba(140, 43, 255, 0.6)',
    };

    const toast = document.createElement('div');
    Object.assign(toast.style, {
        background:    'rgba(15, 2, 36, 0.95)',
        border:        '1px solid rgba(255, 42, 122, 0.2)',
        borderLeft:    `4px solid ${borderColors[type] || borderColors.info}`,
        borderRadius:  '12px',
        minWidth:      '300px',
        boxShadow:     '0 10px 30px rgba(0,0,0,0.5)',
        transition:    'all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.25)',
        transform:     'translateY(50px)',
        opacity:       '0',
        padding:       '12px',
    });

    toast.innerHTML = `
        <div class="d-flex justify-content-between align-items-center mb-1">
            <strong class="text-white" style="font-family:'Space Grotesk',sans-serif;">
                <i class="fa-solid fa-bell text-pink-glow me-2"></i>${title}
            </strong>
            <button type="button" class="btn-close btn-close-white" style="font-size:.75rem;"
                    onclick="this.closest('.eventhub-toast').remove()"></button>
        </div>
        <div class="text-secondary-light" style="font-size:.85rem;font-family:'Outfit',sans-serif;">${message}</div>
    `;
    toast.classList.add('eventhub-toast');

    toastContainer.appendChild(toast);

    // Animate in
    requestAnimationFrame(() => {
        setTimeout(() => {
            toast.style.transform = 'translateY(0)';
            toast.style.opacity   = '1';
        }, 20);
    });

    // Auto-remove after 5 s
    setTimeout(() => {
        toast.style.transform = 'translateY(-20px)';
        toast.style.opacity   = '0';
        setTimeout(() => toast.remove(), 300);
    }, 5000);
};