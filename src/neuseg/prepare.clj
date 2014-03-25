(ns neuseg.prepare
  (:use [clj-tuple]
        [clojure.java.io]
        [neuseg.db]
        [com.guokr.nlp.seg])
  (:import [com.guokr.neuseg.util NGram]))

(defn- format-case [input output]
  (clojure.string/join "\n" (map (partial clojure.string/join " ") [input output])))

(defn- clean [text]
  (clojure.string/replace text #"(\s|\u00a0)+" " "))

(defn tagging-train [text]
  (let [seged (seg text)]
    (loop [raw text
           sgd seged
           ret [1]]
      (if (nil? (second raw)) (conj ret 1)
        (if (= (second raw) (second sgd))
          (if (= (second raw) " ")
            (recur (nthrest raw 2) (nthrest sgd 2) (conj ret 1 1 1 1))
            (recur (rest raw) (rest sgd) (conj ret -1 -1)))
          (recur (rest raw) (nthrest sgd 2) (conj ret 1 1)))))))

(defn gen-cases [text]
  (str (clojure.string/join "\n" 
         (map format-case (zip (map mdot (neighbors 4 unizero  (map get-vector (iterator-seq (NGram/unigram text)))))
                               (map mdot (neighbors 4 bizero   (map get-vector (iterator-seq (NGram/bigram text)))))
                               (map mdot (neighbors 4 trizero  (map get-vector (iterator-seq (NGram/trigram text)))))
                               (map mdot (neighbors 4 quadzero (map get-vector (iterator-seq (NGram/quadgram text))))))
                               (partition 2 (tagging-train text)))) "\n"))

(def counter (atom 0))

(defn gen-train [file-corpus file-output]
  (reset! counter 0)
  (with-open [wrtr (writer (str file-output "-body") :encoding "utf-8")]
    (with-open [rdr (reader file-corpus :encoding "utf-8")]
      (doseq [line (line-seq rdr)]
        (let [cline (clean line)]
          (swap! counter + (count cline))
          (.write wrtr (gen-cases cline))))))
  (with-open [wrtr (writer file-output :encoding "utf-8")]
    (.write wrtr (str @counter " 32 2\n"))
    (with-open [rdr (reader (str file-output "-body") :encoding "utf-8")]
      (doseq [line (line-seq rdr)]
        (.write wrtr (str line "/n")))))
  (delete-file (str file-output "-body")))

(defn prepare []
  (gen-train "data/corpus/corpus" "data/trains/train")
  (gen-train "data/corpus/tests" "data/trains/tests"))

