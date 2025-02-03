import React from 'react'
import BookRepository from '../repository/BookRepository'

export default class FormByNewComment extends React.Component {

    bookRepository = new BookRepository

    constructor(props) {
        super(props)
        this.state = {
            storageBooks: [],
            id: 0,
            text: "",
            bookId: ""
        }
    }

    componentDidMount() {
        this.bookRepository.findAll().then(storageBooks => this.setState({storageBooks}))
    }


    render() {
        return (
            <form>
                <h3>Добавить комментарий</h3>
                <div className="row">
                    <input type="hidden" readOnly="readonly" name="id" value="0"/>
                </div>

                <div className="row">
                    <label htmlFor="text-comment">Введите&nbsp;текст&nbsp;комментария</label>
                    <input onChange = {e => this.setState({text: e.target.value})} id="text-comment" type="text" name="text" placeholder="Введите текст комментария"/>
                </div>

                <div className="row">
                    <label htmlFor="book-id">Выберите&nbsp;книгу</label>
                    <select id="book-id" defaultValue="default" onChange = {e => this.setState({bookId: e.target.value})} name="bookId">
                        <option disabled value="default">--выберите книгу--</option>
                        {
                            this.state.storageBooks.map((book, i) => (
                                <option value={book.id} key={i}>
                                    {book.title}
                                </option>
                                )
                            )
                        }
                    </select>
                </div>

                <button onClick={() =>this.props.addComment({
                        id: 0,
                        text: this.state.text,
                        bookId: this.state.bookId,
                    })
                } type="button">Добавить</button>
            </form>
        )
    }

}