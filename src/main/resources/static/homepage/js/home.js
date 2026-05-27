/**
 * EventHub Interactive Mechanics Script
 * Advanced animations, active item tracking, active status counters & local filtering simulations.
 */

document.addEventListener('DOMContentLoaded', () => {
    // 1. Dynamic Navbar scroll state detection
    const navbar = document.querySelector('.navbar-custom');
    if (navbar) {
        window.addEventListener('scroll', () => {
            if (window.scrollY > 50) {
                navbar.classList.add('scrolled');
            } else {
                navbar.classList.remove('scrolled');
            }
        });
    }

    // 2. Automated Statistical Counters animation
    const statsSection = document.getElementById('statisticsSection');
    const counters = document.querySelectorAll('.stat-number');
    
    // Observer for lazy triggering
    if (statsSection && counters.length > 0) {
        const observerOptions = {
            root: null,
            threshold: 0.2, // trigger when 20% visible
        };

        let animated = false;

        const countUp = (counter) => {
            const target = parseInt(counter.getAttribute('data-target'), 10) || 0;
            const prefix = counter.getAttribute('data-prefix') || '';
            const suffix = counter.getAttribute('data-suffix') || '';
            const duration = 2000; // 2 seconds
            const startTime = performance.now();

            const updateCount = (currentTime) => {
                const elapsedTime = currentTime - startTime;
                const progress = Math.min(elapsedTime / duration, 1);
                
                // Ease out quad formula
                const easeProgress = progress * (2 - progress);
                const currentValue = Math.floor(easeProgress * target);

                counter.textContent = `${prefix}${currentValue.toLocaleString()}${suffix}`;

                if (progress < 1) {
                    requestAnimationFrame(updateCount);
                } else {
                    counter.textContent = `${prefix}${target.toLocaleString()}${suffix}`;
                }
            };

            requestAnimationFrame(updateCount);
        };

        const statsObserver = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting && !animated) {
                    counters.forEach(counter => countUp(counter));
                    animated = true; // only animate once
                    statsObserver.unobserve(statsSection);
                }
            });
        }, observerOptions);

        statsObserver.observe(statsSection);
    }

    // 3. Client-Side Interactive Event Feature Filtering (Tab Filter simulation)
    const filterButtons = document.querySelectorAll('.btn-cat-filter');
    const eventCards = document.querySelectorAll('.event-list-item');

    if (filterButtons.length > 0 && eventCards.length > 0) {
        filterButtons.forEach(button => {
            button.addEventListener('click', (e) => {
                e.preventDefault();
                
                // Toggle Active Tab Category
                filterButtons.forEach(btn => btn.classList.remove('active'));
                button.classList.add('active');

                const targetCategory = button.getAttribute('data-category');

                // Toggle visibility with smooth entry transition
                eventCards.forEach(card => {
                    const cardCategory = card.getAttribute('data-category');
                    
                    if (targetCategory === 'all' || cardCategory === targetCategory) {
                        card.style.display = 'block';
                        // Trigger CSS entrance
                        setTimeout(() => {
                            card.style.opacity = '1';
                            card.style.transform = 'scale(1)';
                        }, 50);
                    } else {
                        card.style.opacity = '0';
                        card.style.transform = 'scale(0.95)';
                        // Wait for shrink transition before hiding completely
                        setTimeout(() => {
                            card.style.display = 'none';
                        }, 300);
                    }
                });
            });
        });
    }

    // 4. Smooth Anchor scrolling for page markers
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            const dest = this.getAttribute('href');
            if (dest !== "#" && dest.startsWith('#')) {
                e.preventDefault();
                const targetElement = document.querySelector(dest);
                if (targetElement) {
                    targetElement.scrollIntoView({
                        behavior: 'smooth',
                        block: 'start'
                    });
                }
            }
        });
    });

    // 5. Simulated Newsletter Subscription form interaction
    const newsletterForm = document.querySelector('.newsletter-form');
    if (newsletterForm) {
        newsletterForm.addEventListener('submit', (e) => {
            e.preventDefault();
            const emailInput = newsletterForm.querySelector('.newsletter-input');
            const submitBtn = newsletterForm.querySelector('.btn-newsletter-submit');
            
            if (emailInput && emailInput.value.trim() !== "") {
                const subbedEmail = emailInput.value;
                
                // Visual feedback transition
                const originalContent = submitBtn.innerHTML;
                submitBtn.innerHTML = '<i class="fa-solid fa-circle-check text-white"></i>';
                emailInput.value = '';
                emailInput.placeholder = 'Subscribed successfully!';
                emailInput.disabled = true;
                
                // Trigger Toast banner
                showEventToast(`Subscribed!`, `Awesome! Event updates will now be sent to <strong>${subbedEmail}</strong>.`, 'success');

                setTimeout(() => {
                    submitBtn.innerHTML = originalContent;
                    emailInput.disabled = false;
                    emailInput.placeholder = 'Your email address';
                }, 4000);
            }
        });
    }

    // 6. Toast Notification Helper
    window.showEventToast = function(title, message, type = 'info') {
        let toastContainer = document.querySelector('.eventhub-toast-container');
        if (!toastContainer) {
            toastContainer = document.createElement('div');
            toastContainer.className = 'eventhub-toast-container';
            toastContainer.style.position = 'fixed';
            toastContainer.style.bottom = '2rem';
            toastContainer.style.right = '2rem';
            toastContainer.style.zIndex = '9999';
            toastContainer.style.display = 'flex';
            toastContainer.style.flexDirection = 'column';
            toastContainer.style.gap = '0.5rem';
            document.body.appendChild(toastContainer);
        }

        const toast = document.createElement('div');
        toast.className = `eventhub-toast glass-morphic border-gradient p-3 slide-in`;
        toast.style.background = 'rgba(15, 2, 36, 0.95)';
        toast.style.border = '1px solid rgba(255, 42, 122, 0.2)';
        toast.style.borderRadius = '12px';
        toast.style.minWidth = '300px';
        toast.style.boxShadow = '0 10px 30px rgba(0,0,0,0.5)';
        toast.style.transition = 'all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.25)';
        toast.style.transform = 'translateY(50px)';
        toast.style.opacity = '0';

        const borderAccentColors = {
            success: 'rgba(0, 240, 255, 0.6)',
            danger: 'rgba(255, 42, 122, 0.6)',
            info: 'rgba(140, 43, 255, 0.6)'
        };

        toast.style.borderLeft = `4px solid ${borderAccentColors[type] || borderAccentColors.info}`;

        toast.innerHTML = `
            <div class="d-flex justify-content-between align-items-center mb-1">
                <strong class="text-white" style="font-family: 'Space Grotesk', sans-serif;"><i class="fa-solid fa-bell text-pink-glow me-2"></i>${title}</strong>
                <button type="button" class="btn-close btn-close-white" style="font-size: 0.75rem;" onclick="this.parentElement.parentElement.remove()"></button>
            </div>
            <div class="text-secondary-light" style="font-size: 0.85rem; font-family: 'Outfit', sans-serif;">${message}</div>
        `;

        toastContainer.appendChild(toast);

        // trigger animation
        setTimeout(() => {
            toast.style.transform = 'translateY(0)';
            toast.style.opacity = '1';
        }, 50);

        // Auto remove after 5 seconds
        setTimeout(() => {
            toast.style.transform = 'translateY(-20px)';
            toast.style.opacity = '0';
            setTimeout(() => {
                toast.remove();
            }, 300);
        }, 5000);
    };

    // 7. Interactive Book Ticketing simulations
    const bookingButtons = document.querySelectorAll('.btn-book-now');
    bookingButtons.forEach(button => {
        button.addEventListener('click', (e) => {
            e.preventDefault();
            const card = button.closest('.card-event');
            if (card) {
                const eventName = card.querySelector('.event-card-title a').textContent;
                showEventToast('Ticket Request Recieved', `We are preparing your passes for <strong>${eventName}</strong>. Check your inbox!`, 'info');
            }
        });
    });

    // 8. Follow organizer interactions
    const followButtons = document.querySelectorAll('.btn-spot-follow');
    followButtons.forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.preventDefault();
            const hostName = btn.closest('.host-spot-card').querySelector('h6').textContent;
            
            if (btn.classList.contains('following')) {
                btn.classList.remove('following');
                btn.textContent = 'Follow';
                btn.style.background = 'transparent';
                btn.style.color = 'var(--text-secondary)';
                showEventToast('Unfollowed', `You unfollowed ${hostName}.`, 'info');
            } else {
                btn.classList.add('following');
                btn.textContent = 'Following';
                btn.style.background = 'var(--gradient-primary)';
                btn.style.color = 'white';
                showEventToast('Following', `You are now following ${hostName} for dynamic notifications!`, 'success');
            }
        });
    });

    // 9. Current year setter
    const currentYearEl = document.getElementById('currentYear');
    if (currentYearEl) {
        currentYearEl.textContent = new Date().getFullYear();
    }
});
