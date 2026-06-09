/**
 * event_management.js
 * Path: src/main/resources/static/moderator/js/event_management.js
 *
 * Handles:
 * - Live search with debounce → submit form to server
 * - Status filter change → auto submit
 * - Table row entrance animation
 * - Stat counter animation
 */

document.addEventListener('DOMContentLoaded', () => {

    /* ──────────────────────────────────────────
       1. SEARCH + FILTER: auto-submit with debounce
    ────────────────────────────────────────── */
    const searchInput  = document.getElementById('searchInput');
    const statusSelect = document.getElementById('statusSelect');
    const categorySelect = document.getElementById('categorySelect');

    /**
     * Build URL with current keyword + status then navigate
     */
    const applyFilters = () => {
        const keyword = searchInput ? searchInput.value.trim() : '';
        const status  = statusSelect ? statusSelect.value : '';
        const categoryId = categorySelect ? categorySelect.value : '';

        const url     = new URL(window.location.href);

        url.searchParams.set('page', '0');
        if (keyword) url.searchParams.set('keyword', keyword);
        else url.searchParams.delete('keyword');

        if (status) url.searchParams.set('status', status);
        else url.searchParams.delete('status');

        if (categoryId) url.searchParams.set('categoryId', categoryId);
        else url.searchParams.delete('categoryId');

        window.location.href = url.toString();
    };

    // Debounce helper
    const debounce = (fn, delay) => {
        let timer;
        return (...args) => {
            clearTimeout(timer);
            timer = setTimeout(() => fn(...args), delay);
        };
    };

    if (searchInput) {
        searchInput.addEventListener('input', debounce(applyFilters, 500));
        // Also submit on Enter
        searchInput.addEventListener('keydown', (e) => {
            if (e.key === 'Enter') applyFilters();
        });
    }

    if (statusSelect) {
        statusSelect.addEventListener('change', applyFilters);
    }

    if(categorySelect) {
        categorySelect.addEventListener('change', applyFilters);
    }
    
    /* ──────────────────────────────────────────
       2. TABLE ROW STAGGERED ENTRANCE ANIMATION
    ────────────────────────────────────────── */
    const rows = document.querySelectorAll('.em-table tbody tr');

    rows.forEach((row, i) => {
        row.style.opacity   = '0';
        row.style.transform = 'translateY(10px)';
        row.style.transition = `opacity 0.35s ease ${i * 0.06}s, transform 0.35s ease ${i * 0.06}s`;

        requestAnimationFrame(() => {
            requestAnimationFrame(() => {
                row.style.opacity   = '1';
                row.style.transform = 'translateY(0)';
            });
        });
    });


    /* ──────────────────────────────────────────
       3. STAT COUNTER ANIMATION
    ────────────────────────────────────────── */
    const easeOut = (t) => 1 - Math.pow(1 - t, 3);

    const animateCounter = (el) => {
        const target   = parseInt(el.textContent.replace(/,/g, ''), 10);
        if (isNaN(target)) return;
        const duration = 1000;
        const start    = performance.now();

        const update = (now) => {
            const elapsed  = now - start;
            const progress = Math.min(elapsed / duration, 1);
            el.textContent = Math.round(easeOut(progress) * target).toLocaleString('en-US');
            if (progress < 1) requestAnimationFrame(update);
            else el.textContent = target.toLocaleString('en-US');
        };
        requestAnimationFrame(update);
    };

    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                animateCounter(entry.target);
                observer.unobserve(entry.target);
            }
        });
    }, { threshold: 0.3 });

    document.querySelectorAll('.em-stat-value').forEach(el => observer.observe(el));

});