DROP TABLE IF EXISTS user_request_category_reltn CASCADE;

CREATE TABLE user_request_category_reltn(
    user_id character varying(8) NOT NULL,
    request_category_id integer NOT NULL
);

ALTER TABLE ONLY user_request_category_reltn
    ADD CONSTRAINT user_request_category_reltn_pkey PRIMARY KEY (user_id, request_category_id);
    
ALTER TABLE ONLY user_request_category_reltn
    ADD CONSTRAINT request_category_id_fkey FOREIGN KEY (request_category_id) REFERENCES request_category(id) ON DELETE CASCADE;
			
ALTER TABLE public.user_request_category_reltn OWNER TO postgres;