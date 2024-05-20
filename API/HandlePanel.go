package API

import (
	"database/sql"
	"encoding/json"
	"fmt"
	"html/template"
	"net/http"

	_ "github.com/mattn/go-sqlite3"
)

var players []Crud
var db *sql.DB

type Player struct {
	PlayerID   string `json:"player_id"`
	PlayerName string `json:"player_name"`
}

type PlayerStats struct {
	PlayerID   string `json:"player_id"`
	ID         string `json:"id"`
	Name       string `json:"name"`
	Score      string `json:"score"`
	Kills      string `json:"kills"`
	EntityType string `json:"entity_type"`
	Coins      string `json:"coins"`
}

type PlayerInventory struct {
	PlayerID string `json:"player_id"`
	ItemName string `json:"item_name"`
	Amount   int    `json:"quantity"`
	Position int    `json:"position"`
}

type PlayerPurchase struct {
	PlayerID     string `json:"player_id"`
	ItemName     string `json:"item_name"`
	Price        int    `json:"price"`
	PurchaseDate string `json:"purchase_date"`
}

type PlayerMoved struct {
	PlayerID string `json:"player_id"`
	Blocks   string `json:"blocks_moved"`
}

type PlayerBlock struct {
	PlayerID  string `json:"player_id"`
	BlockName string `json:"block_name"`
	Position  int    `json:"position"`
	NomBlocks string `json:"name_block"`
}

type PlayerAchievements struct {
	PlayerID     string `json:"player_id"`
	Achievements string `json:"achievements"`
	Position     int    `json:"position"`
}

type Coins struct {
	PlayerID string `json:"player_id"`
	Coins    string `json:"coins"`
}

type Crud struct {
	PlayerID   string `json:"player_id"`
	PlayerName string `json:"player_name"`
	Kills      string `json:"kills"`
	EntityType string `json:"entity_type"`
	BlockName  string `json:"block_name"`
	Position   int    `json:"position"` // Modifiez le type de int à *int64
	Position2  int    `json:"position2"`
	Position3  int    `json:"position3"`

	NomBlocks    string `json:"nom_blocks"`
	Price        int    `json:"price"`
	PurchaseDate string `json:"purchase_date"`
	ItemName     string `json:"item_name"`
	Amount       int    `json:"quantity"`

	Player         []Player             `json:"player,omitempty"`
	Stats          []PlayerStats        `json:"-"`
	Inventory      []PlayerInventory    `json:"-"`
	PlayerPurchase []PlayerPurchase     `json:"-"`
	Block          []PlayerBlock        `json:"-"`
	Achievements   []PlayerAchievements `json:"-"`
	Blocks         []PlayerMoved        `json:"-"`
}

func insertPlayerData(player Crud) error {
	db, err := sql.Open("sqlite3", "database.sqlite")
	if err != nil {
		return err
	}
	defer db.Close()

	_, err = db.Exec("INSERT INTO players (player_id, player_name, kills, entity_type, block_name, item_name, purchase_date, price, position) VALUES (?, ?, ?, ?, ?, ?,?,?, ?)",
		player.PlayerID, player.PlayerName, player.Kills, player.EntityType, player.BlockName, player.ItemName, player.PurchaseDate, player.Price, player.Position)

	if err != nil {
		return err
	}

	return nil
}

func FetchPlayersFromDB(db *sql.DB) []Crud {
	rows, err := db.Query("SELECT * FROM players")
	if err != nil {
		fmt.Println("Error fetching players from database:", err)
		return nil
	}
	defer rows.Close()

	var playersFromDB []Crud

	for rows.Next() {
		var player Crud

		err := rows.Scan(
			&player.PlayerID,
			&player.PlayerName,
			&player.Kills,
			&player.EntityType,
			&player.ItemName,
			&player.Position, // Changez ce type à *int64
			&player.Position2,
			&player.Position3,
			&player.Position3,

			&player.Price,
			&player.PurchaseDate,
			&player.BlockName,
			&player.Amount,
		)

		if err != nil {
			fmt.Println("Error scanning player row:", err)
			continue
		}
		playersFromDB = append(playersFromDB, player)
	}

	if err := rows.Err(); err != nil {
		fmt.Println("Error iterating over player rows:", err)
		return nil
	}

	return playersFromDB
}

