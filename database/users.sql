CREATE TABLE IF NOT EXISTS scheduler.users
(
    password_hash character varying ,
    email character varying(255) ,
    username character varying(64) ,
    user_id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1000 MINVALUE 1000 MAXVALUE 32767 CACHE 1 ),


    
    CONSTRAINT users_pkey PRIMARY KEY (user_id),
    CONSTRAINT users_email_key UNIQUE (email),
    CONSTRAINT users_password_hash_check CHECK (password_hash ~* '^[A-Fa-f0-9]{64}$'),
    CONSTRAINT users_email_check CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    CONSTRAINT users_username_check CHECK (username ~ '^[A-Za-z0-9_]+$')
)

TABLESPACE pg_default;

--INDEXES

CREATE UNIQUE INDEX IF NOT EXISTS users_email_email1_idx
    ON scheduler.users
    USING btree
    INCLUDE(email)
    WITH (deduplicate_items = TRUE)
    TABLESPACE pg_default;
