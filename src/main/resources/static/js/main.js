/**
 * Main JavaScript for Job Portal
 * Handles general functionality for dashboard and job listings
 */

document.addEventListener('DOMContentLoaded', function() {
    initNavigation();
    initModals();
    initCandidateDashboardFilters();
});

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
    
    if (filterCheckboxes.length === 0) return;
    
    filterCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', applyCandidateFilters);
    });
    
    // Add search functionality
    const searchBtn = document.querySelector('.search-btn-hero-candidate');
    const jobSearchInput = document.getElementById('jobSearch');
    const locationSearchInput = document.getElementById('locationSearch');
    
    if (searchBtn && jobSearchInput) {
        // Prevent default form submit for client-side filtering
        searchBtn.addEventListener('click', function(e) {
            if (!jobSearchInput.value && !locationSearchInput.value) {
                e.preventDefault();
                applyCandidateFilters();
            }
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
        
        // Apply visibility
        if (show) {
            card.style.display = 'grid';
            visibleCount++;
        } else {
            card.style.display = 'none';
        }
    });
    
    // Update results count message
    updateResultsCount(visibleCount, jobCards.length);
}

function updateResultsCount(visible, total) {
    const resultsHeader = document.querySelector('.results-header-candidate');
    if (resultsHeader) {
        resultsHeader.textContent = `Search Results (${visible} ${visible === 1 ? 'job' : 'jobs'} found)`;
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
        // In a real implementation, this would navigate to job details
        const jobTitle = jobCard.querySelector('.job-card-title-candidate');
        if (jobTitle) {
            console.log('Clicked job:', jobTitle.textContent);
            // window.location.href = '/jobseeker/jobs/' + jobId;
        }
    }
});

