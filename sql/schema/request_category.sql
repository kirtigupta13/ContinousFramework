DROP TABLE IF EXISTS request_category CASCADE;

CREATE TABLE request_category(
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    is_approved boolean DEFAULT false NOT NULL
);

ALTER TABLE ONLY request_category
    ADD CONSTRAINT request_category_pkey PRIMARY KEY (id);
			
ALTER TABLE public.request_category OWNER TO postgres;

CREATE SEQUENCE request_category_id_seq;

ALTER TABLE public.request_category_id_seq OWNER TO postgres;

ALTER SEQUENCE request_category_id_seq OWNED BY request_category.id;

ALTER TABLE ONLY request_category ALTER COLUMN id SET DEFAULT nextval('request_category_id_seq'::regclass);