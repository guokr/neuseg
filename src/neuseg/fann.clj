(ns neuseg.fann
  (:require [net.n01se.clojure-jna :as jna])
  (:import (com.sun.jna Pointer)))

;; bindings

(System/setProperty "jna.library.path" "/usr/local/lib")

(def *create-standard*
  (jna/to-fn Pointer fann/fann_create_standard))

(def *set-activiation-hidden*
  (jna/to-fn Integer fann/fann_set_activation_function_hidden))

(def *set-activiation-output*
  (jna/to-fn Integer fann/fann_set_activation_function_output))

(def *train-on-data*
  (jna/to-fn Integer fann/fann_train_on_data))

(def *test-data*
  (jna/to-fn Flaot fann/fann_test_data))

(def *save*
  (jna/to-fn Integer fann/fann_save))

(def *destroy*
  (jna/to-fn Integer fann/fann_destroy))

(def *read-train-from-file*
  (jna/to-fn Pointer fann/fann_read_train_from_file))

(def *destroy-train*
  (jna/to-fn Integer fann/fann_destroy_train))

;; constants

(def activiation-function-map {
	:linear 0
  :threshold 1
  :threshold-symmetric 2
  :sigmod 3
  :sigmod-stepwise 4
  :sigmod-symmetric 5
  :sigmod-symmetric-stepwise 6
  :gaussian 7
  :gaussian-symmetric 8
  :gaussian-stepwise 9
  :elliot 10
  :elliot-symmetric 11
  :linear-piece 12
  :linear-piece-symmetric 13
  :sin-symmetric 14
  :cos-symmetric 15
  :sin 16
  :cos 17 })

;; apis

(defn create [layers & opts]
  (let [params (cons (count layers) layers)
        fann   (apply *create-standard* params)]
    (if-let [hidden (:hidden opt)]
      (*set-activiation-hidden* fann (get activiation-function-map hidden)))
    (if-let [output (:output opt)]
      (*set-activiation-output* fann (get activiation-function-map output)))
    fann))

(defn load-train-data [train-file-name]
    (*read-train-from-file* train-file-name))

(defn train [fann train-data max-epochs epochs-between-reports desired-error]
  (*train-on-data* fann train-data max-epochs epochs-between-reports desired-error))

(defn test [fann train-data]
  (*test-data* fann train-data))



