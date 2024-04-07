package Menu

import (
	"html/template"
	"net/http"
)

var playerData PlayerData

type PlayerData struct {
	PlayerID      string          `json:"player_id"`
	EntityType    string          `json:"entity_type"`
	Coins         []Coin          `json:"coins"`
	Items         []Item          `json:"items"`
	Kills         []Kill          `json:"kills"`
	Blocks        []Block         `json:"blocks"`
	Achievements  []Achievements  `json:"achievements"`
	BlocksDestroy []BlocksDestroy `json:"block_name"`
	Inventory     []Inventory     `json:"nom_item"`
	Shop          []Shop          `json:"shop"`
}

type Coin struct {
	PlayerID string `json:"player_id"`
	Coins    string `json:"coins"`
}

type Item struct {
	PlayerUUID string `json:"player_uuid"`
	ItemData   string `json:"item_data"`
	Amount     string `json:"amount"`
}

type Kill struct {
	PlayerID   string `json:"player_id"`
	EntityType string `json:"entity_type"`
	Kills      string `json:"kills"`
}

type Block struct {
	PlayerID string `json:"player_id"`
	Blocks   string `json:"blocks_moved"`
}

type Achievements struct {
	PlayerID     string `json:"player_id"`
	Achievements string `json:"achievements"`
}

type Shop struct {
	ID           string `json:"id"`
	PlayerID     string `json:"player_id"`
	ItemName     string `json:"item_name"`
	Amount       string `json:"quantity"`
	Price        string `json:"price"`
	PurchaseDate string `json:"purchase_date"`
}

type BlocksDestroy struct {
	PlayerID  string `json:"player_name"`
	BlockName string `json:"block_name"`
	NomBlocks string `json:"nom_block"`
	Amount    string `json:"amount"`
	ImagePath string `json:"block_image"`
	ImageData string
}

type Inventory struct {
	ID        string `json:"id"`
	PlayerID  string `json:"player_id"`
	NomItem   string `json:"nom_item"`
	Amount    string `json:"quantite"`
	Position  string `json:"position"`
	ImagePath string `json:"image_base64"`
	ImageData string
}

type ResponseData struct {
	PlayerID      string          `json:"player_id"`
	EntityType    string          `json:"entity_type"`
	Coins         []Coin          `json:"coins"`
	Items         []Item          `json:"items"`
	Kills         []Kill          `json:"kills"`
	Blocks        []Block         `json:"blocks"`
	Achievements  []Achievements  `json:"achievements"`
	BlocksDestroy []BlocksDestroy `json:"block_name"`
	Inventory     []Inventory     `json:"nom_item"`
	Shop          []Shop          `json:"shop"`
}

func UserHandler(w http.ResponseWriter, r *http.Request) {
	if r.Method != "GET" {
		http.Error(w, "Méthode non autorisée", http.StatusMethodNotAllowed)
		return
	}

	tmpl, err := template.ParseFiles("templates/html/user.html")
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	err = tmpl.Execute(w, playerData)
	if err != nil {
		http.Error(w, "Erreur lors de l'exécution de la template HTML: "+err.Error(), http.StatusInternalServerError)
		return
	}
}
