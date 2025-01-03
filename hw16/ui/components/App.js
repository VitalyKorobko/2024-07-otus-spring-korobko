import React, { useEffect } from 'react'
import H3 from './H3'
import Authors from './Authors'
import Genres from './Genres'
import ButtonForNewBook from './ButtonForNewBook'
import FormByUpdateBook from './FormByUpdateBook'
import FormByUpdateComment from './FormByUpdateComment'
import TableTitle from './TableTitle'
import ErrorMessage from './ErrorMessage'
import ButtonForNewComment from './ButtonForNewComment'

import BookRepository from '../repository/BookRepository'
import GenreRepository from '../repository/GenreRepository'
import AuthorRepository from '../repository/AuthorRepository'
import CommentRepository  from '../repository/CommentRepository'




export default class App extends React.Component {

    bookRepository = new BookRepository
    genreRepository = new GenreRepository
    authorRepository = new AuthorRepository
    commentRepository = new CommentRepository


    title_ = "Список книг"
    genresTitle = "Список жанров"
    authorsTitle = "Список авторов"
    commentsTitle = "Выберите книгу для просмотра комментариев"
    commentTitle = "Список комментариев для книги: "


    constructor() {
        super();
        this.state = {
            books: [], 
            authors: [],
            genres: [],
            booksWithComments: [],
            comments: [], 
            title: this.title_,
            columnName: "Title",
            currentBook: "",
            buttonForNewBook: "",
            buttonForNewComment: "",
            editBook: "",
            editComment: "",
            cancelEditButton: "",
            errorMessage: <ErrorMessage message=""/>
        };
        this.getBooks = this.getBooks.bind(this)
        this.getAuthors = this.getAuthors.bind(this)
        this.getGenres = this.getGenres.bind(this)
        this.getBookWithComments = this.getBookWithComments.bind(this)
        this.getComments = this.getComments.bind(this)
        this.deleteComment = this.deleteComment.bind(this)
        this.addBook = this.addBook.bind(this)
        this.addComment = this.addComment.bind(this)
        this.editBook = this.editBook.bind(this)
        this.editComment = this.editComment.bind(this)
        this.clearList = this.clearList.bind(this)

    }

    componentDidMount() {
        this.bookRepository.findAll().then(books => this.setState({books}))
        this.setState({buttonForNewBook: <ButtonForNewBook addBook = {this.addBook}/>})
    }

    componentDidUpdate(prevProp) {
        if (this.state.title === this.title_) {
            document.title = this.title_
        }
        if (this.state.title === this.genresTitle) {
            document.title = this.genresTitle
        }
        if (this.state.title === this.authorsTitle) {
            document.title = this.authorsTitle
        }
        if (this.state.title === this.commentsTitle) {
            document.title = this.commentsTitle
        }
        if (this.state.title === this.commentTitle) {
            document.title =this.commentTitle + this.state.currentBook.title
        }

    }
    

    render() {
        return (
            <React.Fragment>
                <header className="container">
                    <span className="logo">HomeWork 9</span>
                    <nav>
                        <span onClick={this.getBooks}>Книги</span>
                        <span onClick={this.getGenres}>Жанры</span>
                        <span onClick={this.getAuthors}>Авторы</span>
                        <span onClick={this.getBookWithComments}>Комментарии</span>
                    </nav>
                </header>
                <main className="container">
                    <H3 text = {this.state.title} value = {this.state.currentBook.title} />
                    <table className = "booksTable">
                        <TableTitle columnName = {this.state.columnName}/>
                        <tbody>
                        {
                            this.state.books.map((book, i) => (
                                    <tr className="booksTableItem" key={i}>
                                        <td className="booksTableItem">{book.id}</td>
                                        <td className="booksTableItem">{book.title}</td>
                                        <td className="booksTableItem">
                                            <button onClick={e => this.editBook(book)} className="edit-btn" type="button">Подробнее</button>
                                            <button onClick={e => this.deleteBook(book)} className="edit-btn" type="button">Удалить</button>
                                        </td>
                                    </tr>
                                )
                            )
                        }
                            <Authors list = {this.state.authors}/>
                            <Genres list = {this.state.genres}/>
                        {
                            this.state.booksWithComments.map((book, i) => (
                                <tr className="booksTableItem" key={i}>
                                    <td className="booksTableItem">{book.id}</td>
                                    <td onClick={e => this.getComments(book.id)} className="link booksTableItem">{book.title}</td>
                                </tr>
                                )
                            )
                        }
                        {
                            this.state.comments.map((comment, i) => (
                                <tr className="booksTableItem" key={i}>
                                    <td className="booksTableItem">{comment.id}</td>
                                    <td className="booksTableItem">{comment.text}</td>
                                    <td className="booksTableItem">
                                        <button onClick={e => this.editComment(comment)} className="edit-btn" type="button">Редактировать</button>
                                        <button onClick={e => this.deleteComment(comment)} className="edit-btn" type="button">Удалить</button>
                                    </td>
                                </tr>
                                )
                            )
                        }
                        </tbody>
                    </table>
                    {this.state.editBook}
                    {this.state.editComment}
                    {this.state.cancelEditButton}
                    {this.state.buttonForNewBook}
                    {this.state.buttonForNewComment}
                    {this.state.errorMessage}
                </main>
            </React.Fragment>
        )
    }

