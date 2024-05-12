package API

import "database/sql"

func markItemAsReceived(itemID string) error {
	db, err := sql.Open("sqlite3", "database.sqlite")
	if err != nil {
		return err
	}
	defer db.Close()

	_, err = db.Exec("UPDATE achats SET send = true WHERE item_id = ?", itemID)
	if err != nil {
		return err
	}

	return nil
}
