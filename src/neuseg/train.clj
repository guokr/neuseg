(ns neuseg.train
  (:require [neuseg.fann :as fann]))

(defn train []
  (let [nn         (fann/create [32 32 2] :hidden :sigmod-symmetric :output :sigmod-symmetric)
        train-data (fann/load-train-data "data/trains/train")
        test-data  (fann/load-train-data "data/trains/test")]
    (println "training...")
    (fann/train nn train-data 500000 1000 0.001)
    (println "testing...")
    (println "error = " (fann/test nn test-data))))
