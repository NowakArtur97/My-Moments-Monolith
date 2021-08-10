CREATE TABLE my_moments.role (
    "id" SERIAL PRIMARY KEY,
    "uuid" VARCHAR(36) NOT NULL,
    "name" TEXT NOT NULL,
    "create_date" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "modify_date" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "version" INT DEFAULT 0 NOT NULL
);