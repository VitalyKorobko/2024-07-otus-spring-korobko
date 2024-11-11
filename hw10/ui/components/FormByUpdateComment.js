import React from 'react'
import ErrorMessage from './ErrorMessage'

export default class FormByUpdateComment extends React.Component {
    commentMessage = "Введите текст комментария"

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
        console.log("componentDidMount")
        fetch('/api/v1/book/all')
            .then(response => response.json())
            .then(storageBooks => this.setState({storageBooks}))
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
                    <input value = {this.state.text} onChange = {e => this.setState({text: e.target.value})} 
                        id="text-comment" type="text" name="text" placeholder="Введите текст комментария"/>
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
                    <ErrorMessage message = {this.state.errorMessage}/>
                </div>

                <button onClick={this.add} type="button">Сохранить</button>
            </form>
        )
    }

    add() {
        if (this.state.title === "") {
            this.setState({errorMessage: this.commentMessage})
            return
        } else {
            this.setState({errorMessage: ""})
        }
        var commentData = {
            id: this.state.id,
            text: this.state.text,
            bookId: this.state.bookId,
        }
        console.log("commentData")
        console.log(commentData)
        
        fetch('/api/v1/comment', {
            method: "PATCH",
            cache: "no-cache",
            credentials: "same-origin",
            headers: {
              "Content-Type": "application/json",
            },
            redirect: "follow",
            referrerPolicy: "no-referrer",
            body: JSON.stringify(commentData),
            }
        )

    }

}