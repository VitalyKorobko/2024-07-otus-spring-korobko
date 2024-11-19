import React from 'react'
import AuthorRepository from '../repository/AuthorRepository';
import GenreRepository from '../repository/GenreRepository';

export default class FormByNewBook extends React.Component {

    genreRepository = new GenreRepository
    authorRepository = new AuthorRepository

    constructor(props) {
        super(props)
        this.state = {
            storageAutors: [],
            storageGenres: [],
            id: 0,
            title: "",
            el: [],
            genres: [],
            author: "",
        }
    }

    componentDidMount() {
        this.authorRepository.findAll().then(storageAutors => this.setState({storageAutors}))
        this.genreRepository.findAll().then(storageGenres => this.setState({storageGenres}))
    }


    render() {
        return (
            <form>
                <h3>Добавить книгу</h3>
                <div className="row">
                    <input type="hidden" readOnly="readonly" name="id" value="0"/>
                </div>

                <div className="row">
                    <label htmlFor="title-book">Введите&nbsp;название&nbsp;книги</label>
                    <input onChange = {e => this.setState({title: e.target.value})} id="title-book" type="text" name="title" placeholder="Введите название книги"/>
                </div>

                <div className="row">
                    <label htmlFor="author-id">Выберите&nbsp;имя&nbsp;автора&nbsp;книги</label>
                    <select defaultValue = {"default"}onChange = {e => this.setState({author: e.target.value})} id="author-id" name="authorId">
                        <option disabled value={"default"}>--выберите автора--</option>
                        {
                            this.state.storageAutors.map((author, i) => (
                                <option value={author.id} key={i}>
                                    {author.fullName}
                                </option>
                                )
                            )
                        }
                    </select>
                </div>

                <div className="row">
                    <label htmlFor="genres-ids">Выберите&nbsp;жанры&nbsp;для&nbsp;книги</label>
                    <select id="genres-ids" multiple={true} onChange = {e => this.setState({genres: this.getGenres(e.target.selectedOptions)})} required>
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
                <button onClick={() =>this.props.addBook({
                        id: 0,
                        title: this.state.title,
                        authorId: this.state.author,
                        setGenresId: this.state.genres
                    })
                } type="button">Добавить</button>
            </form>
        )
    }

    getGenres(el) {
        var countArr = []
        for (let i = 0; i < el.length; i++) {
            countArr.push(i);
          }
        var genresArr = countArr.map(i => {return el.item(i).value})
        return genresArr
    }
}