DROP TABLE IF EXISTS admins CASCADE;

CREATE TABLE admins (
    user_id character varying(8) NOT NULL,
    first_name character varying(300),
    last_name character varying(300),
    email_id character varying(200),
    role character varying(60),
    auth_level integer,
    isdeleted boolean DEFAULT false NOT NULL,
    title character varying(100),
    department character varying(100)
);

ALTER TABLE ONLY admins
    ADD CONSTRAINT user_id_pkey PRIMARY KEY (user_id);
	
ALTER TABLE ONLY admins
    ADD CONSTRAINT auth_level_fkey FOREIGN KEY (auth_level) REFERENCES auth_level(authorization_level) ON DELETE CASCADE;
	
ALTER TABLE public.admins OWNER TO postgres;