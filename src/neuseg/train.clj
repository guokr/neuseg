(ns neuseg.train
  (:require [neuseg.fann :as fann]))

(defn train []
  (let [nn         (fann/create [32 64 16 2] :hidden :sigmoid-symmetric :output :sigmoid-symmetric)
        train-data (fann/load-train-data "data/trains/train")
        test-data  (fann/load-train-data "data/trains/tests")]
    (println "training...")
    (fann/train nn train-data 100 1 0.001)
    (println "testing...")
    (println "error = " (fann/testnn nn test-data))
    (fann/save nn "data/models/tagging.nn")))

(defn sainity-test []
  (let [nn         (fann/create [2 3 3 1] :hidden :sigmoid-symmetric :output :sigmoid-symmetric)
        train-data (fann/load-train-data "data/trains/xor")
        test-data  (fann/load-train-data "data/trains/xor")]
    (println "training...")
    (fann/train nn train-data 1000 1 0.001)
    (println "testing...")
    (println "error = " (fann/testnn nn test-data))
    (fann/save nn "data/models/tagging.nn")))
