(ns neuseg.dbn
  (:use [clojure.core.matrix :only [set-current-implementation new-vector]])
  (:import (com.guokr.dbn DBN)
           (mikera.vectorz Vectorz)))

(set-current-implementation :vectorz)

(defn- normalize [val]
  (/ (+ 1 val) 2))

(defn- vectorize [line dim]
  (Vectorz/create (double-array (map #(normalize (Double. %)) (clojure.string/split line #" ")))))

(defn create [layers]
  (DBN. (int-array layers)))

(defn pretrain [nn k lr train-file-name]
  (with-open [rdr (clojure.java.io/reader train-file-name)]
    (let [data (line-seq rdr)
          [total idm odm] (clojure.string/split (first data) #" ")]
      (doall (map #(.pretrain nn k lr (vectorize % (Integer. idm)))
                  (flatten (partition 1 2 (rest data))))))))

(defn finetune [nn lr train-file-name]
  (with-open [rdr (clojure.java.io/reader train-file-name)]
    (let [data (line-seq rdr)
          [total idm odm] (clojure.string/split (first data) #" ")
          idm (Integer. idm)
          odm (Integer. odm)]
      (doall (map #(.finetune nn lr (vectorize (first %) idm) (vectorize (second %) odm))
                  (partition 2 (rest data)))))))

(defn predict [nn input dim]
  (map #(- (* 2 (Math/round %)) 1)) (.pridict nn (vectorize input dim)))

(defn- testfun [nn idim odim]
  (fn [input output]
    (if (= (predict nn input idim) output) 1 0)))

(defn testnn [nn test-file-name]
  (with-open [rdr (clojure.java.io/reader test-file-name)]
    (let [data (line-seq rdr)
          [total idm odm] (clojure.string/split (first data) #" ")
          idm (Integer. idm)
          odm (Integer. odm)
          tfn (testfun nn idm odm)]
      (/ (reduce + (map #(tfn (vectorize (first %) idm) (vectorize (second %) odm))
                        (partition 2 (rest data)))) (Double. total)))))

(defn save [nn])
