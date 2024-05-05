package Connexion

import (
	"crypto/rand"
	"database/sql"
	"encoding/base64"
	_ "fmt"
	_ "github.com/mattn/go-sqlite3"
	_ "golang.org/x/crypto/bcrypt"
	"html/template"
	"log"
	"net/http"
	"strconv"
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
	Connected     []Connected     `json:"connected"`
}

func GenerateToken() (string, error) {
	token := make([]byte, 32)
	_, err := rand.Read(token)
	if err != nil {
		return "", err
	}
	return base64.StdEncoding.EncodeToString(token), nil
}

type RegisterData struct {
	ErrorMessage string
}

func HandleAdministrator(w http.ResponseWriter, r *http.Request) {
	db, err := sql.Open("sqlite3", "database.sqlite")
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	defer db.Close()

	if r.Method == "POST" {
		adminUsername := r.FormValue("username")
		adminPassword := r.FormValue("password")
		email := r.FormValue("email")

		hashedPassword, err := HashPassword(adminPassword)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		_, err = db.Exec("INSERT INTO utilisateurs (username, password, email, est_admin) VALUES (?, ?, ?, 1)", adminUsername, hashedPassword, email)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
		_, err = db.Exec("UPDATE utilisateurs SET date_inscription = CURRENT_TIMESTAMP WHERE username = ?", adminUsername)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
		http.Redirect(w, r, "/login", http.StatusFound)
		return
	}

	tmpl, err := template.ParseFiles("templates/html/Connexion/admin.html")
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	if err := tmpl.Execute(w, nil); err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
}

func HandleRegister(w http.ResponseWriter, r *http.Request) {
	db, err := sql.Open("sqlite3", "database.sqlite")
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	defer db.Close()
	usersWithCoins := GetAllUsersWithCoins()
	log.Println("Solde des pièces pour chaque utilisateur:", usersWithCoins)
	if r.Method == "POST" {
		username := r.FormValue("username")
		password := r.FormValue("password")
		email := r.FormValue("email")

		if !ValidatePassword(password) {
			http.Error(w, "Le mot de passe ne respecte pas les conditions requises", http.StatusBadRequest)
			return
		}

		exists, err := CheckUsernameExists(db, username)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		if exists {
			data := struct {
				ExistingEmail    bool
				ExistingUsername bool
				ErrorMessage     string
				ErrorEmail       string
			}{
				ExistingEmail:    true,
				ExistingUsername: true,
				ErrorMessage:     "Le nom d'utilisateur est déjà pris",
				ErrorEmail:       "L'email est déjà pris",
			}

			tmpl, err := template.ParseFiles("templates/html/Connexion/register.html")
			if err != nil {
				http.Error(w, err.Error(), http.StatusInternalServerError)
				return
			}

			err = tmpl.Execute(w, data)
			if err != nil {
				http.Error(w, err.Error(), http.StatusInternalServerError)
				return
			}

			return
		}

		emailExists, err := CheckEmailExists(db, email)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		if emailExists {
			data := RegisterData{
				ErrorMessage: "L'email est déjà utilisé",
			}
			tmpl, err := template.ParseFiles("templates/html/Connexion/register.html")
			if err != nil {
				http.Error(w, err.Error(), http.StatusInternalServerError)
				return
			}
			err = tmpl.Execute(w, data)
			if err != nil {
				http.Error(w, err.Error(), http.StatusInternalServerError)
				return
			}
			return
		}

		hashedPassword, err := HashPassword(password)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		token, err := GenerateToken()
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		// Récupération du solde des pièces de l'utilisateur
		coins := 0
		usersWithCoins := GetAllUsersWithCoins()
		if val, ok := usersWithCoins[username]; ok {
			coins = val
		}

		_, err = db.Exec("INSERT INTO utilisateurs (username, password, email, reset_token, coins) VALUES (?, ?, ?, ?, ?)", username, hashedPassword, email, token, coins)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
		_, err = db.Exec("UPDATE utilisateurs SET date_inscription = CURRENT_TIMESTAMP WHERE username = ?", username)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		http.Redirect(w, r, "/login", http.StatusFound)
		return
	}

	tmpl, err := template.ParseFiles("templates/html/Connexion/register.html")
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	if err := tmpl.Execute(w, nil); err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
}
func GetAllUsersWithCoins() map[string]int {
	users := make(map[string]int)

	for _, coin := range playerData.Coins {
		if _, ok := users[coin.PlayerName]; ok {
			users[coin.PlayerName] += convertStringToInt(coin.Coins)
		} else {
			users[coin.PlayerName] = convertStringToInt(coin.Coins)
		}
	}

	for user, coins := range users {
		log.Printf("Utilisateur: %s - Solde des pièces: %d\n", user, coins)
	}

	return users
}

func convertStringToInt(str string) int {
	val, err := strconv.Atoi(str)
	if err != nil {
		return 0
	}
	return val
}
