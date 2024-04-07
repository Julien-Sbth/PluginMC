package main

import (
	"APIMC/Menu"
	_ "APIMC/Menu"
	"log"
	"net/http"
)

func main() {
	http.Handle("/css/", http.StripPrefix("/css/", http.FileServer(http.Dir("templates/css"))))

	http.HandleFunc("/api", Menu.ApiHandler)
	http.HandleFunc("/api/user", Menu.UserHandler)
	http.HandleFunc("/data", Menu.ZombieKillsHandler)

	// Démarrer le serveur
	log.Println("Serveur Go démarré sur le port 8080")
	if err := http.ListenAndServe(":8080", nil); err != nil {
		log.Fatal("Erreur lors du démarrage du serveur:", err)
	}
}
