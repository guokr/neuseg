(ns neuseg.dbn
  (:use [clojure.core.matrix :only [set-current-implementation
                                    new-vector dimensionality]])
  (:import (com.guokr.dbn DBN)
           (mikera.vectorz Vectorz)))

(set-current-implementation :vectorz)

(defn- normalize [val]
  (/ (+ 1 val) 2))

(defn- vectorize [line dim]
  (Vectorz/create (double-array (map #(normalize (Double. %)) (clojure.string/split line #" ")))))

(defn- int-seq [line]
  (map #(Integer. %) (clojure.string/split line #" ")))

(defn create [layers]
  (DBN. (int-array layers)))

(defn pretrain [nn k lr train-file-name]
  (with-open [rdr (clojure.java.io/reader train-file-name)]
    (let [data (line-seq rdr)
          [total idm odm] (clojure.string/split (first data) #" ")
          idm (Integer. idm)]
      (doseq [line (flatten (partition 1 2 (rest data)))]
        (if (> (count line) idm)
          (.pretrain nn k lr (vectorize line idm)))))))

(defn finetune [nn lr train-file-name]
  (with-open [rdr (clojure.java.io/reader train-file-name)]
    (let [data (line-seq rdr)
          [total idm odm] (clojure.string/split (first data) #" ")
          idm (Integer. idm)
          odm (Integer. odm)]
      (doseq [dlines (partition 2 (rest data))]
        (let [lndata (first dlines)
              lntest (second dlines)]
          (if (and (> (count lndata) idm) (> (count lntest) odm))
            (.finetune nn lr (vectorize lndata idm) (vectorize lntest odm))))))))

(defn predict [nn input]
    (map #(- (* 2 (Math/round %)) 1) (.pridict nn input)))

(defn- testfun [nn]
  (fn [input output]
    (if (= (predict nn input) output) 1 0)))

(defn testnn [nn test-file-name]
  (with-open [rdr (clojure.java.io/reader test-file-name)]
    (let [data (line-seq rdr)
          [total idm odm] (clojure.string/split (first data) #" ")
          idm (Integer. idm)
          tfn (testfun nn)]
      (/ (reduce + (map #(tfn (vectorize (first %) idm) (int-seq (second %)))
                        (partition 2 (rest data)))) (Double. total)))))

(defn save [nn])
