GRANT SELECT, INSERT, UPDATE ON `music_meta_service`.* TO 'mms_user'@'%';

CREATE USER 'mms_liquibase_user'@'%' IDENTIFIED BY 'CjzPcREYIyotEpCWadiz';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, REFERENCES, INDEX, ALTER ON `music_meta_service`.* TO 'mms_liquibase_user'@'%';
