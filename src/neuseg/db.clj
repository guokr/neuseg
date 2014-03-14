(ns neuseg.db
  (:use [clj-tuple]
        [clojure.core.matrix]))

(set-current-implementation :vectorz)

(def registry
  { "unigram"   (atom {})
    "bigram"    (atom {})
    "trigram"   (atom {})
    "quadgram"  (atom {}) })

(defn- enum-splited-line [data line]
  (let [[idx _ _] data
        items (clojure.string/split line #"\s+")
        wd    (first items)
        nvec  (normalise (vec (map #(Double. %1) (rest items))))]
    (if idx
      (do
        (if (= (mod idx 1000) 0) (println idx))
        (tuple (+ idx 1) wd nvec)))))

(defn- reg-wd-neglect [name data]
  (let [[idx wd nvec] data]
    (do
      (swap! assoc (get registry name) wd idx)
      nvec)))

(defn load-db [name]
  (with-open [rdr (clojure.java.io/reader (str "data" "/" name))]
    (map (partial reg-wd-neglect name)
         (reduce enum-splited-line [] (rest (line-seq rdr))))))

(def unigram  (matrix (load-db "unigram")))
(def bigram   (matrix (load-db "bigram")))
(def trigram  (matrix (load-db "trigram")))
(def quadgram (matrix (load-db "quadgram")))
