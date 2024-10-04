#!/bin/bash

DB_NAME="scheduler_db"
DB_USER="postgres"
DB_PASSWORD="admin"
SQL_FILE="database/all_db_creation_sentences.sql"

export PGPASSWORD=$DB_PASSWORD

DB_EXIST=$(psql -U $DB_USER -tc "SELECT 1 FROM pg_database WHERE datname = '$DB_NAME';")

if [[ $DB_EXIST != 1 ]]; then
    echo "Creating database: $DB_NAME"
    createdb -U $DB_USER $DB_NAME
else
    echo "Database $DB_NAME already exists."
fi

echo "Initializing the schema and tables..."
psql -U $DB_USER -d $DB_NAME -f $SQL_FILE

echo "Database initialization complete."

unset PGPASSWORD
