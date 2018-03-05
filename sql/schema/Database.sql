/*Step 1
Purpose: Creating tables in an order
File usage instructions: 
1. Install PostgreSQL and PgAdmin
2. Open PgAdmin and create a database with below name and configuration
3. Open the created database and run the remaining code in Database.sql as PGScript.*/

-- Database: continue_education
DROP DATABASE IF EXISTS continue_education CASCADE;
CREATE DATABASE continue_education
  WITH OWNER = postgres
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       LC_COLLATE = 'English_United States.1252'
       LC_CTYPE = 'English_United States.1252'
       CONNECTION LIMIT = -1;
       
-- Table auth_level
CREATE TABLE auth_level (
    authorization_level numeric NOT NULL,
    description character varying(200)
);
ALTER TABLE ONLY auth_level
    ADD CONSTRAINT auth_level_pkey PRIMARY KEY (authorization_level);
ALTER TABLE public.auth_level OWNER TO postgres;

-- Table admins
CREATE TABLE admins (
    user_id character varying(8) NOT NULL,
    first_name character varying(300),
    last_name character varying(300),
    email_id character varying(200),
    role character varying(60),
    auth_level integer,
    isdeleted boolean DEFAULT false NOT NULL,
    title character varying(100),
    department character varying(100)
);
ALTER TABLE ONLY admins
    ADD CONSTRAINT user_id_pkey PRIMARY KEY (user_id);
ALTER TABLE ONLY admins
    ADD CONSTRAINT auth_level_fkey FOREIGN KEY (auth_level) REFERENCES auth_level(authorization_level) ON DELETE CASCADE;
ALTER TABLE public.admins OWNER TO postgres;


-- Table category
CREATE TABLE category (
    id integer NOT NULL,
    name character varying (100) NOT NULL,
    description character varying (250) NOT NULL,
    difficulty_level integer NOT NULL
);
ALTER TABLE ONLY category
    ADD CONSTRAINT category_pkey PRIMARY KEY (id);
ALTER TABLE public.category OWNER TO postgres;
CREATE SEQUENCE category_id_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    NO MAXVALUE
    CACHE 1;
ALTER TABLE public.category_id_seq OWNER TO postgres;
ALTER SEQUENCE category_id_seq OWNED BY category.id;
ALTER TABLE ONLY category 
	ALTER COLUMN id SET DEFAULT nextval('category_id_seq'::regclass);

-- Table type
CREATE TABLE type (
    type_name character varying(255),
    type_id integer NOT NULL
);

ALTER TABLE ONLY type
    ADD CONSTRAINT type_pkey PRIMARY KEY (type_id);
ALTER TABLE public.type OWNER TO postgres;
CREATE SEQUENCE type_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER TABLE public.type_type_id_seq OWNER TO postgres;
ALTER SEQUENCE type_type_id_seq OWNED BY type.type_id;
ALTER TABLE ONLY type ALTER COLUMN type_id SET DEFAULT nextval('type_type_id_seq'::regclass);

-- Table resource
CREATE TYPE status AS ENUM ('Available', 'Pending', 'Deleted');
CREATE TABLE resource (
    resource_id integer NOT NULL,
    name character varying(255) NOT NULL,
    link character varying(255) NOT NULL,
    description character varying(200) NOT NULL,
    type_id integer NOT NULL,
    resource_owner character varying(8) NOT NULL,
    status status DEFAULT 'Available' NOT NULL
);
ALTER TABLE ONLY resource
    ADD CONSTRAINT resource_pkey PRIMARY KEY (resource_id);
ALTER TABLE ONLY resource
    ADD CONSTRAINT type_id_fkey FOREIGN KEY (type_id) REFERENCES type(type_id) ON DELETE CASCADE;
ALTER TABLE public.resource OWNER TO postgres;
CREATE SEQUENCE resource_resource_id_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    NO MAXVALUE
    CACHE 1;
ALTER TABLE public.resource_resource_id_seq OWNER TO postgres;
ALTER SEQUENCE resource_resource_id_seq OWNED BY resource.resource_id;
ALTER TABLE ONLY resource 
	ALTER COLUMN resource_id SET DEFAULT nextval('resource_resource_id_seq'::regclass);

