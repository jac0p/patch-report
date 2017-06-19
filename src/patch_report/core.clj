(ns patch-report.core
  (require [clojure.java.jdbc :as sql]
           [clojure.java.shell]
           [clojure.edn :as edn])
  (:gen-class))


;; PARSING
(defn split-packages [coll]
  ;; splits available packages from yum output
  (nth (clojure.string/split coll #"\n\n") 1))



(defn package-info [coll]
  ;; returns human friendly package information
  (map (fn [p]
         (let [pm (clojure.string/split p #"\s+")]
           [:package (first pm) :version (second pm) :repo (last pm)])
         ) (split-packages coll)))



;; OS calls
(defn hostname []
  ;; returns hostname of machine
  (println "checking hostname")
  (clojure.java.shell/sh "hostname"))

(defn kernel-version []
  ;; returns kernel version of machine
  (println "checking kernel")
  (clojure.java.shell/sh "uname" "-r"))

(defn check-update []
  ;; returns available yum updates
  (println "checking updates")
  (clojure.java.shell/sh "yum" "check-update"))



(defn -main [db-spec]
  (println "Please run patch report script with the following parameters:")
  (println "java -jar patch-report.jar <DBCONFIG>\n")

  (print "Creating patch report..")
  (let [os-report (hash-map :hostname (clojure.string/trim-newline (:out (hostname)))
                            :kernel (clojure.string/trim-newline (:out (kernel-version)))
                            :patches (split-packages (clojure.string/trim-newline (:out (check-update))))
                            ;:patches "gcc++ v12.34343.34"
                            :last_update (java.sql.Timestamp. (.getTime (java.util.Date.))))]
    (println "Done!")

    (println (str "hostname: " (:hostname os-report)))
    (println (str "kernel: " (:kernel os-report)))
    (println (str "patches:\n" (:patches os-report)))

    (print "Sending report to database..")

    ;; send data here
    (let [DB (edn/read-string (slurp db-spec))
          hostname (:hostname os-report)
          query (str "SELECT count(*) FROM reports WHERE hostname = '" hostname "'")
          count (:count (into {} (sql/query DB [query])))]

      (if (= count 0)
        (sql/insert! DB :reports os-report)
        (sql/update! DB :reports os-report ["hostname = ?" hostname])))

    ;(println (sql/query DB ["SELECT * FROM reports"]))
    (println "Done!"))
  
  
  (System/exit 0))

