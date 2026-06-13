/**
 * EventHub — Home Page Scripts
 * Phạm vi: stat counters, category filter tabs, smooth anchor scroll,
 *           booking button interactions, follow organizer toggle, pagination
 *
 * Phụ thuộc: footer.js phải được load trước (cung cấp window.showEventToast)
 */

document.addEventListener('DOMContentLoaded', () => {

    // ----------------------------------------------------------------
    // 1. Statistical Counter Animation (lazy — triggered on scroll)
    // ----------------------------------------------------------------
    const statsSection = document.getElementById('statisticsSection');
    const counters     = document.querySelectorAll('.stat-number');

    if (statsSection && counters.length > 0) {
        let animated = false;

        const countUp = (counter) => {
            const target   = parseInt(counter.getAttribute('data-target'), 10) || 0;
            const prefix   = counter.getAttribute('data-prefix') || '';
            const suffix   = counter.getAttribute('data-suffix') || '';
            const duration = 2000;
            const startTime = performance.now();

            const update = (now) => {
                const progress = Math.min((now - startTime) / duration, 1);
                const eased    = progress * (2 - progress);           // ease-out quad
                counter.textContent = `${prefix}${Math.floor(eased * target).toLocaleString()}${suffix}`;

                if (progress < 1) {
                    requestAnimationFrame(update);
                } else {
                    counter.textContent = `${prefix}${target.toLocaleString()}${suffix}`;
                }
            };

            requestAnimationFrame(update);
        };

        const observer = new IntersectionObserver((entries) => {
            entries.forEach((entry) => {
                if (entry.isIntersecting && !animated) {
                    counters.forEach(countUp);
                    animated = true;
                    observer.unobserve(statsSection);
                }
            });
        }, { threshold: 0.2 });

        observer.observe(statsSection);
    }

    // ----------------------------------------------------------------
    // 2. Client-side Category Filter Tabs
    // ----------------------------------------------------------------
    const filterButtons = document.querySelectorAll('.btn-cat-filter');
    const eventCards    = document.querySelectorAll('.event-list-item');

    if (filterButtons.length > 0 && eventCards.length > 0) {
        filterButtons.forEach((button) => {
            button.addEventListener('click', (e) => {
                e.preventDefault();

                filterButtons.forEach((btn) => btn.classList.remove('active'));
                button.classList.add('active');

                const target = button.getAttribute('data-category');

                eventCards.forEach((card) => {
                    const category = card.getAttribute('data-category');
                    const visible  = target === 'all' || category === target;

                    if (visible) {
                        card.style.display = 'block';
                        setTimeout(() => {
                            card.style.opacity   = '1';
                            card.style.transform = 'scale(1)';
                        }, 50);
                    } else {
                        card.style.opacity   = '0';
                        card.style.transform = 'scale(0.95)';
                        setTimeout(() => { card.style.display = 'none'; }, 300);
                    }
                });
            });
        });
    }

    // ----------------------------------------------------------------
    // 3. Smooth Anchor Scroll
    // ----------------------------------------------------------------
    document.querySelectorAll('a[href^="#"]').forEach((anchor) => {
        anchor.addEventListener('click', function (e) {
            const dest = this.getAttribute('href');
            if (dest !== '#' && dest.startsWith('#')) {
                e.preventDefault();
                const target = document.querySelector(dest);
                if (target) {
                    target.scrollIntoView({ behavior: 'smooth', block: 'start' });
                }
            }
        });
    });

    // ----------------------------------------------------------------
    // 4. Booking Button Interactions
    // ----------------------------------------------------------------
    document.querySelectorAll('.btn-book-now').forEach((button) => {
        button.addEventListener('click', (e) => {
            e.preventDefault();
            const card = button.closest('.card-event');
            if (card) {
                const eventName = card.querySelector('.event-card-title a')?.textContent ?? 'this event';
                showEventToast(
                    'Ticket Request Received',
                    `We are preparing your passes for <strong>${eventName}</strong>. Check your inbox!`,
                    'info'
                );
            }
        });
    });

    // ----------------------------------------------------------------
    // 5. Follow / Unfollow Organizer Toggle
    // ----------------------------------------------------------------
    document.querySelectorAll('.btn-spot-follow').forEach((btn) => {
        btn.addEventListener('click', (e) => {
            e.preventDefault();
            const hostName = btn.closest('.host-spot-card')?.querySelector('h6')?.textContent ?? 'this organizer';

            if (btn.classList.contains('following')) {
                btn.classList.remove('following');
                btn.textContent     = 'Follow';
                btn.style.background = 'transparent';
                btn.style.color     = 'var(--text-secondary)';
                showEventToast('Unfollowed', `You unfollowed ${hostName}.`, 'info');
            } else {
                btn.classList.add('following');
                btn.textContent     = 'Following';
                btn.style.background = 'var(--gradient-primary)';
                btn.style.color     = 'white';
                showEventToast('Following', `You are now following ${hostName} for updates!`, 'success');
            }
        });
    });

});

// ----------------------------------------------------------------
// 6. Pagination helper (called from Thymeleaf th:onclick)
// ----------------------------------------------------------------
function changePage(pageNumber) {
    const pageInput  = document.getElementById('pageInput');
    const searchForm = document.getElementById('searchForm');
    if (pageInput && searchForm) {
        pageInput.value = pageNumber;
        searchForm.submit();
    }
}