-- Table topic_resource_reltn
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

-- Table user_interested_topic
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

-- Table tag
CREATE TABLE tag (
    tag_id integer NOT NULL,
    tag_name character varying(255) NOT NULL
);
ALTER TABLE ONLY tag
    ADD CONSTRAINT tag_pkey PRIMARY KEY (tag_id);
ALTER TABLE public.tag OWNER TO postgres;
ALTER TABLE tag ADD CONSTRAINT tag_name_not_blank CHECK(TRIM(tag_name) <> '');
CREATE UNIQUE INDEX tag_tag_name ON tag (LOWER(tag_name));
CREATE SEQUENCE tag_tag_id_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    NO MAXVALUE
    CACHE 1;
ALTER TABLE public.tag_tag_id_seq OWNER TO postgres;
ALTER SEQUENCE tag_tag_id_seq OWNED BY tag.tag_id;
ALTER TABLE ONLY tag ALTER COLUMN tag_id SET DEFAULT nextval('tag_tag_id_seq'::regclass);

-- Table tag_resource_reltn
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

-- Table completed_user_resource
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

-- Table user_resource_rating
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

-- Table user_subscription
CREATE TABLE user_subscription (
    user_id character varying(8) NOT NULL,
    category_id integer NOT NULL
  
);
ALTER TABLE ONLY user_subscription
    ADD CONSTRAINT user_subscription_pkey PRIMARY KEY (user_id,category_id);
ALTER TABLE ONLY user_subscription
    ADD CONSTRAINT user_subscription_category_id_fkey FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE CASCADE;
ALTER TABLE public.user_subscription OWNER TO postgres;

-- Table request_category
CREATE TABLE request_category(
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    is_approved boolean DEFAULT false NOT NULL
);
ALTER TABLE ONLY request_category
    ADD CONSTRAINT request_category_pkey PRIMARY KEY (id);
ALTER TABLE public.request_category OWNER TO postgres;
CREATE SEQUENCE request_category_id_seq;
ALTER TABLE public.request_category_id_seq OWNER TO postgres;
ALTER SEQUENCE request_category_id_seq OWNED BY request_category.id;
ALTER TABLE ONLY request_category ALTER COLUMN id SET DEFAULT nextval('request_category_id_seq'::regclass);

-- Table user_request_category_reltn
CREATE TABLE user_request_category_reltn(
    user_id character varying(8) NOT NULL,
    request_category_id integer NOT NULL
);
ALTER TABLE ONLY user_request_category_reltn
    ADD CONSTRAINT user_request_category_reltn_pkey PRIMARY KEY (user_id, request_category_id);
ALTER TABLE ONLY user_request_category_reltn
    ADD CONSTRAINT request_category_id_fkey FOREIGN KEY (request_category_id) REFERENCES request_category(id) ON DELETE CASCADE;
ALTER TABLE public.user_request_category_reltn OWNER TO postgres;

-- Table resource_request
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

-- Table category_resource_reltn
CREATE TABLE category_resource_reltn (
    resource_id integer,
    category_id integer,
    difficulty_level integer DEFAULT 1
);
ALTER TABLE public.category_resource_reltn OWNER TO postgres;

-- Table user_interested_category
CREATE TABLE user_interested_category (
    user_id character varying(8) NOT NULL,
    category_id integer NOT NULL,
    skill_level integer,
    interest_level integer
);
ALTER TABLE ONLY user_interested_category
    ADD CONSTRAINT user_interested_category_pkey PRIMARY KEY (user_id, category_id);
ALTER TABLE public.user_interested_category OWNER TO postgres;

-- Table learners
CREATE TABLE learners (
  id serial NOT NULL,
  user_id character varying(8) NOT NULL,
  email character varying(50) NOT NULL,
  CONSTRAINT learners_pkey PRIMARY KEY (id),
  CONSTRAINT learners_user_id_key UNIQUE (user_id)
);











