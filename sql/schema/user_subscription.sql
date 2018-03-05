DROP TABLE IF EXISTS user_subscription CASCADE;

CREATE TABLE user_subscription (
    user_id character varying(8) NOT NULL,
    category_id integer NOT NULL
  
);

ALTER TABLE ONLY user_subscription
    ADD CONSTRAINT user_subscription_pkey PRIMARY KEY (user_id,category_id);
    
ALTER TABLE ONLY user_subscription
    ADD CONSTRAINT user_subscription_category_id_fkey FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE CASCADE;

	
ALTER TABLE public.user_subscription OWNER TO postgres;