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

	session, err := Connexion.Store.Get(r, Connexion.SessionName)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	if username, ok := session.Values["username"].(string); ok {
		tmpl, err := template.ParseFiles("templates/html/Menu/index.html")
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		var isAdmin bool
		err = db.QueryRow("SELECT est_admin FROM utilisateurs WHERE username = ? AND est_admin = 1", username).Scan(&isAdmin)
		if err != nil {
			if err == sql.ErrNoRows {
				isAdmin = false
				log.Println("L'utilisateur", username, "n'est pas un administrateur")
			} else {
				http.Error(w, err.Error(), http.StatusInternalServerError)
				return
			}
		} else {
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
			IsAdmin:      isAdmin,
			ErrorMessage: "Joueur Inconnu, veuillez en choisir un autre",
		}

		err = tmpl.Execute(w, data)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
		return
	}

	tmpl, err := template.ParseFiles("templates/html/Menu/index.html")
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	if err := tmpl.Execute(w, nil); err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
	}
}
