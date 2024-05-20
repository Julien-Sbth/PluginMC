package API

import (
	"APIMC/Connexion"
	"fmt"
	"net/http"
	"strconv"
)

func CanBuyItem(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodGet {
		http.Error(w, "Méthode non autorisée", http.StatusMethodNotAllowed)
		return
	}

	itemID := r.URL.Query().Get("buyItemID")
	if itemID == "" {
		http.Error(w, "ID de l'item manquant", http.StatusBadRequest)
		return
	}

	itemPriceStr := r.URL.Query().Get("itemPrice")
	if itemPriceStr == "" {
		http.Error(w, "Prix de l'item manquant", http.StatusBadRequest)
		return
	}

	itemPrice, err := strconv.Atoi(itemPriceStr)
	if err != nil {
		http.Error(w, "Prix de l'item invalide", http.StatusBadRequest)
		return
	}

	session, err := Connexion.Store.Get(r, Connexion.SessionName)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	var username string
	if session.Values["username"] == nil {
		http.Redirect(w, r, "/login", http.StatusSeeOther)
		return
	} else {
		username = session.Values["username"].(string)
	}

	playerCoins, err := getPlayerCoins(username)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	var shopItem *ShopItem
	for _, item := range playerData.ShopItem {
		if item.ID == itemID {
			shopItem = &item
			break
		}
	}

	if shopItem == nil {
		http.Error(w, "Item non trouvé dans la boutique", http.StatusNotFound)
		return
	}
	price, err := strconv.Atoi(shopItem.Price)
	if err != nil {
		http.Error(w, "Erreur de conversion du prix: "+err.Error(), http.StatusInternalServerError)
		return
	}

	fmt.Println("Price:", price)

	err = sendHTTPRequest(itemID, username, price)
	if err != nil {
		fmt.Println("Erreur lors de l'appel à SendRequest:", err)
		http.Error(w, "Erreur lors de l'appel à SendRequest: "+err.Error(), http.StatusInternalServerError)
		return
	}

	fmt.Println("SendRequest appelé avec succès")
	err = recordPurchase(itemID, shopItem, username, playerData.Connected)
	if err != nil {
		http.Error(w, "Erreur lors de l'enregistrement de l'achat: "+err.Error(), http.StatusInternalServerError)
		return
	}

	canBuy := playerCoins >= itemPrice

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	if canBuy {
		w.Write([]byte(`{"canBuy": true}`))
	} else {
		w.Write([]byte(`{"canBuy": false}`))
	}
}
