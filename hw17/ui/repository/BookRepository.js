export default class BookRepository {

    findAll(){
        return fetch('/api/v1/book').then(response => response.json())
    }

    findById(id){
        return fetch('/api/v1/book/' + id).then(response => response.json())
    }

    insert(book){
        return fetch('/api/v1/book', {
                    method: "POST", 
                    cache: "no-cache",
                    credentials: "same-origin",
                    headers: {
                    "Content-Type": "application/json",
                    },
                    redirect: "follow",
                    referrerPolicy: "no-referrer",
                    body: JSON.stringify(book),
                    }
                ).then(response => response.json())
    }

    update(book) {
        return fetch('/api/v1/book/' + book.id, {
                    method: "PATCH",
                    cache: "no-cache",
                    credentials: "same-origin",
                    headers: {
                    "Content-Type": "application/json",
                    },
                    redirect: "follow",
                    referrerPolicy: "no-referrer",
                    body: JSON.stringify(book),
                    }
                ).then(response => response.json())
    }

    deleteById(id){
        fetch('/api/v1/book/' + id, {
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