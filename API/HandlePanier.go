package API

import (
	"APIMC/Connexion"
	"database/sql"
	"html/template"
	"log"
	"net/http"
)

func HandleAddToCart(w http.ResponseWriter, r *http.Request) {
	if r.Method != "GET" {
		http.Error(w, "Méthode non autorisée", http.StatusMethodNotAllowed)
		return
	}

	itemID := r.URL.Query().Get("itemID")
	addToCart(itemID)

	// Rediriger vers la page de panier ou de connexion selon l'état de connexion de l'utilisateur
	session, err := Connexion.Store.Get(r, Connexion.SessionName)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	if session.Values["username"] == nil {
		http.Redirect(w, r, "/api/user/login", http.StatusSeeOther)
		return
	}

	http.Redirect(w, r, "/api/user/panier", http.StatusSeeOther)
}

func HandlePanier(w http.ResponseWriter, r *http.Request) {
	db, err := sql.Open("sqlite3", "database.sqlite")
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	defer db.Close()

	session, err := Connexion.Store.Get(r, Connexion.SessionName)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	var username string
	if session.Values["username"] != nil {
		username = session.Values["username"].(string)
	}

	if username != "" {
		var isAdmin bool
		err = db.QueryRow("SELECT est_admin FROM utilisateurs WHERE username = ? AND est_admin = 1", username).Scan(&isAdmin)
		if err != nil {
			if err != sql.ErrNoRows {
				http.Error(w, err.Error(), http.StatusInternalServerError)
				return
			}
			log.Println("L'utilisateur", username, "n'est pas un administrateur")
		} else {
			isAdmin = true
			log.Println("L'utilisateur", username, "est un administrateur")
		}

		// Charger les données de panier
		// Ici, nous devons utiliser une logique pour charger les données du panier
		// playerData.Panier = loadPanierData(username)

		tmpl, err := template.ParseFiles("templates/html/Menu/panier.html")
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		playerData := PlayerData{
			Username:     username,
			IsLoggedIn:   true,
			IsAdmin:      isAdmin,
			Panier:       playerData.Panier, // Assurez-vous que playerData.Panier est correctement chargé
			ErrorMessage: "Joueur Inconnu, veuillez en choisir un autre",
		}

		err = tmpl.Execute(w, playerData)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
		return
	}

	// Si l'utilisateur n'est pas connecté, rediriger vers la page de connexion
	http.Redirect(w, r, "/api/user/login", http.StatusSeeOther)
}

func addToCart(itemID string) {
	// Fonction factice pour ajouter un article au panier
	// Vous devrez peut-être implémenter votre propre logique ici pour obtenir les détails de l'article à partir de l'ID
	var itemToAdd ShopItem
	// Supposez que playerData soit une variable globale qui stocke les données de l'utilisateur
	for _, item := range playerData.ShopItem {
		if item.ID == itemID {
			itemToAdd = item
			break
		}
	}
	playerData.Panier = append(playerData.Panier, PanierItem{
		ID:        itemToAdd.ID,
		ItemsName: itemToAdd.ItemsName,
		Quantity:  itemToAdd.Quantity,
		Price:     itemToAdd.Price,
		ImagePath: itemToAdd.ImagePath,
		ImageData: itemToAdd.ImageData,
	})
}
