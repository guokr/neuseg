(ns neuseg.seg
  (:use [neuseg.db])
  (:require [neuseg.fann :as fann]))

(def nn (fann/loadnn "data/models/tagging.nn"))

(def tagging (mk-invoke-fn nn 2))

(defn seg [text]
  (zip (map #(second (tagging %)) (vectorize text)) (vec text))