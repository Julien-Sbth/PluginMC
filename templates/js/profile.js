function deleteAnimation() {
    var form = document.getElementById('deleteForm');
    form.classList.add('delete-animation');
    setTimeout(function () {
        form.submit();
    }, 500);
}