(ns neuseg.train
  (:use [clj-tuple]
        [clojure.core.matrix :only [dot zero-vector]]
        [neuseg.db]
        [com.guokr.nlp.seg]
        [clojure.string :only [join]])
  (:import (com.guokr.neuseg.util NGram
                                  NeighborSlider)))

(defn- ndot [vecs]
  (let [mid 4
        mid-vec (nth vecs mid)]
    (map (partial dot mid-vec) (concat (take mid vecs) (drop (inc mid) vecs)))))

(defn- zip [& colls]
  (partition (count colls) (apply interleave colls)))

(defn- tformat [coll]
  (let [row (flatten coll)]
    (str (join " " (take 32 row) "\n" (join " " (nthrest 32 row)) "\n"))))

(defn tagging [text]
  (let [seged (seg text)]
		(loop [raw text
           sgd seged
           ret [1]]
      (if (nil? (second raw)) (conj ret 1)
        (if (= (second raw) (second sgd))
          (recur (rest raw) (rest sgd) (conj ret -1 -1))
          (recur (rest raw) (nthrest sgd 2) (conj ret 1 1)))))))

(defn gen-case [text]
  (map tformat (zip (map ndot (NeighborSlider. 4 (get-vector " ") (map get-vector (iterator-seq (NGram/unigram text)))))
                    (map ndot (NeighborSlider. 4 (get-vector "  ") (map get-vector (iterator-seq (NGram/bigram text)))))
                    (map ndot (NeighborSlider. 4 (get-vector "   ") (map get-vector (iterator-seq (NGram/trigram text)))))
                    (map ndot (NeighborSlider. 4 (get-vector "    ") (map get-vector (iterator-seq (NGram/quadgram text)))))
                    (partition 2 (tagging text)))))
