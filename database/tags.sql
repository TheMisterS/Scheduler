CREATE TABLE IF NOT EXISTS scheduler.tags
(
    tag_id smallint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 2000 MINVALUE 2000 MAXVALUE 32767 CACHE 1 ),
    tag_name character varying(64),
    
    CONSTRAINT "Tags_pkey" PRIMARY KEY (tag_id)
)

TABLESPACE pg_default;