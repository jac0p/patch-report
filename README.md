# patch-report

A script designed to report server patch versions.

## Usage

java -jar patch-report-agent.jar DBCONFIG

#### Sample db config
```edn
{:dbtype   "postgresql"
 :dbname   "patch_mgmt"
 :host     "localhost"
 :user     "dbuser"
 :password "12345"}
```


