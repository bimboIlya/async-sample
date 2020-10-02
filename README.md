# async-sample
Downloading JSON using RxJava and Coroutines from fake "server" made with OkHttp interceptor.
All data is taken from https://jsonplaceholder.typicode.com and saved locally so app won't spam real server with same requests.
Data consists of 10 Users, each has 10 Posts and each Post has 5 Comments.
App allows to "download" either all Users, all Posts or all Comments using RxJava or plain Coroutines or Flow.