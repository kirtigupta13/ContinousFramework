DROP TABLE IF EXISTS completed_user_resource CASCADE;

CREATE TABLE completed_user_resource(
    user_id character varying(8) NOT NULL,
    resource_id integer NOT NULL,
    completion_rating integer,
    completion_date bigint NOT NULL
);

ALTER TABLE ONLY completed_user_resource
    ADD CONSTRAINT completed_user_resource_pkey PRIMARY KEY (user_id, resource_id);

ALTER TABLE ONLY completed_user_resource
    ADD CONSTRAINT resource_id_fkey FOREIGN KEY (resource_id) REFERENCES resource(resource_id) ON DELETE CASCADE;
	
ALTER TABLE public.completed_user_resource OWNER TO postgres;