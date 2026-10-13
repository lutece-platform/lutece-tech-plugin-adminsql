-- liquibase formatted sql
-- changeset adminsql:init_core_adminsql.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM core_admin_right WHERE id_right = 'ADMIN_SQL_MANAGEMENT'
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url) VALUES
('ADMIN_SQL_MANAGEMENT','adminsql.adminFeature.adminsql_management.name',1,'jsp/admin/plugins/adminsql/ManageDatabases.jsp','adminsql.adminFeature.adminsql_management.description',0,'adminsql',NULL,NULL,NULL);

-- changeset adminsql:init_core_adminsql-rev1.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM core_user_right WHERE id_right = 'ADMIN_SQL_MANAGEMENT' AND id_user = 1
INSERT INTO core_user_right (id_right,id_user) VALUES ('ADMIN_SQL_MANAGEMENT',1);
