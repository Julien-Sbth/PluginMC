package Connexion

import (
	"database/sql"
	"html/template"
	"net/http"
)

func HandleProfile(w http.ResponseWriter, r *http.Request) {
	db, err := sql.Open("sqlite3", "database.sqlite")
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	defer db.Close()

	session, err := Store.Get(r, SessionName)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	if username, ok := session.Values["username"].(string); ok {
		tmpl, err := template.ParseFiles("templates/html/Menu/profile.html")
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		var email, dateInscription interface{}
		err = db.QueryRow("SELECT email, date_inscription FROM utilisateurs WHERE username = ?", username).Scan(&email, &dateInscription)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		nextResetToken, err := GenerateToken()
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		data := struct {
			Email           string
			DateInscription interface{}
			Token           string
			Username        string
			IsLoggedIn      bool
		}{
			Email:           email.(string),
			DateInscription: dateInscription,
			Username:        username,
			IsLoggedIn:      true,
			Token:           nextResetToken,
		}

		err = tmpl.Execute(w, data)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
		return
	}

	tmpl, err := template.ParseFiles("templates/html/Menu/profile.html")
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	if err := tmpl.Execute(w, nil); err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
	}
}
