(ns marketentry.facts-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.facts :as facts]))

(deftest fji-has-spec-basis
  (let [sb (facts/spec-basis "FJI")]
    (is (some? sb))
    (is (string? (:provenance sb)))
    (is (seq (:required-evidence sb)))
    (is (some? (facts/corporate-number-spec-basis "FJI")))
    (is (some? (facts/procurement-threshold-spec-basis "FJI")))))

(deftest fji-rep-spec-basis-is-honestly-absent
  (testing "the Registrar of Companies' own site names a distinct Foreign Companies registration category, but the Companies Act 2015's own local-representative provision could not be confirmed at a specific section number -- deliberately not claimed"
    (is (nil? (facts/rep-spec-basis "FJI")))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "ATL")))
  (is (nil? (facts/spec-basis "ZZZ"))))

(deftest required-evidence-satisfied
  (let [sb (facts/spec-basis "FJI")
        all (:required-evidence sb)]
    (is (true? (facts/required-evidence-satisfied? "FJI" all)))
    (is (not (facts/required-evidence-satisfied? "FJI" (take 1 all))))
    (is (nil? (facts/required-evidence-satisfied? "ATL" all)))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["FJI" "USA" "ATL"])]
    (is (= 3 (:requested c)))
    (is (= 2 (:covered c)))
    (is (= ["ATL"] (:missing-jurisdictions c)))))

(deftest procurement-threshold-spec-basis-criteria
  (let [pt (facts/procurement-threshold-spec-basis "FJI")]
    (is (= 50000.0 (get-in pt [:procurement-threshold-criteria :simplified-procurement-threshold-fjd])))))
