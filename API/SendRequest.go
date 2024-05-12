package API

import (
	"encoding/json"
	"fmt"
	"net/http"
)

func SendRequest(w http.ResponseWriter, r *http.Request) {
	if r.URL.Path != "/items" {
		http.Error(w, "Route non autorisée", http.StatusNotFound)
		return
	}

	switch r.Method {
	case "GET":
		achats, err := retrieveAchatsFromDB()
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		responseJSON, err := json.Marshal(achats)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusOK)
		w.Write(responseJSON)

	case "POST":
		var item Item
		err := json.NewDecoder(r.Body).Decode(&item)
		if err != nil {
			http.Error(w, err.Error(), http.StatusBadRequest)
			return
		}

		fmt.Printf("Nouvel élément reçu : %+v\n", item)

		w.WriteHeader(http.StatusOK)
		fmt.Fprintf(w, "Réception réussie de l'élément\n")

	default:
		http.Error(w, "Méthode non autorisée", http.StatusMethodNotAllowed)
	}
}
