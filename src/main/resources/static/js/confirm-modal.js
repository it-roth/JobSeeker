// Custom Confirmation Modal System
class ConfirmModal {
    constructor() {
        this.overlay = null;
        this.currentResolve = null;
        this.initModal();
    }

    initModal() {
        // Create modal overlay if it doesn't exist
        if (!document.getElementById('confirm-modal-overlay')) {
            this.overlay = document.createElement('div');
            this.overlay.id = 'confirm-modal-overlay';
            this.overlay.className = 'confirm-modal-overlay';
            document.body.appendChild(this.overlay);
        } else {
            this.overlay = document.getElementById('confirm-modal-overlay');
        }
    }

    show(options = {}) {
        const {
            title = 'Are you sure?',
            message = 'This action cannot be undone.',
            icon = 'warning', // warning, danger, info, success
            confirmText = 'Yes, delete it',
            cancelText = 'Cancel',
            confirmButtonClass = '',
            iconClass = 'fa-exclamation-triangle'
        } = options;

        return new Promise((resolve) => {
            this.currentResolve = resolve;

            // Build modal HTML
            this.overlay.innerHTML = `
                <div class="confirm-modal">
                    <div class="confirm-modal-header">
                        <div class="confirm-modal-icon ${icon}">
                            <i class="fas ${iconClass}"></i>
                        </div>
                        <h2 class="confirm-modal-title">${title}</h2>
                    </div>
                    <div class="confirm-modal-body">
                        <p class="confirm-modal-message">${message}</p>
                    </div>
                    <div class="confirm-modal-footer">
                        <button class="confirm-modal-btn confirm-modal-btn-cancel" id="confirm-modal-cancel">
                            <i class="fas fa-times"></i>
                            <span>${cancelText}</span>
                        </button>
                        <button class="confirm-modal-btn confirm-modal-btn-confirm ${confirmButtonClass || icon}" id="confirm-modal-confirm">
                            <i class="fas fa-check"></i>
                            <span>${confirmText}</span>
                        </button>
                    </div>
                </div>
            `;

            // Show modal with animation
            setTimeout(() => this.overlay.classList.add('show'), 10);

            // Add event listeners
            document.getElementById('confirm-modal-cancel').addEventListener('click', () => this.close(false));
            document.getElementById('confirm-modal-confirm').addEventListener('click', () => this.close(true));

            // Close on overlay click
            this.overlay.addEventListener('click', (e) => {
                if (e.target === this.overlay) {
                    this.close(false);
                }
            });

            // Close on ESC key
            const escHandler = (e) => {
                if (e.key === 'Escape') {
                    this.close(false);
                    document.removeEventListener('keydown', escHandler);
                }
            };
            document.addEventListener('keydown', escHandler);
        });
    }

    close(result) {
        this.overlay.classList.remove('show');
        setTimeout(() => {
            if (this.currentResolve) {
                this.currentResolve(result);
                this.currentResolve = null;
                // Show a cancel notification when the user explicitly cancels
                try {
                    if (typeof notify !== 'undefined' && result === false) {
                        notify.cancel('Action cancelled.');
                    }
                } catch (err) {
                    // ignore if notify isn't available
                }
            }
        }, 300);
    }

    // Preset confirmations
    confirmDelete(title = 'Delete this item?', message = 'This action cannot be undone.') {
        return this.show({
            title,
            message,
            icon: 'danger',
            iconClass: 'fa-trash-alt',
            confirmText: 'Yes, delete it',
            cancelText: 'Cancel'
        });
    }

    confirmRemove(title = 'Remove this item?', message = 'You can always add it back later.') {
        return this.show({
            title,
            message,
            icon: 'warning',
            iconClass: 'fa-exclamation-triangle',
            confirmText: 'Yes, remove it',
            cancelText: 'Cancel'
        });
    }

    confirmAction(title, message) {
        return this.show({
            title,
            message,
            icon: 'info',
            iconClass: 'fa-info-circle',
            confirmText: 'Confirm',
            cancelText: 'Cancel'
        });
    }
}

// Initialize global confirm modal
const confirmModal = new ConfirmModal();

// Helper function to replace default confirm
async function showConfirm(options) {
    if (typeof options === 'string') {
        // Simple message
        return await confirmModal.show({ message: options });
    }
    return await confirmModal.show(options);
}

// Attach to forms with data-confirm attribute
document.addEventListener('DOMContentLoaded', function() {
    // Handle forms with confirmation
    document.addEventListener('submit', async function(e) {
        const form = e.target;
        const confirmMessage = form.getAttribute('data-confirm');
        const confirmTitle = form.getAttribute('data-confirm-title');
        const confirmType = form.getAttribute('data-confirm-type') || 'warning';
        
        if (confirmMessage) {
            e.preventDefault();
            
            let options = {
                message: confirmMessage,
                icon: confirmType
            };
            
            if (confirmTitle) {
                options.title = confirmTitle;
            }
            
            // Set appropriate icon based on type
            if (confirmType === 'delete' || confirmType === 'danger') {
                options.iconClass = 'fa-trash-alt';
                options.confirmText = 'Yes, delete it';
            } else if (confirmType === 'remove' || confirmType === 'warning') {
                options.iconClass = 'fa-exclamation-triangle';
                options.confirmText = 'Yes, remove it';
            }
            
            const result = await confirmModal.show(options);
            
            if (result) {
                // Remove the data-confirm attribute to prevent loop
                form.removeAttribute('data-confirm');
                form.submit();
            }
        }
    });

    // Handle links with confirmation
    document.addEventListener('click', async function(e) {
        const link = e.target.closest('[data-confirm]');
        
        if (link && link.tagName === 'A') {
            e.preventDefault();
            
            const confirmMessage = link.getAttribute('data-confirm');
            const confirmTitle = link.getAttribute('data-confirm-title');
            const confirmType = link.getAttribute('data-confirm-type') || 'warning';
            
            let options = {
                message: confirmMessage,
                icon: confirmType
            };
            
            if (confirmTitle) {
                options.title = confirmTitle;
            }
            
            const result = await confirmModal.show(options);
            
            if (result) {
                window.location.href = link.href;
            }
        }
    });
});
