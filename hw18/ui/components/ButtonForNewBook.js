import React from 'react'
import FormByNewBook from './FormByNewBook'



export default class ButtonForNewBook extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            form: "",
        }
        this.getForm = this.getForm.bind(this)
        this.hiddenForm = this.hiddenForm.bind(this)

    }

    render() {
        return (
            <div>
                <button type="button" onClick={this.getForm}>Добавить книгу</button>
                {this.state.form !== "" ? <button type="button" onClick = {this.hiddenForm} >Отменить</button> : ""}
                {this.state.form}
            </div>
            
        )
        
    }

    getForm() {
        this.setState({form: <FormByNewBook addBook = {this.props.addBook}/>})
    }

    hiddenForm() {
        this.setState({form: ""})
    }
}