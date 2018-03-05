DROP TABLE IF EXISTS type CASCADE;

CREATE TABLE type (
    type_name character varying(255),
    type_id integer NOT NULL
);

ALTER TABLE ONLY type
    ADD CONSTRAINT type_pkey PRIMARY KEY (type_id);
	
ALTER TABLE public.type OWNER TO postgres;

CREATE SEQUENCE type_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.type_type_id_seq OWNER TO postgres;

ALTER SEQUENCE type_type_id_seq OWNED BY type.type_id;

ALTER TABLE ONLY type ALTER COLUMN type_id SET DEFAULT nextval('type_type_id_seq'::regclass);