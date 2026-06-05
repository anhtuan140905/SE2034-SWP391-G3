/**
 * event_detail.js
 * Path: src/main/resources/static/moderator/js/event_detail.js
 *
 * Handles:
 * - Approve event (POST)
 * - Reject event (POST with reason validation)
 * - Entrance animations
 */

document.addEventListener('DOMContentLoaded', () => {

    /* ──────────────────────────────────────────
       1. APPROVE EVENT
    ────────────────────────────────────────── */
    window.approveEvent = async (eventId) => {
        if (!eventId) return;

        const confirmed = confirm('Are you sure you want to APPROVE this event?');
        if (!confirmed) return;

        try {
            const res = await fetch(`/moderator/events/${eventId}/approve`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    // Spring Security CSRF — thymeleaf injects this via meta tag if configured
                    [document.querySelector('meta[name="_csrf_header"]')?.content || 'X-CSRF-TOKEN']:
                        document.querySelector('meta[name="_csrf"]')?.content || ''
                }
            });

            if (res.ok) {
                showToast('Event approved successfully!', 'success');
                setTimeout(() => window.location.href = '/moderator/events', 1200);
            } else {
                showToast('Failed to approve event. Please try again.', 'error');
            }
        } catch (err) {
            console.error('Approve error:', err);
            showToast('Network error. Please try again.', 'error');
        }
    };


    /* ──────────────────────────────────────────
       2. REJECT EVENT
    ────────────────────────────────────────── */
    window.rejectEvent = async (eventId) => {
        if (!eventId) return;

        const textarea = document.getElementById('rejectReason');
        const reason   = textarea ? textarea.value.trim() : '';

        if (!reason) {
            textarea?.classList.add('textarea-error');
            textarea?.focus();
            showToast('Please provide a reason for rejection.', 'error');
            return;
        }

        textarea?.classList.remove('textarea-error');

        const confirmed = confirm('Are you sure you want to REJECT this event?');
        if (!confirmed) return;

        try {
            const res = await fetch(`/moderator/events/${eventId}/reject`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [document.querySelector('meta[name="_csrf_header"]')?.content || 'X-CSRF-TOKEN']:
                        document.querySelector('meta[name="_csrf"]')?.content || ''
                },
                body: JSON.stringify({ reason })
            });

            if (res.ok) {
                showToast('Event rejected.', 'success');
                setTimeout(() => window.location.href = '/moderator/events', 1200);
            } else {
                showToast('Failed to reject event. Please try again.', 'error');
            }
        } catch (err) {
            console.error('Reject error:', err);
            showToast('Network error. Please try again.', 'error');
        }
    };


    /* ──────────────────────────────────────────
       3. TEXTAREA ERROR STYLE
    ────────────────────────────────────────── */
    const style = document.createElement('style');
    style.textContent = `
        .textarea-error {
            border-color: #E8336E !important;
            box-shadow: 0 0 0 3px rgba(232, 51, 110, 0.12) !important;
        }
    `;
    document.head.appendChild(style);

    document.getElementById('rejectReason')?.addEventListener('input', function () {
        if (this.value.trim()) this.classList.remove('textarea-error');
    });


    /* ──────────────────────────────────────────
       4. TOAST NOTIFICATION
    ────────────────────────────────────────── */
    window.showToast = (message, type = 'success') => {
        // Remove existing toast
        document.querySelector('.ed-toast')?.remove();

        const toast = document.createElement('div');
        toast.className = 'ed-toast';
        toast.innerHTML = `
            <i class="fa-solid ${type === 'success' ? 'fa-circle-check' : 'fa-circle-xmark'}"></i>
            <span>${message}</span>
        `;

        const toastStyle = document.createElement('style');
        toastStyle.textContent = `
            .ed-toast {
                position: fixed;
                bottom: 28px; right: 28px;
                display: flex; align-items: center; gap: 10px;
                background: ${type === 'success' ? '#1A9C5B' : '#C0225A'};
                color: #fff;
                padding: 12px 20px;
                border-radius: 10px;
                font-size: 13.5px;
                font-weight: 600;
                box-shadow: 0 6px 24px rgba(0,0,0,0.15);
                z-index: 9999;
                animation: toastIn 0.3s ease, toastOut 0.3s ease 1.6s forwards;
            }
            @keyframes toastIn  { from { opacity: 0; transform: translateY(16px); } to { opacity: 1; transform: translateY(0); } }
            @keyframes toastOut { from { opacity: 1; } to { opacity: 0; transform: translateY(8px); } }
        `;
        document.head.appendChild(toastStyle);
        document.body.appendChild(toast);

        setTimeout(() => toast.remove(), 2000);
    };


    /* ──────────────────────────────────────────
       5. ENTRANCE ANIMATION
    ────────────────────────────────────────── */
    const animateIn = (selector, delay = 0) => {
        const el = document.querySelector(selector);
        if (!el) return;
        el.style.opacity   = '0';
        el.style.transform = 'translateY(16px)';
        el.style.transition = `opacity 0.4s ease ${delay}s, transform 0.4s ease ${delay}s`;
        requestAnimationFrame(() => requestAnimationFrame(() => {
            el.style.opacity   = '1';
            el.style.transform = 'translateY(0)';
        }));
    };

    animateIn('.ed-title-block', 0.0);
    animateIn('.ed-cover',       0.08);
    animateIn('.ed-section',     0.14);
    animateIn('.ed-date-row',    0.18);
    animateIn('.ed-reject-box',  0.22);
    animateIn('.ed-sidebar',     0.10);

});