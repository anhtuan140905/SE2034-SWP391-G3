function toggleSidebar(open) {
    var sidebar = document.getElementById("sidebar");
    var overlay = document.getElementById("sidebarOverlay");

    if (open) {
        sidebar.classList.add("open");
        overlay.classList.add("show");
        document.body.style.overflow = "hidden";
    } else {
        sidebar.classList.remove("open");
        overlay.classList.remove("show");
        document.body.style.overflow = "";
    }
}
function handleSearch(value) {
    var clearBtn = document.getElementById("searchClear");

    clearBtn.classList.toggle(
        "d-none",
        value.length === 0
    );
}
function clearSearch() {
    var input = document.getElementById("searchInput");

    input.value = "";

    document
        .getElementById("searchClear")
        .classList.add("d-none");

    input.focus();
}