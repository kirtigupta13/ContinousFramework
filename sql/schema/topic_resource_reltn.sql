DROP TABLE IF EXISTS topic_resource_reltn CASCADE;

CREATE TABLE topic_resource_reltn (
    resource_id integer,
    topic_id integer,
    difficulty_level integer DEFAULT 1
);

ALTER TABLE public.topic_resource_reltn OWNER TO postgres;

ALTER TABLE ONLY topic_resource_reltn
    ADD CONSTRAINT topic_resource_reltn_pkey PRIMARY KEY (topic_id, resource_id);

ALTER TABLE ONLY topic_resource_reltn
    ADD CONSTRAINT resource_id_fkey FOREIGN KEY (resource_id) REFERENCES resource(resource_id) ON DELETE CASCADE;
    
ALTER TABLE ONLY topic_resource_reltn
    ADD CONSTRAINT topic_id_fkey FOREIGN KEY (topic_id) REFERENCES category(id) ON DELETE CASCADE;