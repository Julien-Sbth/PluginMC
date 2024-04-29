package API

import (
	"APIMC/Menu"
	"database/sql"
	"encoding/json"
	"net/http"
)

func ApiHandler(w http.ResponseWriter, r *http.Request) {
	if r.Method != "GET" && r.Method != "POST" {
		http.Error(w, "Méthode non autorisée", http.StatusMethodNotAllowed)
		return
	}

	db, err := sql.Open("sqlite3", "players.db")
	if err != nil {
		http.Error(w, "Erreur lors de la connexion à la base de données: "+err.Error(), http.StatusInternalServerError)
		return
	}
	defer db.Close()

	playersFromDB := Menu.FetchPlayersFromDB(db)

	if r.Method == "POST" {
		var newData map[string]interface{}
		err := json.NewDecoder(r.Body).Decode(&newData)
		if err != nil {
			http.Error(w, "Erreur lors de la lecture des données de la requête: "+err.Error(), http.StatusBadRequest)
			return
		}
	}

	playersJSON, err := json.Marshal(playersFromDB)
	if err != nil {
		http.Error(w, "Erreur lors de l'encodage des données JSON: "+err.Error(), http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	w.Write(playersJSON)
}
