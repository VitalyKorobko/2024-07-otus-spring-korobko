export default class AuthorRepository {

    findAll() {
        return fetch('/api/v1/authors').then(response => response.json())
    }
}