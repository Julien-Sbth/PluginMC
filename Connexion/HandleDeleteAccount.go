package Connexion

import (
	"database/sql"
	"net/http"
)

func HandleDeleteAccount(w http.ResponseWriter, r *http.Request) {
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

	if r.Method == "POST" {
		if username, ok := session.Values["username"].(string); ok {
			_, err := db.Exec("DELETE FROM utilisateurs WHERE username = ?", username)
			if err != nil {
				http.Error(w, err.Error(), http.StatusInternalServerError)
				return
			}
			session.Options.MaxAge = -1
			err = session.Save(r, w)
			if err != nil {
				http.Error(w, err.Error(), http.StatusInternalServerError)
				return
			}
			http.Redirect(w, r, "/", http.StatusSeeOther)
			return
		}
	}
	http.Redirect(w, r, "/", http.StatusSeeOther)
}
