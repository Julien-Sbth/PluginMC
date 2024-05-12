function addToCart(itemID) {
    window.location.href = "/api/user/addtocart?itemID=" + itemID;
}
function switchTab(type) {
    const inventorySlots = document.querySelectorAll('.inventory-slot');

    inventorySlots.forEach(slot => {
        if (slot.classList.contains(type) || type === 'all') {
            slot.style.display = 'block';
        } else {
            slot.style.display = 'none';
        }
    });
}
const images = document.querySelectorAll('.grid-item img, .blocks2 img');

images.forEach(image => {
    if (image.naturalWidth < image.parentNode.offsetWidth && image.naturalHeight < image.parentNode.offsetHeight) {
        image.style.width = '100%';
        image.style.height = '100%';
    }
});