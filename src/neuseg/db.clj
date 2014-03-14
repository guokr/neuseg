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

(defn- process-line [vecmodel name data]
  (let [[[wd nvec] idx] data]
      (do
        (if (= (mod idx 1000) 0) (print "."))
        (swap! (get registry name) assoc wd idx)
        (set-row! vecmodel idx nvec))))

(defn load-db [name]
  (println "")
  (println "loading" name "started!")
  (with-open [rdr (clojure.java.io/reader (str "data" "/" name))]
    (let [data  (line-seq rdr)
          declr (first data)
          [size dim] (map #(Integer. %) (clojure.string/split line #"\s+"))
          last (atom -1)
          vecmodel (new-matrix size dim)]
          (dorun
            (map (partial process-line vecmodel name)
              (partition-by #(if (number? %1) (reset! last %1) (+ @last 1))
                (interleave (map splited-line data)
                            (iterate inc 0)))))
          vecmodel)))

(def unigram  (load-db "unigram"))
(def bigram   (load-db "bigram"))
(def trigram  (load-db "trigram"))
(def quadgram (load-db "quadgram"))
