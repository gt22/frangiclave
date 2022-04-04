function initSidebar() {
    for (element of document.getElementsByClassName('section-title')) {
        element.addEventListener('click', function() {
            this.nextElementSibling.classList.toggle('section-list-opened');
        });
    }
    var activeSectionItem = document.getElementById('section-item-active');
    if (activeSectionItem != null) {
        activeSectionItem.scrollIntoView();
        window.scrollTo(0, 0);
    }
}

document.addEventListener('DOMContentLoaded', initSidebar);