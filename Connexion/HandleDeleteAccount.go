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
			// Supprimer la session après la suppression du compte
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

	// Si la méthode n'est pas POST ou si l'utilisateur n'est pas connecté, rediriger vers la page de connexion
	http.Redirect(w, r, "/", http.StatusSeeOther)
}
