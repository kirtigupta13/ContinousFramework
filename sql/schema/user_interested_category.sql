DROP TABLE IF EXISTS user_interested_category CASCADE;

CREATE TABLE user_interested_category (
    user_id character varying(8) NOT NULL,
    category_id integer NOT NULL,
    skill_level integer,
    interest_level integer
);

ALTER TABLE ONLY user_interested_category
    ADD CONSTRAINT user_interested_category_pkey PRIMARY KEY (user_id, category_id);
	
ALTER TABLE public.user_interested_category OWNER TO postgres;