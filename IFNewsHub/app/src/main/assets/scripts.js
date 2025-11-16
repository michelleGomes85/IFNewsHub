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

    // Substitui cada linha que começa com número + ponto
    const listItems = content.split('\n').map(line => {
        const match = line.match(/^\s*(\d+)\.\s*(.+)$/);
        if (match) {
            return `<li>${match[2]}</li>`;
        }
        return line; // Mantém linhas que não começam com número
    });

    // Junta itens como lista ordenada
    const html = `<ol>${listItems.join('')}</ol>`;

    area.innerHTML = html;
}



