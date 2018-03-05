DROP TABLE IF EXISTS resource_request CASCADE;

CREATE TABLE resource_request(
    id integer NOT NULL,
    user_id character varying(8) NOT NULL,
    category_name character varying(255) NOT NULL,
    resource_name character varying(255) NOT NULL,
    is_approved boolean DEFAULT false NOT NULL
);

ALTER TABLE ONLY resource_request
    ADD CONSTRAINT resource_request_pkey PRIMARY KEY (id);
			
ALTER TABLE public.resource_request OWNER TO postgres;

CREATE SEQUENCE resource_request_id_seq;

ALTER TABLE public.resource_request_id_seq OWNER TO postgres;

ALTER SEQUENCE resource_request_id_seq OWNED BY resource_request.id;

ALTER TABLE ONLY resource_request ALTER COLUMN id SET DEFAULT nextval('resource_request_id_seq'::regclass);
