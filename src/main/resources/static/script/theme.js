function setTheme(theme) {
    localStorage.setItem("theme", theme)
    updateThemeFull()
}

function updateThemeFull() {
    updateTheme() // Inlined into page
    updateSelector()
}

function updateSelector() {
    var theme = localStorage.getItem("theme")
    switch(theme) {
        case "dark":
            document.getElementById("lightswitch-auto").classList.remove("active")
            document.getElementById("lightswitch-light").classList.remove("active")
            document.getElementById("lightswitch-dark").classList.add("active")
            break
        case "light":
            document.getElementById("lightswitch-auto").classList.remove("active")
            document.getElementById("lightswitch-light").classList.add("active")
            document.getElementById("lightswitch-dark").classList.remove("active")
            break
        default:
            document.getElementById("lightswitch-auto").classList.add("active")
            document.getElementById("lightswitch-light").classList.remove("active")
            document.getElementById("lightswitch-dark").classList.remove("active")
            break
    }
}

function initTheme() {
    document.querySelector("#lightswitch-auto").addEventListener("click", () => setTheme("auto"))
    document.querySelector("#lightswitch-light").addEventListener("click", () => setTheme("light"))
    document.querySelector("#lightswitch-dark").addEventListener("click", () => setTheme("dark"))
    updateThemeFull()
}

document.addEventListener('DOMContentLoaded', initTheme);