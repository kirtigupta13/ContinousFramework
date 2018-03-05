DROP TABLE IF EXISTS category_resource_reltn CASCADE;

CREATE TABLE category_resource_reltn (
    resource_id integer,
    category_id integer,
    difficulty_level integer DEFAULT 1
);

ALTER TABLE public.category_resource_reltn OWNER TO postgres;


