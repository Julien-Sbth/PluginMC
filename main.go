package main

import (
	"APIMC/API"
	"APIMC/Connexion"
	"APIMC/Menu"
	_ "APIMC/Menu"
	"log"
	"net/http"
)

func main() {
	http.Handle("/templates/", http.StripPrefix("/templates/", http.FileServer(http.Dir("templates/"))))

	http.HandleFunc("/api", API.ApiHandler)
	http.HandleFunc("/api/user", API.UserHandler)
	http.HandleFunc("/data", API.DataHandlerAPI)
	http.HandleFunc("/login", Connexion.HandleLogin)
	http.HandleFunc("/register", Connexion.HandleRegister)
	http.HandleFunc("/about", Menu.HandleAbout)

	http.HandleFunc("/logout", Connexion.HandleLogout)
	http.HandleFunc("/shop", Menu.HandleShop)
	http.HandleFunc("/api/player/search", API.PlayerInfoHandler)
	http.HandleFunc("/", Menu.HandleMenu)

	log.Println("Serveur Go démarré sur le port 8080")
	if err := http.ListenAndServe(":8080", nil); err != nil {
		log.Fatal("Erreur lors du démarrage du serveur:", err)
	}
}
