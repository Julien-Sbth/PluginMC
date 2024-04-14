package main

import (
	"APIMC/Menu"
	_ "APIMC/Menu"
	"log"
	"net/http"
)

func main() {
	http.Handle("/templates/", http.StripPrefix("/templates/", http.FileServer(http.Dir("templates/"))))

	http.HandleFunc("/api", Menu.ApiHandler)
	http.HandleFunc("/api/user", Menu.UserHandler)
	http.HandleFunc("/data", Menu.ZombieKillsHandler)
	http.HandleFunc("/api/player/search", Menu.PlayerInfoHandler)
	http.HandleFunc("/", Menu.HandleMenu)

	log.Println("Serveur Go démarré sur le port 8080")
	if err := http.ListenAndServe(":8080", nil); err != nil {
		log.Fatal("Erreur lors du démarrage du serveur:", err)
	}
}
