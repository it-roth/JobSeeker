/**
 * Main JavaScript for Job Portal
 * Handles general functionality for dashboard and job listings
 */

document.addEventListener('DOMContentLoaded', function() {
    initNavigation();
    initModals();
    initCandidateDashboardFilters();
    initMobileMenu();
});

// Mobile Menu Toggle
function initMobileMenu() {
    const mobileMenuToggle = document.getElementById('mobileMenuToggle');
    const navLinks = document.getElementById('navLinks');
    const navButtons = document.getElementById('navButtons');
    
    if (mobileMenuToggle) {
        mobileMenuToggle.addEventListener('click', function() {
            // Toggle menu button animation
            this.classList.toggle('active');
            
            // Toggle menu visibility
            if (navLinks) navLinks.classList.toggle('active');
            if (navButtons) navButtons.classList.toggle('active');
            
            // Toggle body class for overlay
            document.body.classList.toggle('menu-open');
        });

        // Close menu when clicking on a link
        if (navLinks) {
            const links = navLinks.querySelectorAll('a');
            links.forEach(link => {
                link.addEventListener('click', function() {
                    mobileMenuToggle.classList.remove('active');
                    navLinks.classList.remove('active');
                    if (navButtons) navButtons.classList.remove('active');
                    document.body.classList.remove('menu-open');
                });
            });
        }

        // Close menu when clicking the overlay
        document.addEventListener('click', function(e) {
            if (document.body.classList.contains('menu-open') && 
                !e.target.closest('.navbar-content') &&
                !e.target.closest('#mobileMenuToggle')) {
                mobileMenuToggle.classList.remove('active');
                if (navLinks) navLinks.classList.remove('active');
                if (navButtons) navButtons.classList.remove('active');
                document.body.classList.remove('menu-open');
            }
        });
    }
}

// Navigation helpers
function initNavigation() {
    // Add active class to current nav item
    const currentPath = window.location.pathname;
    const navLinks = document.querySelectorAll('.navbar-nav a, .navbar-nav-candidate a');
    
    navLinks.forEach(link => {
        if (link.getAttribute('href') === currentPath) {
            link.classList.add('active');
        }
    });
}

// Modal helpers
function initModals() {
    // Close modals on backdrop click
    document.addEventListener('click', function(e) {
        if (e.target.classList.contains('modal-backdrop')) {
            closeAllModals();
        }
    });
}

function closeAllModals() {
    const modals = document.querySelectorAll('.modal');
    modals.forEach(modal => modal.classList.remove('show'));
}

// Candidate Dashboard Filters
function initCandidateDashboardFilters() {
    const filterCheckboxes = document.querySelectorAll('.filter-section-candidate input[type="checkbox"]');
    const searchBtn = document.querySelector('.search-btn-hero-candidate');
    const jobSearchInput = document.getElementById('jobSearch');
    const locationSearchInput = document.getElementById('locationSearch');
    const clearFiltersBtn = document.getElementById('clearFiltersBtn');
    
    // Only proceed if we're on the candidate dashboard page
    if (!searchBtn && filterCheckboxes.length === 0) return;
    
    // Attach filter checkbox listeners
    filterCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', applyCandidateFilters);
    });
    
    // Add search button functionality
    if (searchBtn) {
        searchBtn.addEventListener('click', function(e) {
            e.preventDefault();
            console.log('Search button clicked!'); // Debug log
            applyCandidateFilters();
        });
    }
    
    // Add real-time search on input
    if (jobSearchInput) {
        jobSearchInput.addEventListener('input', applyCandidateFilters);
        // Also trigger on Enter key
        jobSearchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                applyCandidateFilters();
            }
        });
    }
    if (locationSearchInput) {
        locationSearchInput.addEventListener('input', applyCandidateFilters);
        // Also trigger on Enter key
        locationSearchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                applyCandidateFilters();
            }
        });
    }
    
    // Clear filters button
    if (clearFiltersBtn) {
        clearFiltersBtn.addEventListener('click', function() {
            // Clear all checkboxes
            filterCheckboxes.forEach(checkbox => checkbox.checked = false);
            // Clear search inputs
            if (jobSearchInput) jobSearchInput.value = '';
            if (locationSearchInput) locationSearchInput.value = '';
            // Reapply filters (which will show all jobs)
            applyCandidateFilters();
        });
    }
}

