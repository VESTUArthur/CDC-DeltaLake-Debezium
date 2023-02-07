CREATE DATABASE IF NOT EXISTS ping;

CREATE TABLE ping.subscriber_sb (
    sb_id INTEGER NOT NULL AUTO_INCREMENT,
    sb_email VARCHAR(255),
    sb_password VARCHAR(255),
    sb_type VARCHAR(255),
    sb_price INTEGER,
  CONSTRAINT sb_id_pk PRIMARY KEY (sb_id)
);
