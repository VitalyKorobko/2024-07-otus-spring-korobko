export default class GenreRepository{

    findAll(){
        return fetch('/api/v1/genres').then(response => response.json())
    }

}