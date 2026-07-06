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
function toggleUserMenu(event) {
    event.stopPropagation();
    var dropdown = document.getElementById("userDropdown");
    var trigger = document.getElementById("userMenuTrigger");
    dropdown.classList.toggle("show");
    trigger.classList.toggle("open");
}

document.addEventListener("click", function (e) {
    var dropdown = document.getElementById("userDropdown");
    var trigger = document.getElementById("userMenuTrigger");

    if (
        dropdown &&
        dropdown.classList.contains("show") &&
        !dropdown.contains(e.target) &&
        !trigger.contains(e.target)
    ) {
        dropdown.classList.remove("show");
        trigger.classList.remove("open");
    }
});