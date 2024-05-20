module APIMC

go 1.21

replace Menu/Menu => ./Menu

require (
	github.com/gorilla/sessions v1.2.2
	github.com/gorilla/websocket v1.5.1
	github.com/mattn/go-sqlite3 v1.14.22
	golang.org/x/crypto v0.22.0
)

require (
	github.com/gorilla/securecookie v1.1.2 // indirect
	golang.org/x/net v0.21.0 // indirect
)
