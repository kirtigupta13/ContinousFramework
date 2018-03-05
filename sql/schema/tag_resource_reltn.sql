DROP TABLE IF EXISTS tag_resource_reltn CASCADE;

CREATE TABLE tag_resource_reltn (
    tag_id integer NOT NULL,
    resource_id integer NOT NULL
);

ALTER TABLE ONLY tag_resource_reltn
    ADD CONSTRAINT tag_resource_reltn_pkey PRIMARY KEY (tag_id, resource_id);

ALTER TABLE ONLY tag_resource_reltn
    ADD CONSTRAINT tag_resource_reltn_tag_id_fkey FOREIGN KEY (tag_id) REFERENCES tag(tag_id) ON DELETE CASCADE;
	
ALTER TABLE ONLY tag_resource_reltn
    ADD CONSTRAINT tag_resource_reltn_resource_id_fkey FOREIGN KEY (resource_id) REFERENCES resource(resource_id) ON DELETE CASCADE;

ALTER TABLE public.tag_resource_reltn OWNER TO postgres;