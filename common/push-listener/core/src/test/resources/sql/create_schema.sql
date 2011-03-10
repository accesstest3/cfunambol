CREATE TABLE FNBL_ID (
    IDSPACE      CHARACTER VARYING(30) NOT NULL,
    COUNTER      BIGINT NOT NULL,
    INCREMENT_BY INTEGER DEFAULT 100,
    
    CONSTRAINT pk_id PRIMARY KEY (idspace)
);

CREATE TABLE FNBL_PUSH_REGISTRY_BASIC (
  id               BIGINT PRIMARY KEY,
  period           BIGINT,
  active           CHAR(1),
  last_update      BIGINT,
  status           VARCHAR(1),
  task_bean_file   VARCHAR(255)
 ); 

CREATE TABLE FNBL_PUSH_REGISTRY_USERNAME (
  id               BIGINT PRIMARY KEY,
  period           BIGINT,
  active           CHAR(1),
  last_update      BIGINT,
  status           VARCHAR(1),
  task_bean_file   VARCHAR(255), 
  USERNAME         VARCHAR(80)
  );
  
CREATE TABLE FNBL_PUSH_REGISTRY_COMPLEX (
  id               BIGINT PRIMARY KEY,
  period           BIGINT,
  active           CHAR(1),
  last_update      BIGINT,
  status           VARCHAR(1),
  task_bean_file   VARCHAR(255), 
  VAR_FIELD        VARCHAR(255),
  BOOL_FIELD       BOOLEAN,
  OLD_BOOL_FIELD   CHAR(1),
  LONG_FIELD       BIGINT,
  DECIMAL_FIELD    DECIMAL,
  DOUBLE_FIELD     DOUBLE,
  INTEGER_FIELD    INTEGER,
  DATE_FIELD       DATE,
  TIME_FIELD       TIME,
  TIMESTAMP_FIELD  TIMESTAMP
 );
 
 