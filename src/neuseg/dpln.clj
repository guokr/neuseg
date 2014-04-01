(ns neuseg.dpln
  (:import (java.util Date)
           (org.apache.commons.math3.random MersenneTwister)
           (org.deeplearning4j.dbn CDBN$Builder)
           (org.jblas DoubleMatrix)))

(defn- normalize [val]
  (/ (+ 1 val) 2))

(defn- vectorize [line]
  (map #(normalize (Double. %)) (clojure.string/split line #" ")))

(defn- labelize [line]
  (map #(normalize (Integer. %)) (clojure.string/split line #" ")))

(defn- int-seq [line]
  (map #(Integer. %) (clojure.string/split line #" ")))

(defn- to-matrix [rows cols row-based-data]
  (.transpose (DoubleMatrix. cols rows (double-array row-based-data))))

(defn- from-matrix [m]
  (vec (.toArray (.transpose m))))

(defn create [layers]
 (let [builder (doto (CDBN$Builder.)
                 (.numberOfInputs (first layers))
                 (.numberOfOutPuts (last layers))
                 (.hiddenLayerSizes (int-array (rest (pop layers))))
                 (.useRegularization false)
                 (.withRng (MersenneTwister. (.getTime (Date.))))
                 (.withL2 0.1))]
    (.build builder)))

(defn pretrain [nn k lr epochs train-file-name]
  (with-open [rdr (clojure.java.io/reader train-file-name)]
    (let [data (line-seq rdr)
          [total idm odm] (clojure.string/split (first data) #" ")
          total (Integer. total)
          idm (Integer. idm)
          train-data (flatten (map vectorize (flatten (partition 1 2 (rest data)))))]
      (.pretrain nn (to-matrix total idm train-data) k lr epochs))))

(defn finetune [nn lr epochs train-file-name]
  (with-open [rdr (clojure.java.io/reader train-file-name)]
    (let [data (line-seq rdr)
          [total idm odm] (clojure.string/split (first data) #" ")
          total (Integer. total)
          idm (Integer. idm)
          odm (Integer. odm)
          train-label (flatten (map labelize (flatten (partition 1 2 (nthrest data 2)))))]
    (.finetune nn (to-matrix total odm train-label) lr epochs))))

(defn predict [nn input]
  (let [dim (count input)
        test-data (to-matrix 1 dim input)]
    (map #(- (* 2 (Math/round %)) 1) (from-matrix (.predict nn test-data)))))

(defn- testfun [nn]
  (fn [input output]
    (if (= (predict nn input) output) 1 0)))

(defn testnn [nn test-file-name]
  (with-open [rdr (clojure.java.io/reader test-file-name)]
    (let [data (line-seq rdr)
          [total idm odm] (clojure.string/split (first data) #" ")
          idm (Integer. idm)
          tfn (testfun nn)]
      (/ (reduce + (map #(tfn (vectorize (first %)) (int-seq (second %)))
                        (partition 2 (rest data)))) (Double. total)))))

(defn save [nn])