    editBook(book){
        this.clearList()
        this.setState({columnName: ""})
        this.setState({editBook: <FormByUpdateBook book = {book}/>})
        this.setState({cancelEditButton: <button onClick={this.getBooks} type="button">Вернуться</button>})
    }

    editComment(comment){
        this.clearList()
        this.setState({columnName: ""})
        this.setState({editComment: <FormByUpdateComment comment = {comment}/>})
        this.setState({cancelEditButton: <button onClick={this.getBookWithComments} type="button">Вернуться</button>})

    }

    deleteBook(book){
        this.bookRepository.deleteById(book.id)
        var books = this.state.books;
        var reduceBooksArr = books.filter(function(b){ return b.id !== book.id } );
        this.setState({books: reduceBooksArr})
    }

    deleteComment(comment){
        this.commentRepository.deleteById(comment.id)
        var comments = this.state.comments;
        var reduceCommenrsArr = comments.filter(function(c){ return c.id !== comment.id } );
        this.setState({comments: reduceCommenrsArr})
    }

    getBooks() {
        this.clearList()
        this.setState({buttonForNewBook: <ButtonForNewBook addBook = {this.addBook}/>})
        this.setState({title: this.title_})
        this.setState({columnName: "Title"})
        this.bookRepository.findAll().then(books => this.setState({books}))
    }

    addBook(book) { 
        this.bookRepository.insert(book)
        .then(bookDtoWeb =>  !this.check(bookDtoWeb))
        .then(()=> this.bookRepository.findAll().then(books => this.setState({books})))
        this.setState({errorMessage: <ErrorMessage message=""/>})
    }

    check(dtoWeb) {
        if (dtoWeb.message === null) {
            return true
        }
        this.setState({errorMessage: <ErrorMessage message={dtoWeb.message}/>})
        return false
    }

    addComment(comment) {
        if (comment.bookId==="") {
            this.commentRepository.insert(comment)
            .then(commentDtoWeb => this.check(commentDtoWeb))
        } else {
            this.commentRepository.insert(comment)
            .then(commentDtoWeb => this.check(commentDtoWeb))
            .then(()=> this.commentRepository.findAllByBookId(comment.bookId).then(comments => this.setState({comments})))
            this.setState({errorMessage: <ErrorMessage message=""/>})
        }

    }

    getAuthors() {
        this.clearList()
        this.setState({title: this.authorsTitle})
        this.setState({columnName: "Fullname"})
        this.authorRepository.findAll().then(authors => this.setState({authors}));
    }

    getGenres() {
        this.clearList()
        this.setState({title: this.genresTitle})
        this.setState({columnName: "Name"})
        this.genreRepository.findAll().then(genres => this.setState({genres}));
    }

    getBookWithComments() {
        this.clearList()
        this.setState({title: this.commentsTitle})
        this.setState({columnName: "Text"})
        this.bookRepository.findAll().then(booksWithComments => this.setState({booksWithComments}))
    }

    getComments(bookId){
        this.clearList()
        this.setState({title: this.commentTitle})
        this.setState({columnName: "Text"})
        this.setState({buttonForNewComment: <ButtonForNewComment addComment = {this.addComment}/>})
        this.bookRepository.findById(bookId).then(currentBook => this.setState({currentBook}))
        this.commentRepository.findAllByBookId(bookId).then(comments => this.setState({comments}))
    }

    clearList() {
        this.setState({books: []})
        this.setState({authors: []})
        this.setState({genres: []})
        this.setState({booksWithComments: []})
        this.setState({comments: []})
        this.setState({currentBook: ""})
        this.setState({buttonForNewBook: ""})
        this.setState({buttonForNewComment: ""})
        this.setState({editBook: ""})
        this.setState({editComment: ""})
        this.setState({cancelEditButton: ""})
        this.setState({errorMessage: <ErrorMessage message=""/>})
    }

};
