document.addEventListener('DOMContentLoaded', function () {
    const statItems = document.querySelectorAll('.stat-item');
    const counters = document.querySelectorAll('.stat-number');

    function animateCount(el, target, duration = 1500) {
        let start = 0;
        const startTime = performance.now();

        function step(now) {
            const elapsed = now - startTime;
            const progress = Math.min(elapsed / duration, 1);
            // easeOutCubic
            const eased = 1 - Math.pow(1 - progress, 3);
            const value = Math.floor(eased * (target - start) + start);
            el.textContent = value;
            if (progress < 1) {
                requestAnimationFrame(step);
            } else {
                el.textContent = target;
            }
        }

        requestAnimationFrame(step);
    }

    const observerOptions = {
        root: null,
        rootMargin: '0px',
        threshold: 0.2
    };

    const observer = new IntersectionObserver((entries, obs) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                // reveal numbers only (avoid moving labels/images)
                counters.forEach(num => num.classList.add('visible'));
                // start counters
                counters.forEach(c => {
                    const target = parseInt(c.getAttribute('data-target')) || 0;
                    // avoid re-running if already started
                    if (!c.dataset.started) {
                        c.dataset.started = 'true';
                        animateCount(c, target, 1600);
                    }
                });
                obs.disconnect();
            }
        });
    }, observerOptions);

    const statsSection = document.querySelector('.stats-section');
    if (statsSection) observer.observe(statsSection);
});
