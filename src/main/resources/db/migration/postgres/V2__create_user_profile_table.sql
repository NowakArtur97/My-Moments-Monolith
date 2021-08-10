CREATE TABLE my_moments.user_profile (
    "id" SERIAL PRIMARY KEY,
    "uuid" VARCHAR(36) NOT NULL,
    "about" TEXT DEFAULT '',
    "gender" TEXT NOT NULL,
    "interests" TEXT DEFAULT '',
    "languages" TEXT DEFAULT '',
    "location" TEXT DEFAULT '',
    "image" BYTEA,
    "create_date" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "modify_date" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "version" INT DEFAULT 0 NOT NULL,

    CONSTRAINT "FK_USER_USER_PROFILE" FOREIGN KEY ("id") REFERENCES my_moments.user ("id") ON DELETE CASCADE ON UPDATE CASCADE
);