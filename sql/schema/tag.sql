DROP TABLE IF EXISTS tag CASCADE;

CREATE TABLE tag (
    tag_id integer NOT NULL,
    tag_name character varying(255) NOT NULL
);

ALTER TABLE ONLY tag
    ADD CONSTRAINT tag_pkey PRIMARY KEY (tag_id);
	
ALTER TABLE public.tag OWNER TO postgres;

ALTER TABLE tag ADD CONSTRAINT tag_name_not_blank CHECK(TRIM(tag_name) <> '');

CREATE UNIQUE INDEX tag_tag_name ON tag (LOWER(tag_name));

CREATE SEQUENCE tag_tag_id_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.tag_tag_id_seq OWNER TO postgres;

ALTER SEQUENCE tag_tag_id_seq OWNED BY tag.tag_id;

ALTER TABLE ONLY tag ALTER COLUMN tag_id SET DEFAULT nextval('tag_tag_id_seq'::regclass);