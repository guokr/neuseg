(ns neuseg.seg
  (:use [neuseg.db])
  (:require [neuseg.fann :as fann]))

(def nn (fann/loadnn "data/models/tagging.nn"))

(def tagging (fann/mk-invoke-fn nn 2))

(defn untagging [triple]
  (let [[hd tl ch] triple]
    (if (>= hd  0)
      (if (>= tl  0)
        (str " " ch " ")
        (str " " ch))
      (if (>= tl  0)
        (str ch " ")
        (str ch)))))

(defn seg [text]
  (map untagging (zip (map tagging (vectorize text)) (vec text))))