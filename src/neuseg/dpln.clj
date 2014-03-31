(ns neuseg.dpln
  (:import (org.apache.commons.math3.random MersenneTwister)
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

(defn create [layers]
  (doto (CDBN$Builder.)
    (.numberOfInputs (first layers))
    (.numberOfOutPuts (last layers))
    (.hiddenLayerSizes (int-array (pop (rest layers))))
    (.useRegularization false)
    (.withRng (MersenneTwister. 123))
    (.withL2 0.1)
    (.renderWeights 1000)
    (.build)))

(defn pretrain [nn k lr epochs train-file-name]
  (with-open [rdr (clojure.java.io/reader train-file-name)]
    (let [data (line-seq rdr)
          [total idm odm] (clojure.string/split (first data) #" ")
          total (Integer. total)
          idm (Integer. idm)
          train-data (double-array (flatten (map vectorize (flatten (partition 1 2 (rest data))))))]
      (.pretrain nn (DoubleMatrix. total idm train-data) k lr epochs))))

(defn finetune [nn lr epochs train-file-name]
  (with-open [rdr (clojure.java.io/reader train-file-name)]
    (let [data (line-seq rdr)
          [total idm odm] (clojure.string/split (first data) #" ")
          idm (Integer. idm)
          odm (Integer. odm)
          train-label (double-array (flatten (map labelize (flatten (partition 1 2 (nthrest data 2))))))]
    (.finetune nn (DoubleMatrix. total idm train-label) lr epochs))))

(defn predict [nn input]
  (let [dim (count input)
        test-data (DoubleMatrix. 1 dim (double-array input))]
    (map #(- (* 2 (Math/round %)) 1) (vec (.toArray (.predict nn test-data))))))

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
