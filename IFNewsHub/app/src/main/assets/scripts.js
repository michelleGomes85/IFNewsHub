const buttonUpdate = document.getElementById("btn-refresh"); 

if (buttonUpdate) {
    buttonUpdate.addEventListener('click', function(event) {
        Android.updateNews();
    });
}