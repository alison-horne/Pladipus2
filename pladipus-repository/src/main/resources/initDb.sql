CREATE SCHEMA IF NOT EXISTS pladipus2;

USE pladipus2;

CREATE TABLE IF NOT EXISTS users (
  user_id   INTEGER NOT NULL AUTO_INCREMENT,
  user_name VARCHAR(30) NOT NULL UNIQUE,
  email 	VARCHAR(254),
  password 	VARCHAR(32) NOT NULL,
  active 	BOOLEAN NOT NULL DEFAULT 1,
  PRIMARY KEY (user_id)
);

CREATE TABLE IF NOT EXISTS roles (
  role_id	INTEGER NOT NULL UNIQUE,
  role_name	VARCHAR(20) NOT NULL UNIQUE,
  PRIMARY KEY (role_id)
);
  
INSERT INTO roles (role_id, role_name)
SELECT 1, 'ADMIN' FROM DUAL WHERE NOT EXISTS(
    SELECT 1 FROM roles
    WHERE role_id = 1
);

INSERT INTO roles (role_id, role_name)
SELECT 2, 'USER' FROM DUAL WHERE NOT EXISTS(
    SELECT 1 FROM roles
    WHERE role_id = 2
);

CREATE TABLE IF NOT EXISTS user_roles (
  user_id	INTEGER NOT NULL,
  role_id	INTEGER NOT NULL DEFAULT 2
);

CREATE TABLE IF NOT EXISTS workflows (
	workflow_id		INTEGER NOT NULL AUTO_INCREMENT,
    workflow_name	VARCHAR(50) NOT NULL,
    template		TEXT NOT NULL,	
    user_id			INTEGER,
    active			BOOLEAN NOT NULL DEFAULT 1,
    PRIMARY KEY (workflow_id),
	INDEX (user_id),
	FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS common_defaults (
	default_id		INTEGER NOT NULL AUTO_INCREMENT,
	user_id			INTEGER,
	default_type	VARCHAR(50),
	default_value	TEXT NOT NULL,
	default_name	VARCHAR(50) NOT NULL,
	PRIMARY KEY (default_id),
	INDEX (user_id),
	FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS workflow_steps (
	workflow_step_id		INTEGER NOT NULL AUTO_INCREMENT,
	workflow_id				INTEGER NOT NULL,
	step_identifier			VARCHAR(5) NOT NULL,
	tool_type				VARCHAR(100) NOT NULL,
	PRIMARY KEY (workflow_step_id),
    UNIQUE KEY (workflow_id, step_identifier)
);