func usernameExists1(username string) bool {
	// Ouvre la connexion à la base de données
	db, err := sql.Open("sqlite3", "database.sqlite")
	if err != nil {
		fmt.Println("Erreur lors de la connexion à la base de données:", err)
		return false
	}
	defer db.Close()

	var playerID string
	err = db.QueryRow("SELECT player_id FROM players WHERE player_name = ?", username).Scan(&playerID)
	if err != nil {
		if err == sql.ErrNoRows {
			fmt.Println("Pseudo non trouvé dans la base de données")
			return false
		}
		fmt.Println("Erreur lors de la recherche du pseudo dans la base de données:", err)
		return false
	}

	return true
}

// CreatePlayerHandler crée un nouveau joueur
func CreatePlayerHandler(w http.ResponseWriter, r *http.Request) {
	var player Crud
	err := json.NewDecoder(r.Body).Decode(&player)
	if err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}

	players = append(players, player)

	if err := insertPlayerData(player); err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	w.WriteHeader(http.StatusCreated)
}

func GetAllPlayersHandler(w http.ResponseWriter, r *http.Request) {
	fmt.Println("Getting all players...")

	json.NewEncoder(w).Encode(players)
	fmt.Println("Players sent successfully")
}

func UpdatePlayerHandler(w http.ResponseWriter, r *http.Request) {
	fmt.Println("Updating a player...")

	var updatedPlayer Crud
	err := json.NewDecoder(r.Body).Decode(&updatedPlayer)
	if err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		fmt.Println("Error decoding JSON:", err)
		return
	}

	for i, player := range players {
		if player.PlayerID == updatedPlayer.PlayerID {
			players[i] = updatedPlayer
			w.WriteHeader(http.StatusOK)
			fmt.Println("Player updated successfully:", updatedPlayer)
			return
		}
	}

	http.NotFound(w, r)
	fmt.Println("Player not found for update:", updatedPlayer.PlayerID)
}

func DeletePlayerHandler(w http.ResponseWriter, r *http.Request) {
	fmt.Println("Deleting a player...")

	id := r.FormValue("player_id")

	for i, player := range players {
		if player.PlayerID == id {
			players = append(players[:i], players[i+1:]...)
			w.WriteHeader(http.StatusOK)
			fmt.Println("Player deleted successfully:", id)
			return
		}
	}

	http.NotFound(w, r)
	fmt.Println("Player not found for deletion:", id)
}

func HandlePanel(w http.ResponseWriter, r *http.Request) {
	if r.Method != "GET" {
		http.Error(w, "Méthode non autorisée", http.StatusMethodNotAllowed)
		return
	}

	tmpl, err := template.ParseFiles("templates/html/Menu/panel.html")
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	err = tmpl.Execute(w, players)
	if err != nil {
		http.Error(w, "Erreur lors de l'exécution de la template HTML: "+err.Error(), http.StatusInternalServerError)
		return
	}
}

func PlayerStatsHandler(w http.ResponseWriter, r *http.Request) {
	username := r.FormValue("username")

	if !usernameExists1(username) {
		http.Error(w, "Pseudo non trouvé", http.StatusNotFound)
		return
	}
	tmpl, err := template.ParseFiles("templates/html/User/user.html")
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	err = tmpl.Execute(w, players)
	if err != nil {
		http.Error(w, "Erreur lors de l'exécution de la template HTML: "+err.Error(), http.StatusInternalServerError)
		return
	}
}

func UserHandler1(w http.ResponseWriter, r *http.Request) {
	if r.Method != "GET" {
		http.Error(w, "Méthode non autorisée", http.StatusMethodNotAllowed)
		return
	}

	// Récupérer les joueurs depuis la base de données
	playersFromDB := FetchPlayersFromDB(db)

	// Combiner les joueurs de la base de données avec ceux déjà stockés
	allPlayers := append(players, playersFromDB...)

	// Charger le modèle HTML
	tmpl, err := template.ParseFiles("templates/html/User/user.html")
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	// Passer les données des joueurs au modèle HTML
	err = tmpl.Execute(w, allPlayers)
	if err != nil {
		http.Error(w, "Erreur lors de l'exécution de la template HTML: "+err.Error(), http.StatusInternalServerError)
		return
	}
}
