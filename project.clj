(defproject patch-report "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/java.jdbc "0.6.1"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]]
  :main patch-report.core
  ;:aot [patch-report.core]
  :uberjar-exclusions [#"user.clj"]
  :uberjar-name "patch-report-agent.jar"
  :uberjar {:aot :all}

  )
