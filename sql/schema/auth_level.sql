DROP TABLE IF EXISTS auth_level CASCADE;
CREATE TABLE auth_level (
    authorization_level numeric NOT NULL,
    description character varying(200)
);

ALTER TABLE ONLY auth_level
    ADD CONSTRAINT auth_level_pkey PRIMARY KEY (authorization_level);

ALTER TABLE public.auth_level OWNER TO postgres;