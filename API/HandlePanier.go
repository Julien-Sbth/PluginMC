package API

import (
	"APIMC/Connexion"
	"database/sql"
	"fmt"
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

	session, err := Connexion.Store.Get(r, Connexion.SessionName)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	var username string
	if session.Values["username"] == nil {
		http.Redirect(w, r, "/api/user/login", http.StatusSeeOther)
		return
	} else {
		username = session.Values["username"].(string)
	}

	addToCart(itemID, username)

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

		tmpl, err := template.ParseFiles("templates/html/Menu/panier.html")
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
		var coins []Coin
		var kills []Kill
		var item []Item
		var connect []Connected
		coinsFound := false

		if usernameExists(username) {
			for _, c := range playerData.Coins {
				if c.PlayerName == username {
					coins = append(coins, c)
					coinsFound = true
				}
			}
			for _, c := range playerData.Connected {
				if c.Name == username {
					connect = append(connect, c)
					coinsFound = true
				}
			}
			for _, k := range playerData.Kills {
				if k.PlayerID == username {
					kills = append(kills, k)
				}
			}
			for _, i := range playerData.Items {
				if i.PlayerID == username {
					item = append(item, i)
				}
			}
			for _, c := range playerData.Connected {
				if c.UUID == username {
					connect = append(connect, c)
				}
			}
		}

		if !coinsFound {
			coins = append(coins, Coin{PlayerID: username, Coins: "0"})
		}
		playerData := PlayerData{
			Username:     username,
			IsLoggedIn:   true,
			IsAdmin:      isAdmin,
			Panier:       playerData.Panier,
			ErrorMessage: "Joueur Inconnu, veuillez en choisir un autre",
			Coins:        coins,
			Connected:    connect,
		}

		err = tmpl.Execute(w, playerData)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
		return
	}

	http.Redirect(w, r, "/api/user/login", http.StatusSeeOther)
}

func addToCart(itemID string, username string) {
	db, err := sql.Open("sqlite3", "database.sqlite")
	if err != nil {
		log.Fatal(err)
	}
	defer db.Close()

	var itemToAdd ShopItem
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

	_, err = db.Exec("INSERT INTO panier (item_name, quantity, price, image_path) VALUES (?, ?, ?, ?)",
		itemToAdd.ItemsName, itemToAdd.Quantity, itemToAdd.Price, itemToAdd.ImagePath)
	if err != nil {
		log.Fatal(err)
	}

	var userCoins int
	err = db.QueryRow("SELECT coins FROM utilisateurs WHERE username = ?", username).Scan(&userCoins)
	if err != nil {
		log.Fatal(err)
	}

	fmt.Printf("Coins de l'utilisateur %s : %d\n", username, userCoins)
}
