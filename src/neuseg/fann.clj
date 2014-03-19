(ns neuseg.fann
  (:require [net.n01se.clojure-jna :as jna])
  (:import (com.sun.jna Pointer)))

;; bindings

(System/setProperty "jna.library.path" "/usr/local/lib")

(def ^:private -create-standard
  (jna/to-fn Pointer fann/fann_create_standard))

(def ^:private -create-from-file
  (jna/to-fn Pointer fann/fann_create_from_file))

(def ^:private -set-activiation-hidden
  (jna/to-fn Integer fann/fann_set_activation_function_hidden))

(def ^:private -set-activiation-output
  (jna/to-fn Integer fann/fann_set_activation_function_output))

(def ^:private -train-on-data
  (jna/to-fn Integer fann/fann_train_on_data))

(def ^:private -test-data
  (jna/to-fn Float fann/fann_test_data))

(def ^:private -save
  (jna/to-fn Integer fann/fann_save))

(def ^:private -run
  (jna/to-fn Pointer fann/fann_run))

(def ^:private -destroy
  (jna/to-fn Integer fann/fann_destroy))

(def ^:private -read-train-from-file
  (jna/to-fn Pointer fann/fann_read_train_from_file))

(def ^:private -destroy-train
  (jna/to-fn Integer fann/fann_destroy_train))

;; constants

(def activiation-function-map {
	:linear 0
  :threshold 2
  :threshold-symmetric 3
  :sigmoid 4
  :sigmoid-stepwise 5
  :sigmoid-symmetric 6
  :sigmoid-symmetric-stepwise 7
  :gaussian 8
  :gaussian-symmetric 9
  :gaussian-stepwise 10
  :elliot 11
  :elliot-symmetric 12
  :linear-piece 13
  :linear-piece-symmetric 14
  :sin-symmetric 15
  :cos-symmetric 16
  :sin 17
  :cos 18 })

;; apis

(defn loadnn [file-name]
  (-create-from-file file-name))

(defn create [layers & opts]
  (let [optmap (apply hash-map opts)
        params (cons (count layers) layers)
        fann   (apply -create-standard params)]
    (if-let [hidden (:hidden optmap)]
        (-set-activiation-hidden fann (get activiation-function-map hidden)))
    (if-let [output (:output optmap)]
      (-set-activiation-output fann (get activiation-function-map output)))
    fann))

(defn load-train-data [train-file-name]
    (-read-train-from-file train-file-name))

(defn destroy-train-data [train-data]
    (-destroy-train train-data))

(defn train [fann train-data max-epochs epochs-between-reports desired-error]
  (-train-on-data fann train-data max-epochs epochs-between-reports desired-error))

(defn testnn [fann train-data]
  (-test-data fann train-data))

(defn save [fann file-name]
  (-save fann file-name))

(defn destroy [fann]
  (-destroy fann))

(defn mk-invoke-fn [fann out-dim]
  (fn [input]
    (vec (.getFloatArray (-run fann (float-array input)) 0 out-dim))))


