import React from 'react'

export default class Authors extends React.Component {

    render() {
        return (
            this.props.list.map((author, i) => (
                <tr className="booksTableItem" key={i}>
                    <td className="booksTableItem">"n/a"</td>
                    <td className="booksTableItem">{author.fullName}</td>
                </tr>
                )
            )
        )
    }

}
