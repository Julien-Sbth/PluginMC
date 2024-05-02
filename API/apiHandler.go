package API

import (
	"encoding/json"
	"net/http"
)

// Assure-toi de déclarer cette variable au niveau approprié de ton code

func ApiHandler(w http.ResponseWriter, r *http.Request) {
	if r.Method != "GET" && r.Method != "POST" {
		http.Error(w, "Méthode non autorisée", http.StatusMethodNotAllowed)
		return
	}

	if r.Method == "POST" {
		// Lecture du corps de la requête pour obtenir les nouvelles données
		var newData map[string]interface{}
		err := json.NewDecoder(r.Body).Decode(&newData)
		if err != nil {
			http.Error(w, "Erreur lors de la lecture des données de la requête: "+err.Error(), http.StatusBadRequest)
			return
		}
	}

	// Encodage des données JSON pour la réponse
	jsonResponse, err := json.Marshal(playerData)
	if err != nil {
		http.Error(w, "Erreur lors de l'encodage des données JSON: "+err.Error(), http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	// Renvoi des données JSON
	w.Write(jsonResponse)
}
