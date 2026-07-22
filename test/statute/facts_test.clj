(ns statute.facts-test
  (:require [clojure.string :as str]
            [clojure.test :refer [deftest is]]
            [statute.facts :as facts]))

(deftest fji-has-spec-basis
  (let [sb (facts/spec-basis "FJI")]
    (is (= 2 (count sb)))
    (is (every? #(str/starts-with? (:statute/url %) "https://") sb))
    (is (every? :statute/law-number sb))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "ATL")))
  (is (nil? (facts/spec-basis "ZZZ"))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["FJI" "JPN" "ATL"])]
    (is (= 3 (:requested c)))
    (is (= 1 (:covered c)))
    (is (= ["ATL" "JPN"] (:missing-jurisdictions c)))))

(deftest by-topic-filters
  (is (= ["fji.companies-act-2015"]
         (mapv :statute/id (facts/by-topic "FJI" :corporate-governance))))
  (is (= ["fji.employment-relations-act-2007"]
         (mapv :statute/id (facts/by-topic "FJI" :labor))))
  (is (empty? (facts/by-topic "FJI" :data-protection))
      "no data-protection statute independently confirmed this iteration -- honestly absent, see namespace docstring")
  (is (empty? (facts/by-topic "ATL" :labor))))
