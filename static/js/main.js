/**
 * Python Learning Platform - Main JavaScript
 * Handles theme toggling, navigation, and global interactions
 */

// ============================================================================
// Theme Management
// ============================================================================
class ThemeManager {
    constructor() {
        this.theme = localStorage.getItem('theme') || 'light';
        this.init();
    }

    init() {
        this.applyTheme();
        this.setupToggle();
    }

    applyTheme() {
        document.documentElement.setAttribute('data-theme', this.theme);
        this.updateThemeIcon();
    }

    toggleTheme() {
        this.theme = this.theme === 'light' ? 'dark' : 'light';
        localStorage.setItem('theme', this.theme);
        this.applyTheme();
    }

    updateThemeIcon() {
        const themeIcon = document.querySelector('.theme-icon');
        if (themeIcon) {
            themeIcon.textContent = this.theme === 'light' ? '🌙' : '☀️';
        }
    }

    setupToggle() {
        const toggleBtn = document.getElementById('theme-toggle');
        if (toggleBtn) {
            toggleBtn.addEventListener('click', () => this.toggleTheme());
        }
    }
}

// ============================================================================
// Progress Modal
// ============================================================================
class ProgressModal {
    constructor() {
        this.modal = document.getElementById('progress-modal');
        this.openBtn = document.getElementById('progress-link');
        this.closeBtn = document.getElementById('close-progress-modal');
        this.init();
    }

    init() {
        if (!this.modal) return;

        this.openBtn?.addEventListener('click', (e) => {
            e.preventDefault();
            this.open();
        });

        this.closeBtn?.addEventListener('click', () => this.close());

        // Close on outside click
        this.modal.addEventListener('click', (e) => {
            if (e.target === this.modal) {
                this.close();
            }
        });

        // Close on Escape key
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && this.modal.classList.contains('active')) {
                this.close();
            }
        });
    }

    open() {
        this.modal.classList.add('active');
        this.modal.style.display = 'flex';
        this.renderProgress();
    }

    close() {
        this.modal.classList.remove('active');
        this.modal.style.display = 'none';
    }

    renderProgress() {
        const progressTracker = new ProgressTracker();
        const progress = progressTracker.getProgress();
        const modalBody = document.getElementById('progress-modal-body');

        if (!modalBody) return;

        let html = '<div class="progress-summary">';

        // Overall progress
        const totalLessons = 73; // Total lessons in the course
        const completedLessons = Object.values(progress.modules).reduce(
            (sum, module) => sum + module.completed, 0
        );
        const overallPercentage = Math.round((completedLessons / totalLessons) * 100);

        html += `
            <div class="overall-progress">
                <h3>Overall Progress</h3>
                <div class="progress-bar">
                    <div class="progress-fill" style="width: ${overallPercentage}%"></div>
                </div>
                <p>${completedLessons} of ${totalLessons} lessons completed (${overallPercentage}%)</p>
            </div>
        `;

        // Module-by-module progress
        html += '<div class="modules-progress">';
        html += '<h3>Module Progress</h3>';

        for (let i = 1; i <= 14; i++) {
            const moduleProgress = progress.modules[i] || { completed: 0, total: 0 };
            const percentage = moduleProgress.total > 0
                ? Math.round((moduleProgress.completed / moduleProgress.total) * 100)
                : 0;

            html += `
                <div class="module-progress-item">
                    <div class="module-progress-header">
                        <span>Module ${i}</span>
                        <span>${percentage}%</span>
                    </div>
                    <div class="progress-bar">
                        <div class="progress-fill" style="width: ${percentage}%"></div>
                    </div>
                    <p class="module-progress-detail">${moduleProgress.completed} of ${moduleProgress.total} lessons</p>
                </div>
            `;
        }

        html += '</div></div>';
        modalBody.innerHTML = html;
    }
}

// ============================================================================
// Global Progress Bar
// ============================================================================
class GlobalProgressBar {
    constructor() {
        this.progressFill = document.getElementById('global-progress-fill');
        this.progressText = document.getElementById('global-progress-text');
        this.init();
    }

    init() {
        this.update();
    }

    update() {
        const progressTracker = new ProgressTracker();
        const progress = progressTracker.getProgress();

        const totalLessons = 73;
        const completedLessons = Object.values(progress.modules).reduce(
            (sum, module) => sum + module.completed, 0
        );
        const percentage = Math.round((completedLessons / totalLessons) * 100);

        if (this.progressFill) {
            this.progressFill.style.width = `${percentage}%`;
        }

        if (this.progressText) {
            this.progressText.textContent = `${percentage}% Complete`;
        }
    }
}

// ============================================================================
// Smooth Scrolling
// ============================================================================
function setupSmoothScroll() {
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            const href = this.getAttribute('href');
            if (href === '#') return;

            e.preventDefault();
            const target = document.querySelector(href);

            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });
}

// ============================================================================
// Copy to Clipboard
// ============================================================================
function setupCopyButtons() {
    document.querySelectorAll('.copy-btn').forEach(button => {
        button.addEventListener('click', function() {
            const targetId = this.getAttribute('data-copy-target');
            const codeElement = document.getElementById(targetId);

            if (codeElement) {
                const code = codeElement.textContent;
                navigator.clipboard.writeText(code).then(() => {
                    const originalText = this.textContent;
                    this.textContent = 'Copied!';
                    this.classList.add('copied');

                    setTimeout(() => {
                        this.textContent = originalText;
                        this.classList.remove('copied');
                    }, 2000);
                }).catch(err => {
                    console.error('Failed to copy:', err);
                });
            }
        });
    });
}

// ============================================================================
// Keyboard Shortcuts
// ============================================================================
function setupKeyboardShortcuts() {
    document.addEventListener('keydown', (e) => {
        // Ctrl/Cmd + K: Toggle theme
        if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
            e.preventDefault();
            const themeToggle = document.getElementById('theme-toggle');
            if (themeToggle) themeToggle.click();
        }

        // Ctrl/Cmd + P: Open progress modal
        if ((e.ctrlKey || e.metaKey) && e.key === 'p') {
            e.preventDefault();
            const progressLink = document.getElementById('progress-link');
            if (progressLink) progressLink.click();
        }
    });
}

// ============================================================================
// Analytics & Tracking (placeholder for future implementation)
// ============================================================================
function trackEvent(category, action, label) {
    // In production, this would send data to analytics service
    console.log('Event tracked:', { category, action, label });
}

// ============================================================================
// Initialization
// ============================================================================
document.addEventListener('DOMContentLoaded', () => {
    // Initialize components
    new ThemeManager();
    new ProgressModal();
    new GlobalProgressBar();

    // Setup features
    setupSmoothScroll();
    setupCopyButtons();
    setupKeyboardShortcuts();

    // Track page view
    trackEvent('page', 'view', window.location.pathname);

    console.log('🚀 Python Learning Platform initialized');
});

// ============================================================================
// Global Utilities
// ============================================================================
window.utils = {
    trackEvent,
    formatDate: (date) => {
        return new Date(date).toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
    },
    debounce: (func, wait) => {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }
};
