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
	http.HandleFunc("/admin", Connexion.HandleAdministrator)
	http.HandleFunc("/about", Menu.HandleAbout)
	http.HandleFunc("/delete-account", Connexion.HandleDeleteAccount)
	http.HandleFunc("/logout", Connexion.HandleLogout)
	http.HandleFunc("/settings", Connexion.HandleProfile)
	http.HandleFunc("/shop", API.HandleShop)
	http.HandleFunc("/panel", Menu.HandlePanel)
	http.HandleFunc("/create", Menu.CreatePlayerHandler)
	http.HandleFunc("/read", Menu.GetAllPlayersHandler)
	http.HandleFunc("/update", Menu.UpdatePlayerHandler)
	http.HandleFunc("/delete", Menu.DeletePlayerHandler)
	http.HandleFunc("/api/player/search", API.PlayerInfoHandler)
	http.HandleFunc("/", Menu.HandleMenu)

	log.Println("Serveur Go démarré sur le port 8080")
	if err := http.ListenAndServe(":8080", nil); err != nil {
		log.Fatal("Erreur lors du démarrage du serveur:", err)
	}
}
