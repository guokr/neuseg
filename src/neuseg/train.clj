(ns neuseg.train
  (:require [neuseg.fann :as fann]))

(defn train []
  (let [nn         (fann/create [32 32 32 2] :hidden :sigmod-symmetric :output :sigmod-symmetric)
        train-data (fann/load-train-data "data/trains/train")
        test-data  (fann/load-train-data "data/trains/tests")]
    (println "training...")
    (fann/train nn train-data 100 1 0.01)
    (println "testing...")
    (println "error = " (fann/testnn nn test-data))
    (fann/save nn "data/models/tagging.nn")))
