(ns patch-report.core
  ;(:require [clojure.java.shell/sh]))
  )


(def OS-COMMANDS {:rhel-7 {:hostname "hostname"
                           :kernel   ["uname" "-r"]
                           :package  ["yum" "check-update"]
                           }
                  })

;; PARSING
(defn package-info [coll]
  ;; returns human friendly package information
  (map (fn [p]
         (let [pm (clojure.string/split p #"\s+")]
           [:package (first pm) :version (second pm) :repo (last pm)])
         ) (split-packages coll)))

(defn split-packages [coll]
  ;; splits available packages from yum output
  (clojure.string/split (nth (clojure.string/split coll #"\n\n") 1) #"\n"))



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



(defn -main [os]
  (println (get-in OS-COMMANDS [:rhel-7 :os]))
  )

