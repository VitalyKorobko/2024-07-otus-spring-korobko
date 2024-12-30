export default class AuthorRepository {

    findAll() {
        return fetch('/datarest/author').then(response => response.json()).then(json => json._embedded.authors)
    }
}