(ns neuseg.train
  (:use [clj-tuple]
        [clojure.core.matrix]
        [neuseg.db]
        [com.guokr.nlp.seg]
        [clojure.string (:only join)])
  (:import [com.guokr.neuseg.util.NGram]
           [com.guokr.neuseg.util.NeighborSlider]))

(defn ndot [vecs n]
  (let [mid (/ (dec n) 2)
        mid-vec (nth vecs mid)]
    (map (partial dot mid-vec) (concat (take mid vecs) (drop (inc mid) vecs)))))

(defn tagging [text]
  (let [seged (seg text)]
		(loop [raw text
           sgd seged
           ret [1]]
      (if (nil? (second raw)) (conj ret 1)
        (if (= (second raw) (second sgd))
          (recur (rest raw) (rest sgd) (conj ret -1 -1))
          (recur (rest raw) (nthrest sgd 2) (conj ret 1 1)))))))

(defn zip [& colls]
  (partition (count colls) (apply interleave colls)))

(defn format [coll]
  (let [row (flattern coll)]
    (str (join " " (take 32 row) "\n" (join " " (nthrest 32 row)) "\n"))))

(defn gen-case [text]
  (map format (zip (map ndot (NeighborSlider. 4 (zero-vector 16) (iter-seq (NGram/unigram text))))
                   (map ndot (NeighborSlider. 4 (zero-vector 32) (iter-seq (NGram/bigram text))))
                   (map ndot (NeighborSlider. 4 (zero-vector 48) (iter-seq (NGram/trigram text))))
                   (map ndot (NeighborSlider. 4 (zero-vector 48) (iter-seq (NGram/quadgram text))))
                   (partition 2 (tagging text)))))
