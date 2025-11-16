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

    // Remove loader
    area.innerHTML = '';

    // Cria lista se houver números no início das linhas
    const listItems = content.split('\n').map(line => {
        const match = line.match(/^\s*(\d+)\.\s*(.+)$/);
        if (match) return `<li>${match[2]}</li>`;
        return line;
    });

    const html = `<ol>${listItems.join('')}</ol>`;

    const typedContainer = document.createElement('div');
    area.appendChild(typedContainer);

    new Typed(typedContainer, {
        strings: [html],
        typeSpeed: 10,
        backSpeed: 0,
        showCursor: false,
        loop: false
    });
}





