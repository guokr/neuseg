(ns neuseg.train
  (:require [neuseg.dbn :as dbn]))

(defn dtrain []
  (let [nn (dbn/create [112 128 32 2])]
    (println "pretrain...")
    (time (dbn/pretrain nn 1 0.01 "data/trains/train"))
    (println "finetune...")
    (time (dbn/finetune nn 0.1 "data/trains/train"))
    (println "testing...")
    (time (println "success = " (dbn/testnn nn "data/trains/tests")))))
