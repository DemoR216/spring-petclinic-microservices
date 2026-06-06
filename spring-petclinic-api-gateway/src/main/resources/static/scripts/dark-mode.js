'use strict';

(function () {
    var STORAGE_KEY = 'petclinic-dark-mode';

    function applyTheme(dark) {
        document.body.setAttribute('data-theme', dark ? 'dark' : 'light');
        var icon = document.getElementById('dark-mode-icon');
        if (icon) {
            icon.className = dark ? 'fa fa-sun-o' : 'fa fa-moon-o';
        }
    }

    function isDarkMode() {
        var stored = localStorage.getItem(STORAGE_KEY);
        if (stored !== null) {
            return stored === 'true';
        }
        return window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches;
    }

    window.toggleDarkMode = function () {
        var current = document.body.getAttribute('data-theme') === 'dark';
        var next = !current;
        localStorage.setItem(STORAGE_KEY, String(next));
        applyTheme(next);
    };

    applyTheme(isDarkMode());
})();
