DROP TABLE IF EXISTS specialists;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS visits;
CREATE TABLE specialists (id bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
                          email varchar(40) UNIQUE NOT NULL,
                          first_name varchar(40) NOT NULL,
                          last_name varchar(40) NOT NULL,
                          password varchar(60) NOT NULL);
CREATE TABLE customers (id bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
                        email varchar(40) UNIQUE NOT NULL,
                        first_name varchar(40) NOT NULL DEFAULT '',
                        last_name varchar(40) NOT NULL DEFAULT '',
                        password varchar(60) NOT NULL);
CREATE TABLE visits (id bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
                        specialist_id bigint(20) NOT NULL,
                        customer_id bigint(20) NOT NULL,
                        visit_time bigint(13) NOT NULL,
                        visit_duration int NOT NULL,
                        serial varchar(6) NOT NULL);
CREATE INDEX email ON customers(email);
CREATE INDEX visit_time ON visits(visit_time);

