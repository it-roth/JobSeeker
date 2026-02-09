/**
 * Hero Slider Component
 * Handles slideshow functionality with auto-play and manual navigation
 */

class HeroSlider {
    constructor() {
        this.currentSlide = 1;
        this.autoPlayInterval = null;
        this.autoPlayDelay = 3000; // 3 seconds
        this.init();
    }

    init() {
        this.showSlide(this.currentSlide);
        this.startAutoPlay();
        this.attachEventListeners();
    }

    /**
     * Navigate to next slide
     */
    nextSlide() {
        this.changeSlide(1);
    }

    /**
     * Navigate to previous slide
     */
    prevSlide() {
        this.changeSlide(-1);
    }

    /**
     * Change slide by offset
     * @param {number} offset - Number of slides to move
     */
    changeSlide(offset) {
        this.showSlide(this.currentSlide += offset);
        this.resetAutoPlay();
    }

    /**
     * Jump to specific slide
     * @param {number} slideNumber - Slide number to show
     */
    goToSlide(slideNumber) {
        this.showSlide(this.currentSlide = slideNumber);
        this.resetAutoPlay();
    }

    /**
     * Display the specified slide
     * @param {number} n - Slide number to display
     */
    showSlide(n) {
        const slides = document.getElementsByClassName("slide");
        const dots = document.getElementsByClassName("dot");

        if (!slides.length) return;

        // Loop around
        if (n > slides.length) {
            this.currentSlide = 1;
        }
        if (n < 1) {
            this.currentSlide = slides.length;
        }

        // Hide all slides
        Array.from(slides).forEach(slide => {
            slide.classList.remove("active");
        });

        // Deactivate all dots
        Array.from(dots).forEach(dot => {
            dot.classList.remove("active");
        });

        // Show current slide
        slides[this.currentSlide - 1].classList.add("active");
        
        // Activate current dot
        if (dots.length) {
            dots[this.currentSlide - 1].classList.add("active");
        }
    }

    /**
     * Start automatic slideshow
     */
    startAutoPlay() {
        this.autoPlayInterval = setInterval(() => {
            this.nextSlide();
        }, this.autoPlayDelay);
    }

    /**
     * Stop automatic slideshow
     */
    stopAutoPlay() {
        if (this.autoPlayInterval) {
            clearInterval(this.autoPlayInterval);
            this.autoPlayInterval = null;
        }
    }

    /**
     * Reset auto-play timer
     */
    resetAutoPlay() {
        this.stopAutoPlay();
        this.startAutoPlay();
    }

    /**
     * Attach event listeners to navigation elements
     */
    attachEventListeners() {
        // Pause on hover
        const sliderElement = document.querySelector('.hero-slider');
        if (sliderElement) {
            sliderElement.addEventListener('mouseenter', () => {
                this.stopAutoPlay();
            });
            sliderElement.addEventListener('mouseleave', () => {
                this.startAutoPlay();
            });
        }

        // Keyboard navigation
        document.addEventListener('keydown', (e) => {
            if (e.key === 'ArrowLeft') {
                this.prevSlide();
            } else if (e.key === 'ArrowRight') {
                this.nextSlide();
            }
        });
    }
}

// Global functions for inline onclick handlers
let sliderInstance;

function changeSlide(offset) {
    if (sliderInstance) {
        sliderInstance.changeSlide(offset);
    }
}

function currentSlide(slideNumber) {
    if (sliderInstance) {
        sliderInstance.goToSlide(slideNumber);
    }
}

// Initialize slider when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    sliderInstance = new HeroSlider();
});
