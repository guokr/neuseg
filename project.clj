(defproject neuseg "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  
    :dependencies [[org.clojure/clojure "1.5.1"]
                   [clj-tuple "0.1.4"]
                   [clj-pid "0.1.1"]
                   [http-kit "2.1.16"]
                   [org.slf4j/slf4j-api "1.7.5"]
                   [org.slf4j/slf4j-log4j12 "1.7.5"]
                   [log4j/log4j "1.2.17"]
                   [net.mikera/core.matrix "0.20.0"]
                   [net.mikera/vectorz-clj "0.20.0"]
                   [com.googlecode.fannj/fannj "0.6"]
                   [com.guokr/clj-cn-nlp "0.2.0"]
                   [junit "4.0" :scope "test"]]

    :java-source-paths ["java"]
    :jvm-opts ["-Xmx16g"])
