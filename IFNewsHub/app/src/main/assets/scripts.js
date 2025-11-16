const buttonUpdate = document.getElementById("btn-refresh"); 

if (buttonUpdate) {
    buttonUpdate.addEventListener('click', function(event) {
        Android.updateNews();
    });
}

const buttonBack = document.getElementById("btn-back"); 

if (buttonBack) {
    buttonBack.addEventListener('click', function(event) {
        Android.closePage();
    });
}

