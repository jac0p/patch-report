(ns patch-report.core
  (require [clojure.java.jdbc :as sql])
  (:gen-class))


(def DB {:dbtype "postgresql"
         :dbname "patch_mgmt"
         :host   "localhost"})


;; PARSING
(defn split-packages [coll]
  ;; splits available packages from yum output
  (clojure.string/split (nth (clojure.string/split coll #"\n\n") 1) #"\n"))



(defn package-info [coll]
  ;; returns human friendly package information
  (map (fn [p]
         (let [pm (clojure.string/split p #"\s+")]
           [:package (first pm) :version (second pm) :repo (last pm)])
         ) (split-packages coll)))



;; OS calls
(defn hostname []
  ;; returns hostname of machine
  (clojure.java.shell/sh "hostname"))

(defn kernel-version []
  ;; returns kernel version of machine
  ;(clojure.java.shell/sh (nth (get-in OS-COMMANDS [:rhel-7 :os]) 0))
  (clojure.java.shell/sh "uname" "-r"))

(defn check-update []
  ;; returns available yum updates
  (clojure.java.shell/sh "yum" "check-update"))



(defn -main [db-user db-pass]
  (println "Please run patch report script with the following parameters:")
  (println "java -jar patch-report.jar <DBUSER> <DBPASS>\n")

  (print "Creating patch report..")
  (let [os-report (hash-map :hostname (clojure.string/trim-newline (:out (hostname)))
                            :kernel (clojure.string/trim-newline (:out (kernel-version)))
                            :patches (clojure.string/trim-newline (package-info (:out (check-update))))
                            ;:patches "gcc++ v12.34343.34"
                            :last_update (java.sql.Timestamp. (.getTime (java.util.Date.))))]
    (println "Done!")

    (print "Sending report to database..")

    ;; send data here
    (let [DB (merge DB {:user db-user :password db-pass})
          hostname (:hostname os-report)
          query (str "SELECT count(*) FROM reports WHERE hostname = '" hostname "'")
          count (:count (into {} (sql/query DB [query])))]

      (if (= count 0)
        (sql/insert! DB :reports os-report)
        (sql/update! DB :reports os-report ["hostname = ?" hostname])))

    ;(println (sql/query DB ["SELECT * FROM reports"]))
    (println "Done!")))

