DROP TABLE IF EXISTS user_resource_rating CASCADE;

CREATE TABLE user_resource_rating (
    user_id character varying(8) NOT NULL,
    resource_id integer NOT NULL,
    rating integer,
    completion_status integer
);

ALTER TABLE ONLY user_resource_rating
    ADD CONSTRAINT user_resource_rating_pkey PRIMARY KEY (user_id, resource_id);
    
ALTER TABLE ONLY user_resource_rating
    ADD CONSTRAINT resource_id_fkey FOREIGN KEY (resource_id) REFERENCES resource(resource_id) ON DELETE CASCADE;

ALTER TABLE public.user_resource_rating OWNER TO postgres;