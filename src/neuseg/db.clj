(ns neuseg.db
  (:use [clj-tuple]
        [clojure.core.matrix]))

(set-current-implementation :vectorz)

(def registry
  { "unigram"   (atom {})
    "bigram"    (atom {})
    "trigram"   (atom {})
    "quadgram"  (atom {}) })

(defn- splited-line [line]
  (let [items (clojure.string/split line #"\s+")
        wd    (first items)
        nvec  (normalise (vec (map #(Double. %1) (rest items))))]
        (tuple wd nvec)))

(defn- reg-wd-neglect [name data]
  (let [[[wd nvec] idx] data]
    (if (> idx -1)
      (do
        (swap! assoc (get registry name) wd idx)
        nvec))))

(defn load-db [name]
  (with-open [rdr (clojure.java.io/reader (str "data" "/" name))]
    (map (partial reg-wd-neglect name)
         (partition 2 
           (interleave (map splited-line (rest (line-seq rdr)))
                       (iterate inc 1))))))

(def unigram  (matrix (load-db "unigram")))
(def bigram   (matrix (load-db "bigram")))
(def trigram  (matrix (load-db "trigram")))
(def quadgram (matrix (load-db "quadgram")))
