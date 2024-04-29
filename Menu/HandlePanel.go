package Menu

import (
	"database/sql"
	"encoding/json"
	"fmt"
	"html/template"
	"net/http"

	_ "github.com/mattn/go-sqlite3"
)

var players []PlayerData
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
	Achievements int    `json:"achievements"`
}

type Coins struct {
	PlayerID string `json:"player_id"`
	Coins    string `json:"coins"`
}

type PlayerData struct {
	PlayerID     string `json:"player_id"`
	PlayerName   string `json:"player_name"`
	Kills        string `json:"kills"`
	EntityType   string `json:"entity_type"`
	BlockName    string `json:"block_name"`
	Position     int    `json:"position"`
	NomBlocks    string `json:"nom_blocks"`
	Price        int    `json:"price"`
	PurchaseDate string `json:"purchase_date"`
	ItemName     string `json:"item_name"`
	Amount       int    `json:"quantity"`

	Player       []Player             `json:"-"`
	Stats        []PlayerStats        `json:"-"`
	Inventory    []PlayerInventory    `json:"-"`
	Purchase     []PlayerPurchase     `json:"-"`
	Block        []PlayerBlock        `json:"-"`
	Achievements []PlayerAchievements `json:"-"`
	Blocks       []PlayerMoved        `json:"-"`
	Coins        []Coins              `json:"-"`
}

func insertPlayerData(player PlayerData) error {
	db, err := sql.Open("sqlite3", "players.db")
	if err != nil {
		return err
	}
	defer db.Close()

	_, err = db.Exec("INSERT INTO players (player_id, player_name, kills, entity_type, name_block, block_name, item_name, purchase_date, price, position) VALUES (?, ?, ?, ?, ?, ?,?,?, ?, ?)",
		player.PlayerID, player.PlayerName, player.Kills, player.EntityType, player.NomBlocks, player.BlockName, player.ItemName, player.PurchaseDate, player.Price, player.Position)

	if err != nil {
		return err
	}

	return nil
}

func FetchPlayersFromDB(db *sql.DB) []PlayerData {
	rows, err := db.Query("SELECT * FROM players")
	if err != nil {
		fmt.Println("Error fetching players from database:", err)
		return nil
	}
	defer rows.Close()

	var playersFromDB []PlayerData

	for rows.Next() {
		var player PlayerData
		var p Player
		var k PlayerStats
		var inv PlayerInventory
		var purchase PlayerPurchase
		var block PlayerBlock
		var achievement PlayerAchievements
		var coins Coins

		err := rows.Scan(
			&player.PlayerID,
			&player.PlayerName,
			&player.Kills,
			&player.EntityType,
			&player.ItemName,
			&player.Position,
			&p.PlayerID,
			&p.PlayerName,
			&k.ID,
			&k.Name,
			&k.Score,
			&k.Kills,
			&k.EntityType,
			&k.Coins,
			&inv.PlayerID,
			&inv.ItemName,
			&inv.Amount,
			&inv.Position,
			&purchase.PlayerID,
			&purchase.ItemName,
			&purchase.Price,
			&purchase.PurchaseDate,
			&block.PlayerID,
			&block.BlockName,
			&block.Position,
			&block.NomBlocks,
			&achievement.PlayerID,
			&achievement.Achievements,
			&coins.PlayerID,
			&coins.Coins,
		)

		if err != nil {
			fmt.Println("Error scanning player row:", err)
			continue
		}
		player.Player = append(player.Player, p)
		player.Stats = append(player.Stats, k)
		player.Inventory = append(player.Inventory, inv)
		player.Purchase = append(player.Purchase, purchase)
		player.Block = append(player.Block, block)
		player.Achievements = append(player.Achievements, achievement)
		player.Coins = append(player.Coins, coins)
		playersFromDB = append(playersFromDB, player)
	}

	if err := rows.Err(); err != nil {
		fmt.Println("Error iterating over player rows:", err)
		return nil
	}

	return playersFromDB
}

func usernameExists(username string) bool {
	// Ouvre la connexion à la base de données
	db, err := sql.Open("sqlite3", "players.db")
	if err != nil {
		fmt.Println("Erreur lors de la connexion à la base de données:", err)
		return false
	}
	defer db.Close()

	// Exécute une requête pour vérifier si le pseudo existe dans la base de données
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

	// Le pseudo existe dans la base de données
	return true
}

// CreatePlayerHandler crée un nouveau joueur
func CreatePlayerHandler(w http.ResponseWriter, r *http.Request) {
	var player PlayerData
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

	var updatedPlayer PlayerData
	err := json.NewDecoder(r.Body).Decode(&updatedPlayer)
	if err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		fmt.Println("Error decoding JSON:", err)
		return
	}

	for i, player := range players {
		if player.Player[0].PlayerID == updatedPlayer.Player[0].PlayerID {
			players[i] = updatedPlayer
			w.WriteHeader(http.StatusOK)
			fmt.Println("Player updated successfully:", updatedPlayer)
			return
		}
	}

	http.NotFound(w, r)
	fmt.Println("Player not found for update:", updatedPlayer.Player[0].PlayerID)
}

func DeletePlayerHandler(w http.ResponseWriter, r *http.Request) {
	fmt.Println("Deleting a player...")

	id := r.FormValue("player_id")

	for i, player := range players {
		if player.Player[0].PlayerID == id {
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

	if !usernameExists(username) {
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

func UserHandler(w http.ResponseWriter, r *http.Request) {
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
