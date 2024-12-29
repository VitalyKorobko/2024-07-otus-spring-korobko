export default class GenreRepository{

    findAll(){
//        return fetch('/api/v1/genres').then(response => response.json())
        return fetch('/datarest/genre').then(response => response.json()).then(json => json._embedded.genres)
    }

}