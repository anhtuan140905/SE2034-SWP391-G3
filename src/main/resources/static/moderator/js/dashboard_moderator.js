/**
 * dashboard_moderator.js
 * Path: src/main/resources/static/moderator/js/dashboard_moderator.js
 *
 * Handles:
 * - Notification bell toggle
 * - Stat counter animation on load
 * - Activity badge live-pulse effect
 */

document.addEventListener('DOMContentLoaded', () => {

    /* ──────────────────────────────────────────
       1. NOTIFICATION DROPDOWN TOGGLE
    ────────────────────────────────────────── */
    const notifToggle   = document.getElementById('notifToggle');
    const notifDropdown = document.getElementById('notifDropdown');

    if (notifToggle && notifDropdown) {
        notifToggle.addEventListener('click', (e) => {
            e.stopPropagation();
            notifDropdown.classList.toggle('open');
        });

        // Close when clicking outside
        document.addEventListener('click', (e) => {
            if (!notifDropdown.contains(e.target) && e.target !== notifToggle) {
                notifDropdown.classList.remove('open');
            }
        });
    }


    /* ──────────────────────────────────────────
       2. STAT COUNTER ANIMATION
    ────────────────────────────────────────── */
    const statValues = document.querySelectorAll('.stat-value');

    const easeOut = (t) => 1 - Math.pow(1 - t, 3);

    const animateCounter = (el) => {
        const target = parseInt(el.textContent.replace(/,/g, ''), 10);
        if (isNaN(target)) return;

        const duration = 1200; // ms
        const start    = performance.now();

        const update = (now) => {
            const elapsed  = now - start;
            const progress = Math.min(elapsed / duration, 1);
            const eased    = easeOut(progress);
            const current  = Math.round(eased * target);

            el.textContent = current.toLocaleString('en-US');

            if (progress < 1) requestAnimationFrame(update);
            else el.textContent = target.toLocaleString('en-US');
        };

        requestAnimationFrame(update);
    };

    // Trigger animation using IntersectionObserver
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                animateCounter(entry.target);
                observer.unobserve(entry.target);
            }
        });
    }, { threshold: 0.2 });

    statValues.forEach(el => observer.observe(el));


    /* ──────────────────────────────────────────
       3. ACTIVITY ITEM STAGGERED ENTRANCE
    ────────────────────────────────────────── */
    const activityItems = document.querySelectorAll('.activity-item');

    activityItems.forEach((item, i) => {
        item.style.opacity  = '0';
        item.style.transform = 'translateY(12px)';
        item.style.transition = `opacity 0.4s ease ${i * 0.08}s, transform 0.4s ease ${i * 0.08}s`;

        // Trigger reflow then animate in
        requestAnimationFrame(() => {
            requestAnimationFrame(() => {
                item.style.opacity  = '1';
                item.style.transform = 'translateY(0)';
            });
        });
    });


    /* ──────────────────────────────────────────
       4. PENDING BADGE PULSE (for PENDING status)
    ────────────────────────────────────────── */
    const pendingBadges = document.querySelectorAll('.badge-pending');
    pendingBadges.forEach(badge => {
        badge.style.animation = 'pendingPulse 2s ease-in-out infinite';
    });

    // Inject keyframes dynamically
    const styleSheet = document.createElement('style');
    styleSheet.textContent = `
        @keyframes pendingPulse {
            0%, 100% { opacity: 1; }
            50%       { opacity: 0.6; }
        }
    `;
    document.head.appendChild(styleSheet);


    /* ──────────────────────────────────────────
       5. MOBILE SIDEBAR TOGGLE (optional)
    ────────────────────────────────────────── */
    const sidebarToggle = document.getElementById('sidebarToggle');
    const sidebar       = document.querySelector('.mod-sidebar');

    if (sidebarToggle && sidebar) {
        sidebarToggle.addEventListener('click', () => {
            sidebar.classList.toggle('open');
        });

        // Close sidebar on outside click (mobile)
        document.addEventListener('click', (e) => {
            if (sidebar.classList.contains('open')
                && !sidebar.contains(e.target)
                && e.target !== sidebarToggle) {
                sidebar.classList.remove('open');
            }
        });
    }

});