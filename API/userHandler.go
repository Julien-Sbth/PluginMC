package API

import (
	"html/template"
	"net/http"
)

var playerData PlayerData

type PlayerData struct {
	PlayerID      string          `json:"player_id"`
	PlayerName    string          `json:"player_name"`
	EntityType    string          `json:"entity_type"`
	Coins         []Coin          `json:"coins"`
	Items         []Item          `json:"items"`
	Kills         []Kill          `json:"kills"`
	Blocks        []Block         `json:"blocks"`
	Achievements  []Achievements  `json:"achievements"`
	BlocksDestroy []BlocksDestroy `json:"block_name"`
	Inventory     []Inventory     `json:"nom_item"`
	Shop          []Shop          `json:"shop"`
	ShopItem      []ShopItem      `json:"shop_item"`
	Panier        []PanierItem
	Username      string
	IsLoggedIn    bool
	IsAdmin       bool
}

// Structure pour les éléments dans le panier
type PanierItem struct {
	ID        string `json:"id"`
	ItemsName string `json:"item_name"`
	Quantity  string `json:"quantity"`
	Price     string `json:"price"`
	ImagePath string `json:"image_base64"`
	ImageData string
}
type Coin struct {
	PlayerID string `json:"player_id"`
	Coins    string `json:"coins"`
}
type ShopItem struct {
	ID        string `json:"id"`
	ItemsName string `json:"item_name"`
	Quantity  string `json:"quantity"`
	Price     string `json:"price"`
	ImagePath string `json:"image_base64"`
	ImageData string
}
type Item struct {
	PlayerUUID string `json:"player_uuid"`
	ItemData   string `json:"item_data"`
	Amount     string `json:"amount"`
	PlayerID   string
}

type Kill struct {
	PlayerID   string `json:"player_id"`
	EntityType string `json:"entity_type"`
	Kills      string `json:"kills"`
	ImagePath  string `json:"kills_monster"`
	PlayerName string `json:"player_name"`
	ImageData  string
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
	ShopItem      []ShopItem      `json:"shop_item"`
}

func usernameExists(username string) bool {
	if playerData.PlayerName == username {
		return true
	}

	for _, coin := range playerData.Coins {
		if coin.PlayerID == username {
			return true
		}
	}
	for _, item := range playerData.Items {
		if item.PlayerUUID == username {
			return true
		}
	}
	for _, kill := range playerData.Kills {
		if kill.PlayerID == username {
			return true
		}
	}
	for _, block := range playerData.Blocks {
		if block.PlayerID == username {
			return true
		}
	}
	for _, achievement := range playerData.Achievements {
		if achievement.PlayerID == username {
			return true
		}
	}
	for _, blocksDestroy := range playerData.BlocksDestroy {
		if blocksDestroy.PlayerID == username {
			return true
		}
	}
	for _, inventory := range playerData.Inventory {
		if inventory.PlayerID == username {
			return true
		}
	}
	for _, shop := range playerData.Shop {
		if shop.PlayerID == username {
			return true
		}
	}

	return false
}

func PlayerInfoHandler(w http.ResponseWriter, r *http.Request) {
	username := r.FormValue("username")

	if !usernameExists(username) {
		http.Error(w, "Pseudo non trouvé", http.StatusNotFound)
		return
	}
	tmpl, err := template.ParseFiles("templates/html/User/user.html")
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
func UserHandler(w http.ResponseWriter, r *http.Request) {
	if r.Method != "GET" {
		http.Error(w, "Méthode non autorisée", http.StatusMethodNotAllowed)
		return
	}

	tmpl, err := template.ParseFiles("templates/html/User/user.html")
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
