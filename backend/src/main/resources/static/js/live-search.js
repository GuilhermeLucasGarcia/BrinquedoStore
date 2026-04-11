(function () {
    const DEBOUNCE_MS = 300;

    function debounce(fn, delay) {
        let timer;
        return function (...args) {
            window.clearTimeout(timer);
            timer = window.setTimeout(() => fn.apply(this, args), delay);
        };
    }

    function escapeHtml(value) {
        return value
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#39;");
    }

    function createResultsContainer(input) {
        const wrapper = input.closest(".search-bar-container");
        if (!wrapper) {
            return null;
        }

        wrapper.classList.add("live-search-wrapper");

        const results = document.createElement("div");
        results.className = "live-search-results d-none";
        results.setAttribute("role", "listbox");
        wrapper.appendChild(results);
        return results;
    }

    function renderState(resultsContainer, type, message) {
        resultsContainer.classList.remove("d-none");
        resultsContainer.innerHTML = `<div class="live-search-state live-search-state-${type}">${message}</div>`;
    }

    function renderResults(resultsContainer, items) {
        if (!items.length) {
            renderState(resultsContainer, "empty", "Nenhum resultado encontrado.");
            return;
        }

        const html = items.map((item) => {
            const type = escapeHtml(item.type || "resultado");
            const label = escapeHtml(item.label || "");
            const subtitle = escapeHtml(item.subtitle || "");
            const url = escapeHtml(item.url || "#");

            return `
                <a class="live-search-item" href="${url}" role="option">
                    <span class="live-search-item-type">${type}</span>
                    <span class="live-search-item-body">
                        <strong>${label}</strong>
                        <small>${subtitle}</small>
                    </span>
                </a>
            `;
        }).join("");

        resultsContainer.classList.remove("d-none");
        resultsContainer.innerHTML = html;
    }

    function hideResults(resultsContainer) {
        resultsContainer.classList.add("d-none");
        resultsContainer.innerHTML = "";
    }

    function attachLiveSearch(input) {
        const endpoint = input.dataset.liveSearchUrl;
        if (!endpoint) {
            return;
        }

        const resultsContainer = createResultsContainer(input);
        if (!resultsContainer) {
            return;
        }

        let abortController = null;

        const executeSearch = debounce(async () => {
            const term = input.value.trim();
            if (term.length < 1) {
                hideResults(resultsContainer);
                input.classList.remove("is-searching");
                return;
            }

            if (abortController) {
                abortController.abort();
            }

            abortController = new AbortController();
            input.classList.add("is-searching");
            renderState(resultsContainer, "loading", "Buscando...");

            try {
                const response = await fetch(`${endpoint}?q=${encodeURIComponent(term)}`, {
                    headers: { "Accept": "application/json" },
                    signal: abortController.signal
                });

                if (!response.ok) {
                    throw new Error(`HTTP ${response.status}`);
                }

                const items = await response.json();
                renderResults(resultsContainer, Array.isArray(items) ? items : []);
            } catch (error) {
                if (error.name === "AbortError") {
                    return;
                }
                renderState(resultsContainer, "error", "Nao foi possivel concluir a busca. Tente novamente.");
            } finally {
                input.classList.remove("is-searching");
            }
        }, DEBOUNCE_MS);

        input.addEventListener("input", executeSearch);
        input.addEventListener("focus", () => {
            if (input.value.trim().length > 0 && resultsContainer.innerHTML.trim()) {
                resultsContainer.classList.remove("d-none");
            }
        });
        input.addEventListener("keydown", (event) => {
            if (event.key === "Escape") {
                hideResults(resultsContainer);
            }
        });

        document.addEventListener("click", (event) => {
            const wrapper = input.closest(".live-search-wrapper");
            if (wrapper && !wrapper.contains(event.target)) {
                hideResults(resultsContainer);
            }
        });
    }

    document.addEventListener("DOMContentLoaded", () => {
        document.querySelectorAll(".search-input[data-live-search-url]").forEach(attachLiveSearch);
    });
})();
