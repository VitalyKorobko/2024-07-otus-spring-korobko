import React from 'react'
import FormByNewComment from './FormByNewComment'



export default class ButtonForNewComment extends React.Component {

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
                <button type="button" onClick={this.getForm}>Добавить комментарий</button>
                {this.state.form !== "" ? <button type="button" onClick = {this.hiddenForm} >Отменить</button> : ""}
                {this.state.form}
            </div>
            
        )
        
    }

    getForm() {
        this.setState({form: <FormByNewComment addComment = {this.props.addComment}/>})
    }

    hiddenForm() {
        this.setState({form: ""})
    }
}