document.addEventListener('DOMContentLoaded', function () {
    var elems = document.querySelectorAll('.collapsible');
    M.Collapsible.init(elems, { accordion: false });
});

const buttonUpdate = document.getElementById("btn-refresh");

if (buttonUpdate) {
    buttonUpdate.addEventListener('click', function (event) {
        Android.updateNews();
    });
}

const buttonBack = document.getElementById("btn-back");

if (buttonBack) {
    buttonBack.addEventListener('click', function (event) {
        Android.closePage();
    });
}

function updateFullContent(content) {
    const area = document.getElementById("full-content");
    if (!area) return;
    area.innerHTML = content;
}


