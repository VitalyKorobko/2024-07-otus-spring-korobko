import React from "react"

export default class TableTitle extends React.Component {

    constructor(props) {
        super(props)
    }

    render() {
        return (
            <thead>
                {this.props.columnName === "" ? <tr></tr>:
                    <tr className="booksTableItem"> 
                        <th className="booksTableItem">ID</th>
                        <th className="booksTableItem">{this.props.columnName}</th>
                    </tr>
                }
            </thead>
        )
    }
}