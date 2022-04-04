window.onload = function() {
    for (element of document.getElementsByClassName('section-title')) {
        element.addEventListener('click', function() {
            this.nextElementSibling.classList.toggle('section-list-opened');
        });
    }
    var activeSectionItem = document.getElementById('section-item-active');
    console.log(activeSectionItem)
    if (activeSectionItem != null) {
        activeSectionItem.scrollIntoView();
        window.scrollTo(0, 0);
    }
}