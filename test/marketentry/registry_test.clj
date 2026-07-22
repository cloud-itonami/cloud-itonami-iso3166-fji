(ns marketentry.registry-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.registry :as registry]))

(deftest engagement-fee-recompute
  (let [e {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12 :claimed-fee 860000.0}]
    (is (== 860000.0 (registry/compute-engagement-fee e)))
    (is (true? (registry/engagement-fee-matches-claim? e))))
  (let [bad {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12 :claimed-fee 999000.0}]
    (is (false? (registry/engagement-fee-matches-claim? bad)))))

(deftest register-draft-and-submit
  (let [d (registry/register-draft "eng-1" "FJI" 0)
        s (registry/register-submit "eng-1" "FJI" 0)]
    (is (= "FJI-DFT-000000" (get d "draft_number")))
    (is (= "FJI-SUB-000000" (get s "submit_number")))
    (is (nil? (get-in d ["certificate" "proof"])))
    (is (= "draft-unsigned" (get-in s ["certificate" "status"])))))

(deftest register-requires-ids
  (is (thrown? Exception (registry/register-draft "" "FJI" 0)))
  (is (thrown? Exception (registry/register-submit "eng-1" "" 0))))

(deftest simplified-procurement-eligible-at-or-below-threshold
  (testing "the reg 29 threshold is FJD 50,000 -- at or below is eligible"
    (is (true? (registry/simplified-procurement-eligible? {:declared-contract-value 50000.0})))
    (is (true? (registry/simplified-procurement-eligible? {:declared-contract-value 1.0})))
    (is (true? (registry/simplified-procurement-eligible? {:declared-contract-value 0.0})))))

(deftest simplified-procurement-ineligible-above-threshold
  (testing "above FJD 50,000 requires Government Tender Board review (reg 30), not the simplified track"
    (is (false? (registry/simplified-procurement-eligible? {:declared-contract-value 50000.01})))
    (is (false? (registry/simplified-procurement-eligible? {:declared-contract-value 78000.0})))
    (is (false? (registry/simplified-procurement-eligible? {:declared-contract-value nil}))))
  (testing "no declared value at all -> not eligible (fails, does not throw)"
    (is (false? (registry/simplified-procurement-eligible? {})))))

(deftest simplified-procurement-ineligible-claim-is-entity-scope-gated
  (testing "an engagement NOT claiming the simplified track is never flagged, even if its value would fail eligibility"
    (is (false? (registry/simplified-procurement-ineligible-claim? {:claims-simplified-procurement? false
                                                                      :declared-contract-value 999999.0}))))
  (testing "an engagement claiming the simplified track whose declared value exceeds FJD 50,000 -> ineligible claim"
    (is (true? (registry/simplified-procurement-ineligible-claim? {:claims-simplified-procurement? true
                                                                     :declared-contract-value 78000.0}))))
  (testing "an engagement claiming the simplified track whose declared value DOES satisfy eligibility -> not flagged"
    (is (false? (registry/simplified-procurement-ineligible-claim? {:claims-simplified-procurement? true
                                                                      :declared-contract-value 12000.0})))
    (is (false? (registry/simplified-procurement-ineligible-claim? {:claims-simplified-procurement? true
                                                                      :declared-contract-value 50000.0})))))
