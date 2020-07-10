CREATE DATABASE post;

CREATE TABLE post(
id serial PRIMARY KEY,
name varchar(200),
text text,
link varchar(200) UNIQUE,
created date
);