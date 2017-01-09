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

CREATE TABLE IF NOT EXISTS workflow_global_params (
	workflow_global_id		INTEGER NOT NULL AUTO_INCREMENT,
	workflow_id				INTEGER NOT NULL,
	parameter_name			VARCHAR(50) NOT NULL,
    PRIMARY KEY (workflow_global_id),
	INDEX (workflow_id),
	FOREIGN KEY (workflow_id) REFERENCES workflows (workflow_id)
);

CREATE TABLE IF NOT EXISTS workflow_step_params (
	wkf_step_param_id		INTEGER NOT NULL AUTO_INCREMENT,
	workflow_step_id		INTEGER NOT NULL,
	parameter_name			VARCHAR(50) NOT NULL,
    PRIMARY KEY (wkf_step_param_id),
	INDEX (workflow_step_id),
	FOREIGN KEY (workflow_step_id) REFERENCES workflow_steps (workflow_step_id)
);

CREATE TABLE IF NOT EXISTS step_prerequisites (
	workflow_step_id		INTEGER NOT NULL,
	prerequisite_step_id	INTEGER NOT NULL,
	INDEX (workflow_step_id),
	FOREIGN KEY (workflow_step_id) REFERENCES workflow_steps (workflow_step_id),
	FOREIGN KEY (prerequisite_step_id) REFERENCES workflow_steps (workflow_step_id)
);

CREATE TABLE IF NOT EXISTS workflow_global_values (
	workflow_global_id		INTEGER NOT NULL,
    parameter_value			VARCHAR(200) NOT NULL,
    INDEX (workflow_global_id),
    FOREIGN KEY (workflow_global_id) REFERENCES workflow_global_params (workflow_global_id)
);

CREATE TABLE IF NOT EXISTS workflow_step_values (
	wkf_step_param_id		INTEGER NOT NULL,
    parameter_value			VARCHAR(200) NOT NULL,
    INDEX (wkf_step_param_id),
    FOREIGN KEY (wkf_step_param_id) REFERENCES workflow_step_params (wkf_step_param_id)
);

/*
CREATE TABLE IF NOT EXISTS batches (
);

CREATE TABLE IF NOT EXISTS runs (
);

CREATE TABLE IF NOT EXISTS run_global_params (
);

CREATE TABLE IF NOT EXISTS run_step_params (
);

CREATE TABLE IF NOT EXISTS step_status (
);

CREATE TABLE IF NOT EXISTS run_steps (
);

CREATE TABLE IF NOT EXISTS workers (
);

CREATE TABLE IF NOT EXISTS worker_steps (
);

CREATE TABLE IF NOT EXISTS step_errors (
);