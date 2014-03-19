(ns neuseg.train
  (:import (com.googlecode.fannj Fann
                                 Layer
                                 Trainer
                                 ActivationFunction))

(defn train []
  (let [inputLayer  (Layer/create 32 ActivationFunction/FANN_SIGMOID_SYMMETRIC)
        hiddenLayer (Layer/create 32 ActivationFunction/FANN_SIGMOID_SYMMETRIC)
        outLayer    (Layer/create 2 ActivationFunction/FANN_SIGMOID_SYMMETRIC)
        fann        (Fann. [inputLayer hiddenLayer outLayer])
        trainer     (Trainer. fann)]
    (println "training...")
    (.train trainer "data/trains/train" 500000 1000 0.001)
    (println "testing...")
    (println "error = " (.test trainer "data/trains/tests"))))
