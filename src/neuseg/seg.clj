(ns neuseg.seg
  (:use [neuseg.db])
  (:require [neuseg.fann :as fann]))

(def nn (fann/loadnn "data/models/tagging.nn"))

(def tagging (fann/mk-invoke-fn nn 2))

(defn untagging [pair]
  (let [[tag ch] pair]
    (if (>= (first tag)  0)
      (if (>= (second tag)  0)
        (str " " ch " ")
        (str " " ch))
      (if (>= (second tag)  0)
        (str ch " ")
        (str ch)))))

(defn seg [text]
  (map untagging (zip (map tagging (vectorize text)) (vec text))))