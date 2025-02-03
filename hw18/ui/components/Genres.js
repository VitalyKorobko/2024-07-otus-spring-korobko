import React from 'react'

export default class Genres extends React.Component {

    render() {
        return (
            this.props.list.map((genre, i) => (
                <tr className="booksTableItem" key={i}>
                    <td className="booksTableItem">"n/a"</td>
                    <td className="booksTableItem">{genre.name}</td>
                </tr>
                )
            )
        )
    }
}


