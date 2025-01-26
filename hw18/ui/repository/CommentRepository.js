export default class CommentRepository{

    findAllByBookId(bookId) {
        return fetch('/api/v1/comment?book_id=' + bookId).then(response => response.json())
    }

    insert(comment) {
        return fetch('/api/v1/comment', {
                    method: "POST",
                    cache: "no-cache",
                    credentials: "same-origin",
                    headers: {
                    "Content-Type": "application/json",
                    },
                    redirect: "follow",
                    referrerPolicy: "no-referrer",
                    body: JSON.stringify(comment),
                    }
                ).then(response => response.json())
    }

    update(comment) {
        return fetch('/api/v1/comment/' + comment.id, {
                    method: "PATCH",
                    cache: "no-cache",
                    credentials: "same-origin",
                    headers: {
                    "Content-Type": "application/json",
                    },
                    redirect: "follow",
                    referrerPolicy: "no-referrer",
                    body: JSON.stringify(comment),
                    }
                ).then(response => response.json())
    }

    deleteById(id) {
        fetch('/api/v1/comment/' + id, {
            method: "DELETE",
            cache: "no-cache",
            credentials: "same-origin",
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            redirect: "follow",
            referrerPolicy: "no-referrer",
          });
    }
}