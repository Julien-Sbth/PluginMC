package main

import (
	"APIMC/API"
	"APIMC/Connexion"
	"APIMC/Menu"
	"log"
	"net/http"
)

func main() {
	http.Handle("/templates/", http.StripPrefix("/templates/", http.FileServer(http.Dir("templates/"))))

	http.HandleFunc("/api", API.ApiHandler)
	http.HandleFunc("/data", API.DataHandlerAPI)
	http.HandleFunc("/login", Connexion.HandleLogin)
	http.HandleFunc("/register", API.HandleRegister)
	http.HandleFunc("/admin", API.HandleAdministrator)
	http.HandleFunc("/about", Menu.HandleAbout)
	http.HandleFunc("/delete-account", Connexion.HandleDeleteAccount)
	http.HandleFunc("/logout", Connexion.HandleLogout)
	http.HandleFunc("/settings", Connexion.HandleProfile)
	http.HandleFunc("/shop", API.HandleShop)
	http.HandleFunc("/api/user/panier", API.HandlePanier)
	http.HandleFunc("/api/user", API.UserHandler)
	http.HandleFunc("/api/user/addtocart", API.HandleAddToCart)
	http.HandleFunc("/api/user/panier/buy", API.HandleBuyItem)
	http.HandleFunc("/api/user/panier/canbuy", API.CanBuyItem)
	http.HandleFunc("/items", API.SendRequest)
	http.HandleFunc("/delete", API.DeletePlayerHandler)
	http.HandleFunc("/update", API.UpdatePlayerHandler)
	http.HandleFunc("/items/received", API.HandleItemReceived)

	http.HandleFunc("/panel", API.HandlePanel)
	http.HandleFunc("/create", API.CreatePlayerHandler)
	http.HandleFunc("/read", API.GetAllPlayersHandler)
	http.HandleFunc("/api/player/search", API.PlayerInfoHandler)
	http.HandleFunc("/", Menu.HandleMenu)

	log.Println("Serveur Go démarré sur le port 8080")
	if err := http.ListenAndServe(":8080", nil); err != nil {
		log.Fatal("Erreur lors du démarrage du serveur:", err)
	}
}