function applyCandidateFilters() {
    const jobCards = document.querySelectorAll('.job-card-candidate');
    
    // Get selected filters
    const employmentFilters = Array.from(document.querySelectorAll('input[name="employment"]:checked'))
        .map(cb => cb.value);
    const remoteFilters = Array.from(document.querySelectorAll('input[name="remote"]:checked'))
        .map(cb => cb.value);
    const dateFilters = Array.from(document.querySelectorAll('input[name="datePosted"]:checked'))
        .map(cb => cb.value);
    
    // Get search terms
    const searchInput = document.getElementById('jobSearch');
    const locationInput = document.getElementById('locationSearch');
    const searchTerm = searchInput ? searchInput.value.toLowerCase() : '';
    const locationTerm = locationInput ? locationInput.value.toLowerCase() : '';
    
    // Check if any filters are active
    const hasActiveFilters = employmentFilters.length > 0 || 
                            remoteFilters.length > 0 || 
                            dateFilters.length > 0 || 
                            searchTerm || 
                            locationTerm;
    
    let visibleCount = 0;
    
    jobCards.forEach(card => {
        let show = true;
        
        // Search filter
        if (searchTerm) {
            const title = card.querySelector('.job-card-title-candidate');
            const company = card.querySelector('.job-company-candidate');
            const titleText = title ? title.textContent.toLowerCase() : '';
            const companyText = company ? company.textContent.toLowerCase() : '';
            
            show = show && (titleText.includes(searchTerm) || companyText.includes(searchTerm));
        }
        
        // Location filter
        if (locationTerm) {
            const location = card.querySelector('.job-location-candidate');
            const locationText = location ? location.textContent.toLowerCase() : '';
            show = show && locationText.includes(locationTerm);
        }
        
        // Employment Type filter
        if (employmentFilters.length > 0) {
            const jobType = card.getAttribute('data-job-type') || '';
            const matchesEmployment = employmentFilters.some(filter => {
                if (filter === 'parttime') return jobType.includes('part') || jobType.includes('part-time');
                if (filter === 'fulltime') return jobType.includes('full') || jobType.includes('full-time');
                if (filter === 'freelance') return jobType.includes('freelance') || jobType.includes('contract');
                return false;
            });
            show = show && matchesEmployment;
        }
        
        // Remote filter
        if (remoteFilters.length > 0) {
            const remoteType = card.getAttribute('data-remote') || '';
            const matchesRemote = remoteFilters.some(filter => {
                if (filter === 'remote-only') return remoteType.includes('remote') && !remoteType.includes('hybrid') && !remoteType.includes('partial');
                if (filter === 'office-only') return remoteType.includes('office') || remoteType.includes('onsite') || remoteType.includes('on-site');
                if (filter === 'partial-remote') return remoteType.includes('hybrid') || remoteType.includes('partial');
                return false;
            });
            show = show && matchesRemote;
        }
        
        // Date Posted filter
        if (dateFilters.length > 0) {
            const postedDateStr = card.getAttribute('data-posted-date');
            if (postedDateStr) {
                const postedDate = new Date(postedDateStr);
                const today = new Date();
                const diffTime = Math.abs(today - postedDate);
                const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
                
                const matchesDate = dateFilters.some(filter => {
                    if (filter === 'today') return diffDays <= 1;
                    if (filter === 'week') return diffDays <= 7;
                    if (filter === 'month') return diffDays <= 30;
                    return false;
                });
                show = show && matchesDate;
            } else {
                // If no date, don't show when date filter is active
                show = false;
            }
        }
        
        // Apply visibility
        if (show) {
            card.style.display = 'grid';
            visibleCount++;
        } else {
            card.style.display = 'none';
        }
    });
    
    // Update results count message
    updateResultsCount(visibleCount, jobCards.length, hasActiveFilters);
}

function updateResultsCount(visible, total, hasActiveFilters) {
    const resultsHeader = document.querySelector('.results-header-candidate');
    if (resultsHeader) {
        if (hasActiveFilters) {
            resultsHeader.textContent = `Search Results (${visible} ${visible === 1 ? 'job' : 'jobs'} found)`;
        } else {
            resultsHeader.textContent = 'Search Results';
        }
    }
}

// File upload preview
function previewImage(input) {
    if (input.files && input.files[0]) {
        const reader = new FileReader();
        reader.onload = function(e) {
            const preview = document.getElementById('imagePreview');
            if (preview) {
                preview.src = e.target.result;
                preview.style.display = 'block';
            }
        };
        reader.readAsDataURL(input.files[0]);
    }
}

// Confirm delete actions
function confirmDelete(message) {
    return confirm(message || 'Are you sure you want to delete this item?');
}

// Search functionality for regular dashboard
function filterJobs() {
    const searchInput = document.getElementById('jobSearch');
    const locationInput = document.getElementById('locationSearch');
    
    if (!searchInput) return;
    
    const searchTerm = searchInput.value.toLowerCase();
    const locationTerm = locationInput ? locationInput.value.toLowerCase() : '';
    
    const jobCards = document.querySelectorAll('.job-card');
    
    jobCards.forEach(card => {
        const title = card.querySelector('.job-card-title');
        const company = card.querySelector('.job-card-company');
        
        if (!title || !company) return;
        
        const titleText = title.textContent.toLowerCase();
        const companyText = company.textContent.toLowerCase();
        const location = card.dataset.location ? card.dataset.location.toLowerCase() : '';
        
        const matchesSearch = titleText.includes(searchTerm) || companyText.includes(searchTerm);
        const matchesLocation = !locationTerm || location.includes(locationTerm);
        
        card.style.display = (matchesSearch && matchesLocation) ? 'flex' : 'none';
    });
}

// Make job cards clickable
document.addEventListener('click', function(e) {
    const jobCard = e.target.closest('.job-card-candidate');
    if (jobCard && !e.target.closest('button, a')) {
        const jobId = jobCard.getAttribute('data-job-id');
        if (jobId) {
            window.location.href = '/jobseeker/jobs/' + jobId;
        }
    }
});

