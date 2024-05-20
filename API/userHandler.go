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
	Connected     []Connected     `json:"connected"`
	Panier        []PanierItem
	Username      string
	Connect       string
	IsLoggedIn    bool
	IsAdmin       bool
	ErrorMessage  string
}

type PanierItem struct {
	ID           string `json:"id"`
	ItemsName    string `json:"item_name"`
	Quantity     string `json:"quantity"`
	Price        string `json:"price"`
	ImagePath    string `json:"image_base64"`
	ImageData    string
	Username     string
	IsLoggedIn   bool
	IsAdmin      bool
	ErrorMessage string
}

type Coin struct {
	PlayerID   string `json:"player_id"`
	PlayerName string `json:"player_name"`
	Coins      string `json:"coins"`
}

type ShopItem struct {
	ID        string `json:"id"`
	ItemsName string `json:"item_name"`
	Quantity  string `json:"quantity"`
	Price     string `json:"price"`
	ImagePath string `json:"image_base64"`
	ImageData string
}

type Connected struct {
	UUID    string `json:"uuid"`
	Name    string `json:"name"`
	Connect string `json:"status"`
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
	PlayerName string `json:"player_name"`
	PlayerID   string `json:"player_id"`
	Blocks     string `json:"blocks_moved"`
}

type Achievements struct {
	PlayerID     string `json:"player_id"`
	Achievements string `json:"achievements"`
}

type Shop struct {
	ID           string `json:"id"`
	PlayerID     string `json:"player_id"`
	PlayerName   string `json:"player_name"`
	ItemName     string `json:"item_name"`
	Amount       string `json:"quantity"`
	Price        string `json:"price"`
	PurchaseDate string `json:"purchase_date"`
}

type BlocksDestroy struct {
	PlayerName string `json:"player_name"`

	BlockName string `json:"block_name"`
	NomBlocks string `json:"nom_block"`
	Amount    string `json:"amount"`
	ImagePath string `json:"block_image"`
	ImageData string
}

type Inventory struct {
	ID         string `json:"id"`
	PlayerID   string `json:"player_id"`
	PlayerName string `json:"player_name"`
	NomItem    string `json:"nom_item"`
	Amount     string `json:"quantite"`
	Position   string `json:"position"`
	ImagePath  string `json:"image_base64"`
	ImageData  string
	PlayerUUID string
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
	Connected     []Connected     `json:"connected"`
}

func usernameExists(username string) bool {
	for _, coin := range playerData.Coins {
		if coin.PlayerID == username || coin.PlayerName == username {
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
		if blocksDestroy.PlayerName == username {
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

func findPlayerDataByUsername(username string) *ResponseData {
	var responseData ResponseData

	for _, coins := range playerData.Coins {
		if coins.PlayerName == username {
			responseData.Coins = append(responseData.Coins, coins)
		}
	}
	for _, item := range playerData.Items {
		if item.PlayerUUID == username {
			responseData.Items = append(responseData.Items, item)
		}
	}

	for _, kill := range playerData.Kills {
		if kill.PlayerName == username {
			responseData.Kills = append(responseData.Kills, kill)
		}
	}
	for _, block := range playerData.Blocks {
		if block.PlayerID == username {
			responseData.Blocks = append(responseData.Blocks, block)
		}
	}
	for _, achievement := range playerData.Achievements {
		if achievement.PlayerID == username {
			responseData.Achievements = append(responseData.Achievements, achievement)
		}
	}
	for _, blocksDestroy := range playerData.BlocksDestroy {
		if blocksDestroy.PlayerName == username {
			responseData.BlocksDestroy = append(responseData.BlocksDestroy, blocksDestroy)
		}
	}
	for _, inventory := range playerData.Inventory {
		if inventory.PlayerName == username {
			responseData.Inventory = append(responseData.Inventory, inventory)
		}
	}
	for _, movement := range playerData.Blocks {
		if movement.PlayerName == username {
			responseData.Blocks = append(responseData.Blocks, movement)
		}
	}
	for _, shop := range playerData.Shop {
		if shop.PlayerName == username {
			responseData.Shop = append(responseData.Shop, shop)
		}
	}

	return &responseData
}

func PlayerInfoHandler(w http.ResponseWriter, r *http.Request) {
	username := r.FormValue("username")

	responseData := findPlayerDataByUsername(username)
	if responseData == nil {
		http.Error(w, "Pseudo non trouvé", http.StatusNotFound)
		return
	}
	if !usernameExists(username) {
		http.Error(w, "Pseudo non trouvé", http.StatusNotFound)
		return
	}
	tmpl, err := template.ParseFiles("templates/html/User/user.html")
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	err = tmpl.Execute(w, responseData)
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
