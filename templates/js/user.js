const images = document.querySelectorAll('.grid-item img, .blocks2 img');

images.forEach(image => {
    if (image.naturalWidth < image.parentNode.offsetWidth && image.naturalHeight < image.parentNode.offsetHeight) {
        image.style.width = '100%';
        image.style.height = '100%';
    }
});