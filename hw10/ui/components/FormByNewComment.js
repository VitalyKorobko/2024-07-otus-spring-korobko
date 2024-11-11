import React from 'react'

export default class FormByNewComment extends React.Component {

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
        console.log("componentDidMount")
        fetch('/api/v1/book/all')
            .then(response => response.json())
            .then(storageBooks => this.setState({storageBooks}))
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
                    <select onChange = {e => this.setState({bookId: e.target.value})} id="book-id" name="bookId">
                        <option disabled selected value>--выберите книгу--</option>
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