(ns neuseg.db
  (:use [clj-tuple]
        [clojure.core.matrix :only [set-current-implementation
                                    new-matrix set-row! get-row
                                    zero-vector normalise]]))

(set-current-implementation :vectorz)

(def indexes
  { "unigram"   (atom {})
    "bigram"    (atom {})
    "trigram"   (atom {})
    "quadgram"  (atom {}) })

(def dimensions
  { "unigram"   (atom 0)
    "bigram"    (atom 0)
    "trigram"   (atom 0)
    "quadgram"  (atom 0) })

(defn- splited-line [line]
  (let [items (clojure.string/split line #"\s+")
        wd    (first items)
        nvec  (normalise (vec (map #(Double. %1) (rest items))))]
        (tuple wd nvec)))

(defn- process-line [vecmodel name data]
  (let [[[wd nvec] idx] data]
      (do
        (swap! (get indexes name) assoc wd idx)
        (set-row! vecmodel idx nvec))))

(defn load-db [name]
  (println "loading" name "started......")
  (with-open [rdr (clojure.java.io/reader (str "data/models/" name ".vec"))]
    (let [data  (line-seq rdr)
          declr (first data)
          [size dim] (map #(Integer. %) (clojure.string/split declr #"\s+"))
          last (atom -1)
          vecmodel (new-matrix size dim)]
          (reset! (get dimensions name) dim)
          (dorun
            (map (partial process-line vecmodel name)
              (partition-by #(if (number? %1) (reset! last %1) (+ @last 1))
                (interleave (map splited-line (rest data))
                            (iterate inc 0)))))
          vecmodel)))

(def unigram  (load-db "unigram"))
(def bigram   (load-db "bigram"))
(def trigram  (load-db "trigram"))
(def quadgram (load-db "quadgram"))

(def ngrams { "unigram" unigram
              "bigram" bigram
              "trigram" trigram
              "quadgram" quadgram })

(defn- retrieve-vector [name s]
  (let [idx (get @(get indexes name) s)
        dim @(get dimensions name)
        ngram (get ngrams name)]
    (if idx (get-row ngram idx) (zero-vector dim))))

(defn get-vector [s]
  (let [wd (.toString s)]
    (case (.length s)
      1 (retrieve-vector "unigram" wd)
      2 (retrieve-vector "bigram" wd)
      3 (retrieve-vector "trigram" wd)
      4 (retrieve-vector "quadgram" wd)
      (zero-vector 0))))

(def unizero  (get-vector " "))
(def bizero   (get-vector "  "))
(def trizero  (get-vector "   "))
(def quadzero (get-vector "    "))

(defn neighbors [radius elem-fill coll]
  (let [window (+ 1 (* 2 radius))
        head   (repeat radius elem-fill)
        tail   (repeat radius elem-fill)
        merged (concat head coll tail)]
    (partition window 1 merged)))

(defn- mdot [vecs]
  (let [mid 4
        mid-vec (nth vecs mid)]
    (map (partial dot mid-vec) (concat (take mid vecs) (drop (inc mid) vecs)))))

(defn- zip [& colls]
  (map flatten (partition (count colls) (apply interleave colls))))

(defn vectorize [text]
  (zip (map mdot (neighbors 4 unizero  (map get-vector (iterator-seq (NGram/unigram text)))))
                            (map mdot (neighbors 4 bizero   (map get-vector (iterator-seq (NGram/bigram text)))))
                            (map mdot (neighbors 4 trizero  (map get-vector (iterator-seq (NGram/trigram text)))))
                            (map mdot (neighbors 4 quadzero (map get-vector (iterator-seq (NGram/quadgram text)))))))