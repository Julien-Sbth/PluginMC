package Menu

import (
	"encoding/json"
	"fmt"
	"html/template"
	"net/http"
	"strconv"
)

type PlayerData struct {
	PlayerID     string `json:"player_id"`
	PlayerName   string `json:"player_name"`
	ID           int    `json:"id"`
	Name         string `json:"name"`
	Score        int    `json:"score"`
	Coins        int    `json:"coins"`
	Kills        int    `json:"kills"`
	EntityType   string `json:"entity_type"`
	Blocks       string `json:"blocks"`
	Achievements int    `json:"achievements"`
	ItemName     string `json:"item_name"`
	Amount       int    `json:"quantity"`
	Price        int    `json:"price"`
	PurchaseDate string `json:"purchase_date"`
	BlockName    string `json:"block_name"`
	NomBlocks    string `json:"nom_blocks"`
	Position     int    `json:"position"`
	NomItem      string `json:"nom_item"`
}

var players []PlayerData

// CreatePlayerHandler crée un nouveau joueur
func CreatePlayerHandler(w http.ResponseWriter, r *http.Request) {
	// Lecture des données du corps de la requête
	var player PlayerData
	err := json.NewDecoder(r.Body).Decode(&player)
	if err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}

	// Ajout du joueur à la liste
	players = append(players, player)

	// Réponse de succès
	w.WriteHeader(http.StatusCreated)
}

// GetAllPlayersHandler récupère tous les joueurs
func GetAllPlayersHandler(w http.ResponseWriter, r *http.Request) {
	fmt.Println("Getting all players...")

	// Conversion de la slice de joueurs en JSON et envoi en réponse
	json.NewEncoder(w).Encode(players)
	fmt.Println("Players sent successfully")
}

// UpdatePlayerHandler met à jour les données d'un joueur
func UpdatePlayerHandler(w http.ResponseWriter, r *http.Request) {
	fmt.Println("Updating a player...")

	var updatedPlayer PlayerData
	err := json.NewDecoder(r.Body).Decode(&updatedPlayer)
	if err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		fmt.Println("Error decoding JSON:", err)
		return
	}

	// Recherche du joueur par ID
	for i, player := range players {
		if player.ID == updatedPlayer.ID {
			// Mise à jour des données du joueur
			players[i] = updatedPlayer
			w.WriteHeader(http.StatusOK)
			fmt.Println("Player updated successfully:", updatedPlayer)
			return
		}
	}

	// Si le joueur n'est pas trouvé
	http.NotFound(w, r)
	fmt.Println("Player not found for update:", updatedPlayer.ID)
}

// DeletePlayerHandler supprime un joueur de la liste
func DeletePlayerHandler(w http.ResponseWriter, r *http.Request) {
	fmt.Println("Deleting a player...")

	// Lecture de l'ID du joueur à supprimer
	idStr := r.FormValue("id")
	id, err := strconv.Atoi(idStr)
	if err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		fmt.Println("Error parsing player ID:", err)
		return
	}

	// Recherche du joueur par ID
	for i, player := range players {
		if player.ID == id {
			// Suppression du joueur de la liste
			players = append(players[:i], players[i+1:]...)
			w.WriteHeader(http.StatusOK)
			fmt.Println("Player deleted successfully:", id)
			return
		}
	}

	// Si le joueur n'est pas trouvé
	http.NotFound(w, r)
	fmt.Println("Player not found for deletion:", id)
}

// HandlePanel affiche l'interface utilisateur du panneau d'administration
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
