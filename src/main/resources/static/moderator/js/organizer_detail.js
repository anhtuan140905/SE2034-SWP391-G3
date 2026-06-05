/**
 * organizer_detail.js
 * Path: src/main/resources/static/moderator/js/organizer_detail.js
 *
 * Handles:
 * - Approve / Reject organizer (POST)
 * - Reject reason validation
 * - Entrance animations
 * - Toast notification
 */

document.addEventListener('DOMContentLoaded', () => {

    /* ──────────────────────────────────────────
       1. APPROVE ORGANIZER
    ────────────────────────────────────────── */
    window.approveOrganizer = async (organizerId) => {
        if (!organizerId) return;

        const confirmed = confirm('Are you sure you want to APPROVE this organizer?');
        if (!confirmed) return;

        try {
            const res = await fetch(`/moderator/organizers/${organizerId}/approve`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [document.querySelector('meta[name="_csrf_header"]')?.content || 'X-CSRF-TOKEN']:
                        document.querySelector('meta[name="_csrf"]')?.content || ''
                }
            });

            if (res.ok) {
                showToast('Organizer approved successfully!', 'success');
                setTimeout(() => window.location.href = '/moderator/organizers', 1200);
            } else {
                showToast('Failed to approve. Please try again.', 'error');
            }
        } catch (err) {
            console.error('Approve error:', err);
            showToast('Network error. Please try again.', 'error');
        }
    };


    /* ──────────────────────────────────────────
       2. REJECT ORGANIZER
    ────────────────────────────────────────── */
    window.rejectOrganizer = async (organizerId) => {
        if (!organizerId) return;

        const textarea = document.getElementById('rejectReason');
        const reason   = textarea ? textarea.value.trim() : '';

        if (!reason) {
            textarea?.classList.add('textarea-error');
            textarea?.focus();
            showToast('Please provide a reason for rejection.', 'error');
            return;
        }
        textarea?.classList.remove('textarea-error');

        const confirmed = confirm('Are you sure you want to REJECT this organizer?');
        if (!confirmed) return;

        try {
            const res = await fetch(`/moderator/organizers/${organizerId}/reject`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [document.querySelector('meta[name="_csrf_header"]')?.content || 'X-CSRF-TOKEN']:
                        document.querySelector('meta[name="_csrf"]')?.content || ''
                },
                body: JSON.stringify({ reason })
            });

            if (res.ok) {
                showToast('Organizer rejected.', 'success');
                setTimeout(() => window.location.href = '/moderator/organizers', 1200);
            } else {
                showToast('Failed to reject. Please try again.', 'error');
            }
        } catch (err) {
            console.error('Reject error:', err);
            showToast('Network error. Please try again.', 'error');
        }
    };

    // Clear error on textarea input
    document.getElementById('rejectReason')?.addEventListener('input', function () {
        if (this.value.trim()) this.classList.remove('textarea-error');
    });


    /* ──────────────────────────────────────────
       3. TOAST NOTIFICATION
    ────────────────────────────────────────── */
    window.showToast = (message, type = 'success') => {
        document.querySelector('.od-toast')?.remove();

        const toast = document.createElement('div');
        toast.className = 'od-toast';
        toast.innerHTML = `
            <i class="fa-solid ${type === 'success' ? 'fa-circle-check' : 'fa-circle-xmark'}"></i>
            <span>${message}</span>
        `;

        const s = document.createElement('style');
        s.textContent = `
            .od-toast {
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
                animation: odToastIn 0.3s ease, odToastOut 0.3s ease 1.6s forwards;
            }
            @keyframes odToastIn  { from { opacity:0; transform:translateY(16px); } to { opacity:1; transform:translateY(0); } }
            @keyframes odToastOut { from { opacity:1; } to { opacity:0; transform:translateY(8px); } }
        `;
        document.head.appendChild(s);
        document.body.appendChild(toast);
        setTimeout(() => toast.remove(), 2000);
    };


    /* ──────────────────────────────────────────
       4. ENTRANCE ANIMATIONS
    ────────────────────────────────────────── */
    const animItems = [
        { sel: '.od-header',        delay: 0.00 },
        { sel: '.od-cards-row',     delay: 0.06 },
        { sel: '.od-reject-box',    delay: 0.12 },
        { sel: '.od-action-footer', delay: 0.16 },
    ];

    animItems.forEach(({ sel, delay }) => {
        const el = document.querySelector(sel);
        if (!el) return;
        el.style.opacity    = '0';
        el.style.transform  = 'translateY(14px)';
        el.style.transition = `opacity 0.4s ease ${delay}s, transform 0.4s ease ${delay}s`;
        requestAnimationFrame(() => requestAnimationFrame(() => {
            el.style.opacity   = '1';
            el.style.transform = 'translateY(0)';
        }));
    });

});