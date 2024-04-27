package API

import (
	"database/sql"
	"encoding/json"
	"fmt"
	_ "github.com/mattn/go-sqlite3"
	"html/template"
	"net/http"
	"strconv"
)

var players []PlayerData

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
			&player.PlayerID,
			&player.PlayerName,
			&player.ID,
			&player.Name,
			&player.Score,
			&player.Coins,
			&player.Kills,
			&player.EntityType,
			&player.Blocks,
			&player.Achievements,
			&player.ItemName,
			&player.Amount,
			&player.Price,
			&player.PurchaseDate,
			&player.BlockName,
			&player.Position,
			&player.NomItem,
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

func CreatePlayerHandler(w http.ResponseWriter, r *http.Request) {
	var player PlayerData
	err := json.NewDecoder(r.Body).Decode(&player)
	if err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}
	players = append(players, player)

	if err := insertPlayer(player); err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	w.WriteHeader(http.StatusCreated)
}
func GetAllPlayersHandler(w http.ResponseWriter, r *http.Request) {
	fmt.Println("Getting all players from database...")

	FetchPlayersFromDB()

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
		if player.ID == updatedPlayer.ID {
			players[i] = updatedPlayer
			w.WriteHeader(http.StatusOK)
			fmt.Println("Player updated successfully:", updatedPlayer)
			return
		}
	}

	http.NotFound(w, r)
	fmt.Println("Player not found for update:", updatedPlayer.ID)
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
		if player.ID == id {
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
func insertPlayer(player PlayerData) error {
	insertSQL := `
	INSERT INTO players (player_id, player_name, name, score, coins, kills, entityType, blocks, achievements, itemName, quantity, price, purchaseDate, blockName, position, nomItem)
	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
	`
	_, err := db.Exec(insertSQL, player.PlayerID, player.PlayerName, player.Name, player.Score, player.Coins, player.Kills, player.EntityType, player.Blocks, player.Achievements, player.ItemName, player.Amount, player.Price, player.PurchaseDate, player.BlockName, player.Position, player.NomItem)
	if err != nil {
		return err
	}
	return nil
}
