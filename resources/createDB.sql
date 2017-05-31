CREATE DATABASE patch_mgmt;


CREATE TABLE reports (
  id SERIAL,
  hostname VARCHAR UNIQUE NOT NULL,
  kernel VARCHAR,
  patches VARCHAR,
  last_update TIMESTAMP DEFAULT NOW()
  );
