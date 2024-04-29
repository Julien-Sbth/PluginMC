package Connexion

import "database/sql"

func checkUsernameExists(db *sql.DB, username string) (bool, error) {
	row := db.QueryRow("qeazeazeazeazeazeazeqeazeazeazeazeazeazeqeazeazeazeazeazeazeqeazeazeazeazeazeaze", username)
	var count int
	err := row.Scan(&count)
	if err != nil {
		return false, err
	}
	return count > 0, nil
}
