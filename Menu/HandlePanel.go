package Menu

import (
	"database/sql"
	"encoding/json"
	"fmt"
	"html/template"
	"net/http"
	"strconv"

	_ "github.com/mattn/go-sqlite3"
)

var players []PlayerData

type Player struct {
	PlayerID   string `json:"player_id"`
	PlayerName string `json:"player_name"`
}

type PlayerStats struct {
	PlayerID   string `json:"player_id"`
	ID         int    `json:"id"`
	Name       string `json:"name"`
	Score      int    `json:"score"`
	Kills      string `json:"kills"`
	EntityType string `json:"entity_type"`
	Coins      any
}

type PlayerInventory struct {
	PlayerID string  `json:"player_id"`
	ItemName *string `json:"item_name"`
	Amount   int     `json:"quantity"`
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

type PlayerCoins struct {
	PlayerID string `json:"player_id"`
	Coins    string `json:"coins"`
}

type PlayerData struct {
	Player       Player
	Stats        PlayerStats
	Inventory    PlayerInventory
	Purchase     PlayerPurchase
	Block        PlayerBlock
	Achievements PlayerAchievements
	Blocks       PlayerMoved
	Coins        PlayerCoins
}

var db *sql.DB

func FetchPlayersFromDB() []PlayerData {
	rows, err := db.Query("SELECT * FROM players")
	if err != nil {
		fmt.Println("Error fetching players from database:", err)
		return nil
	}
	defer rows.Close()

	var playersFromDB []PlayerData

	for rows.Next() {
		var player PlayerData
		err := rows.Scan(
			&player.Player.PlayerID,
			&player.Player.PlayerName,
			&player.Stats.ID,
			&player.Stats.Name,
			&player.Stats.Score,
			&player.Stats.Coins,
			&player.Stats.Kills,
			&player.Stats.EntityType,
			&player.Achievements.Achievements,
			&player.Inventory.Amount,
			&player.Purchase.ItemName,
			&player.Purchase.Price,
			&player.Purchase.PurchaseDate,
			&player.Block.BlockName,
			&player.Block.NomBlocks,
			&player.Block.Position,
		)

		if err != nil {
			fmt.Println("Error scanning player row:", err)
			continue
		}
		playersFromDB = append(playersFromDB, player)
	}

	// Vérifie s'il y a des erreurs pendant le parcours des lignes
	if err := rows.Err(); err != nil {
		fmt.Println("Error iterating over player rows:", err)
		return nil
	}

	return playersFromDB
}

func init() {
	var err error
	db, err = sql.Open("sqlite3", "./players.db")
	if err != nil {
		panic(err)
	}

	createTableSQL := `
	CREATE TABLE IF NOT EXISTS players (
		id INTEGER PRIMARY KEY AUTOINCREMENT,
		player_id TEXT,
		player_name TEXT,
		name TEXT,
		score INTEGER,
		coins INTEGER,
		kills INTEGER,
		entityType TEXT,
		blocks TEXT,
		achievements INTEGER,
		itemName TEXT,
		quantity INTEGER,
		price INTEGER,
		purchaseDate TEXT,
		blockName TEXT,
		position INTEGER,
		nomItem TEXT
	);
	`
	if _, err := db.Exec(createTableSQL); err != nil {
		panic(err)
	}
}

func usernameExists(username string) bool {
	for _, player := range players {
		if player.Player.PlayerName == username {
			return true
		}

		if player.Inventory.PlayerID == username {
			return true
		}

		if player.Stats.PlayerID == username {
			return true
		}

		if player.Achievements.PlayerID == username {
			return true
		}

		// Autres vérifications pour les autres attributs de player...

	}

	return false
}

func CreatePlayerHandler(w http.ResponseWriter, r *http.Request) {
	var player PlayerData
	err := json.NewDecoder(r.Body).Decode(&player)
	if err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}

	// Ajouter le joueur à la base de données
	if err := insertPlayer(player); err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	// Renvoyer une réponse indiquant que le joueur a été créé avec succès
	w.WriteHeader(http.StatusCreated)
	fmt.Fprintf(w, "Joueur créé avec succès")
}

func GetAllPlayersHandler(w http.ResponseWriter, r *http.Request) {
	fmt.Println("Getting all players from database...")

	players = FetchPlayersFromDB()

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
		if player.Stats.ID == updatedPlayer.Stats.ID {
			players[i] = updatedPlayer
			w.WriteHeader(http.StatusOK)
			fmt.Println("Player updated successfully:", updatedPlayer.Stats.ID)
			return
		}
	}

	http.NotFound(w, r)
	fmt.Println("Player not found for update:", updatedPlayer.Stats.ID)
}

func DeletePlayerHandler(w http.ResponseWriter, r *http.Request) {
	fmt.Println("Deleting a player...")

	idStr := r.FormValue("id")
	id, err := strconv.Atoi(idStr)
	if err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		fmt.Println("Error parsing player ID:", err)
		return
	}

	for i, player := range players {
		if player.Stats.ID == id {
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
	tmpl, err := template.ParseFiles("templates/html/Menu/panel.html")
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	if err := tmpl.Execute(w, players); err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
	}
}

func Lala(w http.ResponseWriter, r *http.Request) {
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

func insertPlayer(player PlayerData) error {
	insertSQL := `
	INSERT INTO players (player_id, player_name, id, name, score, kills, entityType, quantity, price, purchaseDate, blockName, position, achievements, coins)
	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
	`
	_, err := db.Exec(insertSQL, player.Player.PlayerID, player.Player.PlayerName, player.Stats.ID, player.Stats.Name, player.Stats.Score, player.Stats.Kills, player.Stats.EntityType, player.Inventory.Amount, player.Purchase.ItemName, player.Purchase.Price, player.Purchase.PurchaseDate, player.Block.BlockName, player.Block.Position, player.Achievements.Achievements, player.Coins.Coins)
	if err != nil {
		return err
	}
	return nil
}
