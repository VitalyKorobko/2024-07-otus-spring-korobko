import React from 'react'
import ErrorMessage from './ErrorMessage'
import BookRepository from '../repository/BookRepository'
import AuthorRepository from '../repository/AuthorRepository';
import GenreRepository from '../repository/GenreRepository';

export default class FormByUpdateBook extends React.Component {

    bookRepository = new BookRepository
    genreRepository = new GenreRepository
    authorRepository = new AuthorRepository

    constructor(props) {
        super(props)
        this.state = {
            storageAutors: [],
            storageGenres: [],
            id: this.props.book.id,
            title: this.props.book.title,
            el: [],
            genres: this.getGenres(this.props.book.listDtoGenres),
            author: this.props.book.authorDto.id,
            errorMessage: ""
        }

        this.add = this.add.bind(this)
        this.convertGenres = this.convertGenres.bind(this)
    }

    componentDidMount() {
        this.authorRepository.findAll().then(storageAutors => this.setState({storageAutors}))
        this.genreRepository.findAll().then(storageGenres => this.setState({storageGenres}))
    }


    render() {
        return (
            <form className="editForm">
                <h3>Редактировать книгу</h3>
                <div className="row">
                    <label htmlFor="id">id</label>
                    <input id = "id" readOnly="readonly" name="id" value={this.props.book.id} />
                </div> 

                <div className="row">
                    <label htmlFor="title-book">Введите&nbsp;название&nbsp;книги</label>
                    <input value = {this.state.title} onChange = {e => this.setState({title: e.target.value})} 
                        id="title-book" type="text" name="title" placeholder="Введите название книги"/>
                </div>

                <div className="row">
                    <label htmlFor="author-id">Выберите&nbsp;имя&nbsp;автора&nbsp;книги</label>
                    <select value = {this.state.author}
                        onChange = {e => this.setState({author: e.target.value})} id="author-id" name="authorId">
                        {
                            this.state.storageAutors.map((author, i) => (
                                <option value={author.id} key={i}>
                                    {author.fullName}
                                </option>
                                )
                            )
                        }
                    </select>
                    {this.state.errorMessage}
                </div>

                <div className="row">
                    <label htmlFor="genres-ids">Выберите&nbsp;жанры&nbsp;для&nbsp;книги</label>
                    <select id="genres-ids" multiple={true} value = {this.state.genres}
                        onChange = {e => this.convertGenres(e.target.selectedOptions)} required>
                        {
                            this.state.storageGenres.map((genre, i) => (
                                <option value={genre.id} key={i}>
                                    {genre.name}
                                </option>
                                )
                            )
                        }
                    </select>
                </div>
                <button onClick={this.add} type="button">Сохранить</button>
            </form>
        )
    }

    getGenres(json){
        var setGenres = json.map(el => {return el.id})
        return setGenres
    }

    add() {
        var countArr = []
        for (let i = 0; i < this.state.el.length; i++) {
            countArr.push(i);
          }
        var genres = countArr.map(i => {return this.state.el.item(i).value})
        this.setState({genres: genres})
        var bookData = {
            id: this.state.id,
            title: this.state.title,
            authorId: this.state.author,
            setGenresId: this.state.genres
        }
        this.bookRepository.update(bookData)
        .then(bookDtoWeb => {if (!this.check(bookDtoWeb)) {return}})
        this.setState({errorMessage: ""})
    }

    check(bookDtoWeb) {
        if (bookDtoWeb.message === null) {
            return true
        }
        this.setState({errorMessage: <ErrorMessage message={bookDtoWeb.message}/>})
        return false
    }


    convertGenres(el) {
        this.setState({el: el})
        var countArr = []
        for (let i = 0; i < el.length; i++) {
            countArr.push(i);
          }
        var genresList = countArr.map(i => {return el.item(i).value})
        this.setState({genres: genresList})
        return genresList
    }



}