DROP TABLE IF EXISTS user_interested_topic CASCADE;

CREATE TABLE user_interested_topic (
    user_id character varying(8) NOT NULL,
    topic_id integer NOT NULL,
    skill_level integer,
    interest_level integer
);

ALTER TABLE ONLY user_interested_topic
    ADD CONSTRAINT user_interested_topic_pkey PRIMARY KEY (user_id, topic_id);

ALTER TABLE ONLY user_interested_topic
    ADD CONSTRAINT topic_id_fkey FOREIGN KEY (topic_id) REFERENCES category(id) ON DELETE CASCADE;
	
ALTER TABLE public.user_interested_topic OWNER TO postgres;