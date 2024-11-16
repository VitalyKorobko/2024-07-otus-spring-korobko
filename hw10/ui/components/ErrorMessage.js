import React from 'react'

export default class ErrorMessage extends React.Component {

    constructor(props) {
        super(props)
    }
    render() {
        if (this.props.message!="") {
            return (                
                    <p className="red">{this.props.message}</p>
            ) 
        } else {
            return (
                 <p></p>
            )
        }
    }
}