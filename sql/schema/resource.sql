CREATE TYPE status AS ENUM ('Available', 'Pending', 'Deleted');

DROP TABLE IF EXISTS resource CASCADE;

CREATE TABLE resource (
    resource_id integer NOT NULL,
    name character varying(255) NOT NULL,
    link character varying(255) NOT NULL,
    description character varying(200) NOT NULL,
    type_id integer NOT NULL,
    resource_owner character varying(8) NOT NULL,
    status status DEFAULT 'Available' NOT NULL
);

ALTER TABLE ONLY resource
    ADD CONSTRAINT resource_pkey PRIMARY KEY (resource_id);
    
ALTER TABLE ONLY resource
    ADD CONSTRAINT type_id_fkey FOREIGN KEY (type_id) REFERENCES type(type_id) ON DELETE CASCADE;

ALTER TABLE public.resource OWNER TO postgres;

CREATE SEQUENCE resource_resource_id_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.resource_resource_id_seq OWNER TO postgres;

ALTER SEQUENCE resource_resource_id_seq OWNED BY resource.resource_id;

ALTER TABLE ONLY resource 
	ALTER COLUMN resource_id SET DEFAULT nextval('resource_resource_id_seq'::regclass);