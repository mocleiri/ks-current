alter profile default limit password_life_time unlimited;

create user ksa identified by ksa123;
create user rice identified by rice123;
create user ks identified by ks123;

grant all privileges to ksa;
grant all privileges to rice;
grant all privileges to ks;