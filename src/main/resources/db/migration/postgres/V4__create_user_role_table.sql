CREATE TABLE my_moments.user_role (
    "user_id" INT NOT NULL,
    "role_id" INT NOT NULL,

    PRIMARY KEY("user_id", "role_id"),
    CONSTRAINT "FK_USER_ROLE" FOREIGN KEY ("user_id") REFERENCES my_moments.user("id") ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT "FK_ROLE_USER" FOREIGN KEY ("role_id") REFERENCES my_moments.role("id") ON DELETE NO ACTION ON UPDATE NO ACTION
);