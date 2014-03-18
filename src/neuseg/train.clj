(ns neuseg.train
  (:use [clj-tuple]
        [clojure.core.matrix :only [dot zero-vector]]
        [neuseg.db]
        [com.guokr.nlp.seg])
  (:import (com.guokr.neuseg.util NGram
                                  Neighbors)))

(defn- mdot [vecs]
  (let [mid 4
        mid-vec (nth vecs mid)]
    (map (partial dot mid-vec) (concat (take mid vecs) (drop (inc mid) vecs)))))

(defn- zip [& colls]
  (map flatten (partition (count colls) (apply interleave colls))))

(defn- format-case [input output]
  (clojure.string/join "\n" (map (partial clojure.string/join " ") [input output])))

(defn tagging [text]
  (let [seged (seg text)]
		(loop [raw text
           sgd seged
           ret [1]]
      (if (nil? (second raw)) (conj ret 1)
        (if (= (second raw) (second sgd))
          (recur (rest raw) (rest sgd) (conj ret -1 -1))
          (recur (rest raw) (nthrest sgd 2) (conj ret 1 1)))))))

(defn gen-cases [text]
  (clojure.string/join "\n" 
    (map format-case (zip (map mdot (Neighbors/slider 4 unizero  (map get-vector (iterator-seq (NGram/unigram text)))))
                          (map mdot (Neighbors/slider 4 bizero   (map get-vector (iterator-seq (NGram/bigram text)))))
                          (map mdot (Neighbors/slider 4 trizero  (map get-vector (iterator-seq (NGram/trigram text)))))
                          (map mdot (Neighbors/slider 4 quadzero (map get-vector (iterator-seq (NGram/quadgram text))))))
                          (partition 2 (tagging text)))))

(defn gen-train [file-corpus file-output]
  (with-open [wrtr (writer file-output {:encoding "utf-8"})]
    (with-open [rdr (reader file-corpus {:encoding "utf-8"})]
      (let [text (clojure.string/join "" (line-seq rdr))]
        (.write wrtr (str (count text) " 32 2\n"))
        (.write wrtr (gen-cases text))))))

(defn prepare []
  (gen-train "data/corpus/corpus" "data/trains/train"))

