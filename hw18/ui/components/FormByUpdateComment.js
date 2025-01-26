import React from 'react'
import ErrorMessage from './ErrorMessage'
import BookRepository from '../repository/BookRepository'
import CommentRepository from '../repository/CommentRepository'

export default class FormByUpdateComment extends React.Component {

    bookRepository = new BookRepository
    commentRepository = new CommentRepository

    constructor(props) {
        super(props)
        this.state = {
            storageBooks: [],
            id: this.props.comment.id,
            text: this.props.comment.text,
            bookId: this.props.comment.bookId,
            errorMessage: ""
        }

        this.add = this.add.bind(this)
    }

    componentDidMount() {
        this.bookRepository.findAll().then(storageBooks => this.setState({storageBooks}))
    }


    render() {
        return (
            <form className="editForm">
                <h3>Редактировать комментарий</h3>
                <div className="row">
                    <label htmlFor="id">id</label>
                    <input id = "id" readOnly="readonly" name="id" value={this.props.comment.id} />
                </div> 

                <div className="row">
                    <label htmlFor="text-comment">Введите&nbsp;текст&nbsp;комментария</label>
                    <input id="text-comment" value = {this.state.text} onChange = {e => this.setState({text: e.target.value})} 
                        type="text" name="text" placeholder="Введите текст комментария"/>
                </div>

                <div className="row">
                    <label htmlFor="book-id">Выберите&nbsp;книгу</label>
                    <select value = {this.state.bookId}
                        onChange = {e => this.setState({bookId: e.target.value})} id="book-id" name="bookId">
                        {
                            this.state.storageBooks.map((book, i) => (
                                <option value={book.id} key={i}>
                                    {book.title}
                                </option>
                                )
                            )
                        }
                    </select>
                    {this.state.errorMessage}
                </div>

                <button onClick={this.add} type="button">Сохранить</button>
            </form>
        )
    }

    add() {
        var commentData = {
            id: this.state.id,
            text: this.state.text,
            bookId: this.state.bookId,
        }
        this.commentRepository.update(commentData)
        .then(commentDtoWeb => {if (!this.check(commentDtoWeb)) {return}})
        this.setState({errorMessage: ""})

    }

    check(commentDtoWeb) {
        if (commentDtoWeb.message === null) {
            return true
        }
        this.setState({errorMessage: <ErrorMessage message={commentDtoWeb.message}/>})
        return false
    }

}