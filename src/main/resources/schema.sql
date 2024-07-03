-- DROP TABLE IF EXISTS users CASCADE;
-- DROP TABLE IF EXISTS friends CASCADE;
-- DROP TABLE IF EXISTS films CASCADE;
-- DROP TABLE IF EXISTS genres CASCADE;
-- DROP TABLE IF EXISTS ratings CASCADE;
-- DROP TABLE IF EXISTS genre_film CASCADE;
-- DROP TABLE IF EXISTS likes CASCADE;

CREATE TABLE IF NOT EXISTS users
(
    id       integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name     varchar,
    login    varchar,
    email    varchar,
    birthday date
);

CREATE TABLE IF NOT EXISTS friends
(
    offered_by   integer,
    candidate_id integer,
    offered_at   timestamp,
    accepted_at  timestamp
);

CREATE TABLE IF NOT EXISTS films
(
    id           integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         varchar,
    description  varchar,
    release_date date,
    duration     integer,
    rating_id    integer
);

CREATE TABLE IF NOT EXISTS genres
(
    id   integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar
);

CREATE TABLE IF NOT EXISTS ratings
(
    id          integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name        varchar,
    description varchar
);

CREATE TABLE IF NOT EXISTS genre_film
(
    genre_id integer,
    film_id  integer,
    PRIMARY KEY (genre_id, film_id)
);

CREATE TABLE IF NOT EXISTS likes
(
    user_id    integer,
    film_id    integer,
    created_at timestamp,
    PRIMARY KEY (user_id, film_id)
);

COMMENT ON COLUMN films.duration IS 'In minutes';

ALTER TABLE likes
    ADD FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;

ALTER TABLE likes
    ADD FOREIGN KEY (film_id) REFERENCES films (id) ON DELETE CASCADE;

ALTER TABLE films
    ADD FOREIGN KEY (rating_id) REFERENCES ratings (id) ON DELETE SET NULL;

ALTER TABLE genre_film
    ADD FOREIGN KEY (film_id) REFERENCES films (id) ON DELETE CASCADE;

ALTER TABLE friends
    ADD FOREIGN KEY (offered_by) REFERENCES users (id) ON DELETE CASCADE;

ALTER TABLE friends
    ADD FOREIGN KEY (candidate_id) REFERENCES users (id) ON DELETE CASCADE;

ALTER TABLE genre_film
    ADD FOREIGN KEY (genre_id) REFERENCES genres (id) ON DELETE CASCADE;

ALTER TABLE genre_film
    ADD FOREIGN KEY (film_id) REFERENCES films (id) ON DELETE CASCADE;
