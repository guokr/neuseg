(ns neuseg.seg
  (:use [neuseg.db])
  (:require [neuseg.fann :as fann]))

(def nn (fann/loadnn "data/models/tagging.nn"))

(def tagging (fann/mk-invoke-fn nn 2))

(defn untagging [triple]
  (let [[hd tl ch] triple]
    (if (>= tl  0)
      (str ch " ")
      (str ch))))

(defn seg [text]
  (clojure.string/join ""
    (map untagging (zip (map tagging (vectorize text)) (vec text)))))