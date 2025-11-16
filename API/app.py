from flask import Flask, jsonify, request
import requests
from bs4 import BeautifulSoup

from urllib.parse import unquote

app = Flask(__name__)

# =========================================
# Rota: /noticias_if_bq
# Descrição: Retorna a lista de notícias do IF Sudeste MG - Barbacena
# Método: GET
# Parâmetros: nenhum
# Resposta de sucesso: JSON array de notícias
# [
#   {
#       "titulo": "Título da notícia",
#       "descricao": "Descrição resumida",
#       "link": "URL da notícia",
#       "tags": ["tag1", "tag2"]
#   }, ...
# ]
# Erros possíveis:
#   500 Internal Server Error -> falha ao buscar ou processar notícias
# =========================================
@app.route('/noticias_if_bq')
def noticias():

    url_lista = "https://www.ifsudestemg.edu.br/noticias/barbacena/"

    try:
        response = requests.get(url_lista, timeout=15)
        response.raise_for_status()
        soup = BeautifulSoup(response.text, 'html.parser')
        articles = soup.find_all('article', class_='tileItem')

        noticias_lista = []

        for article in articles:

            titulo_tag = article.find('h2', class_='tileHeadline')
            link = None
            titulo = None
            if titulo_tag:
                link_tag = titulo_tag.find('a', class_='summary url')
                if link_tag:
                    titulo = link_tag.get_text(strip=True)
                    link = link_tag['href']

            descricao_tag = article.find('span', class_='description')
            descricao = descricao_tag.get_text(strip=True) if descricao_tag else ""

            tags = []
            tags_container = article.find('div', class_='keywords')
            if tags_container:
                tag_links = tags_container.find_all('a', class_='link-category')
                tags = [tag.get_text(strip=True) for tag in tag_links]

            noticias_lista.append({
                'titulo': titulo,
                'descricao': descricao,
                'link': link,
                'tags': tags,
            })

        return jsonify(noticias_lista)

    except Exception as e:
        print("Erro geral:", e)
        return jsonify({"error": "Falha ao carregar notícias"}), 500

# =========================================
# Rota: /noticia/conteudo_texto
# Descrição: Retorna o conteúdo completo de uma notícia a partir de uma URL
# Método: GET
# Parâmetros:
#   url (obrigatório) -> URL completa da notícia
# Validação:
#   - URL deve começar com "https://www.ifsudestemg.edu.br/"
# Resposta de sucesso: JSON com o campo "texto"
# {
#   "texto": "Conteúdo completo da notícia..."
# }
# Erros possíveis:
#   400 Bad Request -> se parâmetro 'url' ausente
#   403 Forbidden -> se URL não autorizada
#   500 Internal Server Error -> falha ao processar a notícia
#
# Observação: se não encontrar conteúdo, retorna "texto": ""
# =========================================
@app.route('/noticia/conteudo_texto')
def conteudo_texto():

    url = request.args.get('url')
    if not url:
        return jsonify({"error": "Parâmetro 'url' ausente"}), 400

    try:
        
        url = unquote(url)

        if not url.startswith("https://www.ifsudestemg.edu.br/"):
            return jsonify({"error": "URL não autorizada"}), 403

        headers = {'User-Agent': 'Mozilla/5.0 (compatible; IF-Scraper/1.0)'}
        response = requests.get(url, headers=headers, timeout=12)
        response.raise_for_status()

        soup = BeautifulSoup(response.text, 'html.parser')
        corpo = soup.find('div', property='rnews:articleBody')

        if corpo:
            texto = corpo.get_text(separator='\n', strip=True)
        else:
            corpo = soup.find('div', class_='documentContent')
            texto = corpo.get_text(separator='\n', strip=True) if corpo else ""

        return jsonify({"texto": texto})

    except Exception as e:
        print(f"Erro ao extrair conteúdo de {url}: {e}")
        return jsonify({"error": "Falha ao processar a notícia"}), 500

@app.route('/')
def hello():
    return 'Michelle: Hello World!'

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)