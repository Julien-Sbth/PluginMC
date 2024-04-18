package Menu

import (
	"APIMC/Connexion"
	"database/sql"
	"html/template"
	"log"
	"net/http"
)

func HandleMenu(w http.ResponseWriter, r *http.Request) {
	db, err := sql.Open("sqlite3", "database.sqlite")
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	defer db.Close()

	// Récupérer les informations de session
	session, err := Connexion.Store.Get(r, Connexion.SessionName)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	// Vérifier si l'utilisateur est connecté
	if username, ok := session.Values["username"].(string); ok {
		// Utilisateur connecté
		tmpl, err := template.ParseFiles("templates/html/Menu/index.html")
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		// Vérifier si l'utilisateur est un administrateur
		var isAdmin bool
		err = db.QueryRow("SELECT est_admin FROM utilisateurs WHERE username = ? AND est_admin = 1", username).Scan(&isAdmin)
		if err != nil {
			if err == sql.ErrNoRows {
				// L'utilisateur n'est pas un administrateur
				isAdmin = false
				log.Println("L'utilisateur", username, "n'est pas un administrateur")
			} else {
				// Erreur lors de la récupération des informations sur l'administrateur
				http.Error(w, err.Error(), http.StatusInternalServerError)
				return
			}
		} else {
			// L'utilisateur est un administrateur
			isAdmin = true
			log.Println("L'utilisateur", username, "est un administrateur")
		}

		data := struct {
			Username     string
			IsLoggedIn   bool
			IsAdmin      bool
			ErrorMessage string
		}{
			Username:     username,
			IsLoggedIn:   true,
			IsAdmin:      isAdmin, // Indiquer si l'utilisateur est un administrateur ou non
			ErrorMessage: "Joueur Inconnu, veuillez en choisir un autre",
		}

		err = tmpl.Execute(w, data)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
		return
	}

	// Si l'utilisateur n'est pas connecté, afficher la page de connexion
	tmpl, err := template.ParseFiles("templates/html/Menu/index.html")
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	if err := tmpl.Execute(w, nil); err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
	}
}
