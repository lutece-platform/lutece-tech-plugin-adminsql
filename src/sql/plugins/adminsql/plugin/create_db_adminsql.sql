DROP TABLE IF EXISTS adminsql_field_type;

CREATE TABLE adminsql_field_type (
  id_field_type INT NOT NULL,
  label_field_type varchar(30) DEFAULT '' NOT NULL,
  PRIMARY KEY  (id_field_type)
);

DROP TABLE IF EXISTS adminsql_field_key;

CREATE TABLE adminsql_field_key (
  id_field_key INT DEFAULT '12' NOT NULL,
  label_field_key varchar(30) DEFAULT NULL,
  PRIMARY KEY  (id_field_key)
);

DROP TABLE IF EXISTS adminsql_field_null;

CREATE TABLE adminsql_field_null (
  id_field_null INT NOT NULL,
  label_field_null varchar(30) DEFAULT NULL,
  PRIMARY KEY  (id_field_null)
);