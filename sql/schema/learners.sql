DROP TABLE IF EXISTS learners CASCADE;

CREATE TABLE learners (
  id serial NOT NULL,
  user_id character varying(8) NOT NULL,
  email character varying(50) NOT NULL,
  CONSTRAINT learners_pkey PRIMARY KEY (id),
  CONSTRAINT learners_user_id_key UNIQUE (user_id)
)
