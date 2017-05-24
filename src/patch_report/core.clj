(ns patch-report.core

  ;(:require [clojure.java.shell/sh]))

  )


(def OS-COMMANDS {:rhel-7 {



                           }





                  })



(defn -main [os]
  (clojure.java.shell/sh "ls" "-a" "/Users/jac0p/Desktop")
  )

