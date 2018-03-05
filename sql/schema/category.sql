DROP TABLE IF EXISTS category CASCADE;

CREATE TABLE category (
    id integer NOT NULL,
    name character varying (100) NOT NULL,
    description character varying (250) NOT NULL,
    difficulty_level integer NOT NULL
);

ALTER TABLE ONLY category
    ADD CONSTRAINT category_pkey PRIMARY KEY (id);
	
ALTER TABLE public.category OWNER TO postgres;

CREATE SEQUENCE category_id_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.category_id_seq OWNER TO postgres;

ALTER SEQUENCE category_id_seq OWNED BY category.id;
	
ALTER TABLE ONLY category 
	ALTER COLUMN id SET DEFAULT nextval('category_id_seq'::regclass);