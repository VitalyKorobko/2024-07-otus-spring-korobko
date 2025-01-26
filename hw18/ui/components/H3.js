import React from 'react'

export default class H3 extends React.Component {
    render () {
        return (
            <h3>{this.props.text}{this.props.value}</h3>
        )
    }    
}