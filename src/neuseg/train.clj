(ns neuseg.train
  (:require [neuseg.dbn :as dbn]
            [neuseg.dpln :as dpln]))

(defn dtrain []
  (let [nn (dbn/create [112 128 32 2])]
    (println "pretrain...")
    (time (dbn/pretrain nn 1 0.01 "data/trains/train"))
    (println "finetune...")
    (time
      (do
        (dbn/finetune nn 0.01 "data/trains/train")
        (dbn/finetune nn 0.01 "data/trains/train")
        (dbn/finetune nn 0.01 "data/trains/train")
        (dbn/finetune nn 0.01 "data/trains/train")
        (dbn/finetune nn 0.01 "data/trains/train")))
    (println "testing...")
    (time (println "success = " (dbn/testnn nn "data/trains/tests")))))


(defn ntrain []
  (let [nn (dpln/create [112 128 32 2])]
    (println "pretrain...")
    (time (dpln/pretrain nn 1 0.01 100 "data/trains/train"))
    (println "finetune...")
    (time
      (do
        (dpln/finetune nn 0.01 100 "data/trains/train")
    (println "testing...")
    (time (println "success = " (dbn/testnn nn "data/trains/tests")))))

