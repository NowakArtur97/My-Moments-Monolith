CREATE SCHEMA "my_moments";

CREATE TABLE my_moments.user (
	"id" SERIAL PRIMARY KEY,
  "uuid" VARCHAR(36) NOT NULL,
	"username" TEXT NOT NULL UNIQUE,
  "password" TEXT NOT NULL,
  "email" TEXT NOT NULL UNIQUE,
  "create_date" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  "modify_date" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  "version" INT DEFAULT 0 NOT NULL
);