CREATE TABLE my_moments.picture (
    "id" SERIAL PRIMARY KEY,
    "post_id" INT NOT NULL,
    "uuid" VARCHAR(36) NOT NULL,
    "photo" BYTEA,
    "create_date" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "modify_date" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "version" INT DEFAULT 0 NOT NULL,

    CONSTRAINT "FK_POST_PICTURE" FOREIGN KEY ("post_id") REFERENCES my_moments.post ("id") ON DELETE CASCADE ON UPDATE CASCADE
);