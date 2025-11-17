# IF News Hub

Aplicativo que consome notícias do **IF Sudeste MG** via API Flask e gera resumos concisos por inteligência artificial, apresentando os principais pontos de cada notícia de forma clara e interativa.

<img src="assets/app_design.gif" alt="App Preview" width="300"/>

---

## 📌 Descrição do Projeto

O **IF News Hub** combina backend, frontend e IA para fornecer uma experiência completa de leitura de notícias:

- **Backend:** API construída com **Flask**, responsável por coletar e disponibilizar notícias oficiais do IF Sudeste MG.
- **Frontend / App Android:** Interface que consome a API, mostrando título, descrição, tags e resumo gerado por IA.
- **Resumo por IA:** Cada notícia é enviada para um modelo de inteligência artificial (ex: Gemini), que gera um resumo em tópicos numerados, conectando contexto, desenvolvimento e impacto/conclusão.
- **Experiência interativa:** Loader animado enquanto o resumo é gerado e efeito de digitação simulando a escrita da IA.
- **Cache local:** Resumos armazenados no dispositivo para evitar chamadas repetidas à API e melhorar a performance.

---

## 🏗 Estrutura do Projeto



```
IFNewsHub/
├── API/ # Backend Flask que fornece notícias
│ ├── app.py
│ ├── requirements.txt
│ └── ...
├── IFNewsHub/ # Código do app Android/WebView
│ ├── src/
│ ├── assets/
│ └── ...
├── README.md
└── .gitignore
```

### Backend (API Flask)

- Coleta notícias do IF Sudeste MG.
- Retorna JSON com título, descrição, conteúdo e tags.
- Endpoints principais:
  - `GET /news` → Lista de notícias.
  - `GET /news/<id>` → Conteúdo completo da notícia.

### Frontend / App Android

- **WebView** exibe o conteúdo HTML da notícia.
- **Collapsible Materialize** para mostrar resumo gerado por IA.
- Loader animado enquanto a IA processa a notícia.
- Resumo digitado dinamicamente linha por linha, simulando escrita da IA.
- Tags e links interativos.

### IA (Resumo)

- Recebe o texto completo da notícia.
- Gera resumo em **3 tópicos numerados**, com:
  - Título breve por tópico.
  - 1–2 frases completas.
  - Conexão lógica entre tópicos (contexto → desenvolvimento → impacto/conclusão).

### Cache

- Resumos armazenados localmente usando `NewsCache`.
- Expiração padrão de 24 horas.
- Evita chamadas desnecessárias à API ou ao modelo de IA.

---

## ⚡ Funcionalidades

1. Lista de notícias do IF Sudeste MG.
2. Exibição detalhada da notícia com link externo.
3. Resumo gerado por IA com:
   - Tópicos numerados.
   - Efeito de digitação.
   - Loader animado durante a geração.
4. Tags interativas por notícia.
5. Cache local para otimização.

---

## 🛠 Instalação e Uso

### Backend (Flask)

```bash
cd API
```

```bash
python -m venv venv
```

```bash
source venv/bin/activate   # Linux/Mac
venv\Scripts\activate      # Windows
```

```bash
pip install -r requirements.txt
python app.py
```


API estará disponível em `http://localhost:5000`.

### Frontend / App

- Abra o projeto IFNewsHub no Android Studio.

- Conecte o backend Flask na URL da API.

- Compile e rode no dispositivo ou emulador.

## 🎨 Tecnologias Utilizadas

- Backend: Python, Flask
- Frontend: Android (Java), WebView, Materialize CSS
- IA: Gemini (ou outro modelo de linguagem)
- Cache: LocalStorage / NewsCache
- Efeitos visuais: Typed.js, loader de três bolinhas


## 💡 Observações

- Certifique-se de que o backend Flask esteja rodando antes de abrir o app.
- Resumos só são gerados se a notícia tiver conteúdo completo.
- Cache evita recarregar resumos já gerados nos próximos acessos.

## 🔗 Contribuição

- Contribuições são bem-vindas!
- Para sugerir melhorias, abra um Pull Request ou Issue.
