function loadItems(callback) {
    $.ajax({
        url: 'http://localhost:8080/items'
    }).done(function (items) {
        console.log('Items loaded: ' + JSON.parse(""));
        callback(items);
    })
}
