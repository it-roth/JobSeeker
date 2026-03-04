// Toast Notification System
class NotificationManager {
    constructor() {
        this.container = null;
        this.initContainer();
    }

    initContainer() {
        // Create container if it doesn't exist
        if (!document.getElementById('notification-container')) {
            this.container = document.createElement('div');
            this.container.id = 'notification-container';
            this.container.className = 'notification-container';
            document.body.appendChild(this.container);
        } else {
            this.container = document.getElementById('notification-container');
        }
    }

    show(message, type = 'success', duration = 4000) {
        const notification = document.createElement('div');
        notification.className = `notification notification-${type}`;
        
        const iconMap = {
            success: '<i class="fas fa-check-circle"></i>',
            error: '<i class="fas fa-exclamation-circle"></i>',
            warning: '<i class="fas fa-exclamation-triangle"></i>',
            info: '<i class="fas fa-info-circle"></i>',
            delete: '<i class="fas fa-trash-alt"></i>',
            cancel: '<i class="fas fa-times-circle"></i>'
        };

        notification.innerHTML = `
            <div class="notification-icon">${iconMap[type] || iconMap.info}</div>
            <div class="notification-content">
                <div class="notification-message">${message}</div>
            </div>
            <button class="notification-close" onclick="this.parentElement.remove()">
                <i class="fas fa-times"></i>
            </button>
        `;

        this.container.appendChild(notification);

        // Trigger animation
        setTimeout(() => notification.classList.add('notification-show'), 10);

        // Auto remove after duration
        if (duration > 0) {
            setTimeout(() => {
                notification.classList.remove('notification-show');
                notification.classList.add('notification-hide');
                setTimeout(() => notification.remove(), 400);
            }, duration);
        }
    }

    success(message, duration = 4000) {
        this.show(message, 'success', duration);
    }

    error(message, duration = 4000) {
        this.show(message, 'error', duration);
    }

    warning(message, duration = 4000) {
        this.show(message, 'warning', duration);
    }

    info(message, duration = 4000) {
        this.show(message, 'info', duration);
    }

    deleted(message, duration = 4000) {
        this.show(message, 'delete', duration);
    }

    cancel(message, duration = 4000) {
        this.show(message, 'cancel', duration);
    }
}

// Initialize global notification manager
const notify = new NotificationManager();

// Check URL parameters for notifications on page load
document.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);
    
    // Job actions
    if (urlParams.get('deleted') === 'true') {
        notify.deleted('Job posting has been successfully deleted!');
        // Clean URL
        cleanUrl();
    }
    
    if (urlParams.get('posted') === 'true') {
        notify.success('Job posting has been successfully created!');
        cleanUrl();
    }
    
    if (urlParams.get('updated') === 'true') {
        notify.success('Job posting has been successfully updated!');
        cleanUrl();
    }
    
    // Job seeker actions
    if (urlParams.get('applied') === 'true') {
        notify.success('Your application has been submitted successfully!');
        cleanUrl();
    }
    
    if (urlParams.get('saved') === 'true') {
        notify.success('Job has been saved to your favorites!');
        cleanUrl();
    }
    
    if (urlParams.get('unsaved') === 'true') {
        notify.info('Job has been removed from your favorites.');
        cleanUrl();
    }
    
    // Profile actions
    if (urlParams.get('profile-updated') === 'true') {
        notify.success('Your profile has been updated successfully!');
        cleanUrl();
    }
    
    // Error states
    if (urlParams.get('error') === 'true') {
        notify.error('An error occurred. Please try again.');
        cleanUrl();
    }
});

// Clean URL without reloading page
function cleanUrl() {
    const url = new URL(window.location);
    url.search = '';
    window.history.replaceState({}, document.title, url.toString());
}
