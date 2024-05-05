package API

import (
	"APIMC/Connexion"
	"database/sql"
	"encoding/json"
	"errors"
	"log"
	"net/http"
	"strconv"
)

func HandleBuyItem(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPost {
		http.Error(w, "Méthode non autorisée", http.StatusMethodNotAllowed)
		return
	}

	var requestData struct {
		BuyItemID string `json:"buyItemID"`
	}

	err := json.NewDecoder(r.Body).Decode(&requestData)
	if err != nil {
		http.Error(w, "Erreur lors de la lecture des données de la requête: "+err.Error(), http.StatusBadRequest)
		return
	}

	itemID := requestData.BuyItemID
	if itemID == "" {
		http.Error(w, "ID de l'item manquant", http.StatusBadRequest)
		return
	}

	session, err := Connexion.Store.Get(r, Connexion.SessionName)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	var playerData PlayerData
	if session.Values["playerData"] == nil {
		http.Redirect(w, r, "/api/user/login", http.StatusSeeOther)
		return
	} else {
		playerData = session.Values["playerData"].(PlayerData)
	}

	log.Println("Tentative d'achat de l'itemID:", itemID)

	var shopItem *ShopItem
	for _, item := range playerData.ShopItem {
		if item.ID == itemID {
			shopItem = &item
			break
		}
	}

	if shopItem == nil {
		http.Error(w, "Item non trouvé dans la boutique", http.StatusInternalServerError)
		return
	}

	err = buyItem(itemID, &playerData)
	if err != nil {
		http.Error(w, "Erreur lors de l'achat de l'item: "+err.Error(), http.StatusInternalServerError)
		return
	}

	err = session.Save(r, w)
	if err != nil {
		http.Error(w, "Erreur lors de la sauvegarde de la session: "+err.Error(), http.StatusInternalServerError)
		return
	}

	err = recordPurchase(itemID, shopItem, playerData.Username, playerData.Connected)
	if err != nil {
		http.Error(w, "Erreur lors de l'enregistrement de l'achat: "+err.Error(), http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	w.Write([]byte(`{"message": "Achat réussi."}`))
}

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
		http.Redirect(w, r, "/api/user/login", http.StatusSeeOther)
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

func recordPurchase(itemID string, shopItem *ShopItem, username string, connected []Connected) error {
	db, err := sql.Open("sqlite3", "database.sqlite")
	if err != nil {
		return err
	}
	defer db.Close()

	if shopItem.ID == "" {
		return errors.New("Item non trouvé dans la boutique")
	}

	isConnected := len(connected) > 0

	log.Println("Enregistrement de l'achat de l'itemID:", itemID)

	_, err = db.Exec("INSERT INTO achats (item_id, player_name, item_name, price, connected) VALUES (?, ?, ?, ?, ?)",
		itemID, username, shopItem.ItemsName, shopItem.Price, isConnected)
	if err != nil {
		return err
	}
	_, err = db.Exec("UPDATE utilisateurs SET coins = coins - ? WHERE username = ?", shopItem.Price, username)
	if err != nil {
		return err
	}
	return nil
}

func buyItem(itemID string, playerData *PlayerData) error {
	var shopItem *ShopItem
	var tempShopItem ShopItem
	for _, item := range playerData.ShopItem {
		if item.ID == itemID {
			tempShopItem = item
			shopItem = &tempShopItem
			break
		}
	}

	if shopItem == nil {
		return errors.New("Item non trouvé dans la boutique")
	}

	itemPrice, err := strconv.Atoi(shopItem.Price)
	if err != nil {
		return err
	}

	playerCoins, err := getPlayerCoins(playerData.Username)
	if err != nil {
		return err
	}

	if playerCoins < itemPrice {
		return errors.New("solde de coins insuffisant pour acheter l'item")
	}

	if playerCoins-itemPrice < 0 {
		return errors.New("achat impossible, le solde sera négatif")
	}

	addToCartBuy(shopItem, playerData)

	err = decreasePlayerCoins(itemPrice, playerData)
	if err != nil {
		return err
	}

	err = recordPurchase(itemID, shopItem, playerData.Username, playerData.Connected)
	if err != nil {
		return err
	}

	log.Println("Achat de l'itemID:", itemID)

	return nil
}

func addToCartBuy(item *ShopItem, playerData *PlayerData) {
	playerData.Panier = append(playerData.Panier, PanierItem{
		ID:        item.ID,
		ItemsName: item.ItemsName,
		Quantity:  item.Quantity,
		Price:     item.Price,
		ImagePath: item.ImagePath,
		ImageData: item.ImageData,
	})
}

func getPlayerCoins(username string) (int, error) {
	db, err := sql.Open("sqlite3", "database.sqlite")
	if err != nil {
		return 0, err
	}
	defer db.Close()

	var userCoins int
	err = db.QueryRow("SELECT coins FROM utilisateurs WHERE username = ?", username).Scan(&userCoins)
	if err != nil {
		return 0, err
	}

	return userCoins, nil
}

func decreasePlayerCoins(amount int, playerData *PlayerData) error {
	if len(playerData.Coins) == 0 {
		return errors.New("Aucune donnée de coins disponible")
	}

	currentCoins, err := strconv.Atoi(playerData.Coins[0].Coins)
	if err != nil {
		return err
	}

	if currentCoins < amount {
		return errors.New("Solde de coins insuffisant")
	}

	newCoins := currentCoins - amount

	if newCoins < 0 {
		return errors.New("La déduction entraînera un solde négatif")
	}

	playerData.Coins[0].Coins = strconv.Itoa(newCoins)

	return nil
}
