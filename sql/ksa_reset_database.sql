
set echo on

whenever sqlerror continue

@ksa_drop_ddl.sql

whenever sqlerror exit failure

set sqlblanklines on

@ksa_create_ddl.sql

@ksa_config_dml.sql

@test_inserts/ksa_test_data_dml.sql

set sqlterminator '!'

@ksa_load_rules_dml.sql
@ksa_load_fm_rules_dml.sql

set sqlterminator ';'

@ksa_constraints_ddl.sql

commit;

whenever sqlerror continue
