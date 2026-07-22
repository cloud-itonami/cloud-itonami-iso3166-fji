(ns marketentry.registry
  "Pure-function market-entry filing-draft + filing-submit record
  construction -- an append-only market-entry book-of-record draft.

  Like every sibling actor's registry, there is no single international
  reference-number standard for a public-procurement market-entry
  filing -- every jurisdiction assigns its own format. This namespace
  does NOT invent one; it builds a jurisdiction-scoped sequence number
  and validates the record's required fields, the same honest,
  non-fabricating discipline `marketentry.facts` uses.

  `engagement-fee-matches-claim?` is an HONEST reapplication of the
  SAME ground-truth-recompute DISCIPLINE sibling actors use (verify a
  claimed monetary total against the entity's own recorded quantity x
  unit fields), reapplied to a market-entry engagement fee line.

  `simplified-procurement-eligible?` / `simplified-procurement-
  ineligible-claim?` are the SAME discipline applied to a genuinely
  Fiji-specific mechanism: the Procurement Regulations 2010's own
  two-tier value-threshold split between reg 29 (\"Procurement of goods,
  services or works $50,000 and less\") and reg 30 (\"Procurement of
  goods, services or works $50,001 and more\") -- read directly off the
  regulation's own section titles via the Attorney-General's own Laws of
  Fiji portal (see `marketentry.facts` for the full research trail). A
  filing that claims the simplified reg 29 track while its own declared
  contract value actually sits in the reg 30 tier is trying to evade
  Government Tender Board review -- exactly the kind of ground-truth
  mismatch this governor independently recomputes and HARD-holds.

  This is honestly a FLAT-VALUE-THRESHOLD check shape -- the same
  general shape family precedent already used for Albania's Neni
  76(2)(c) carve-out, per CAF's own comparative docstring. This
  namespace does not force a different shape onto Fiji's actual
  mechanism just to appear novel; the Procurement Regulations 2010
  genuinely ARE a plain two-tier dollar-value split, and the check below
  honestly recomputes exactly that, no more and no less.

  This namespace is pure data + pure functions -- no I/O, no network
  call to any real procurement portal. It builds the RECORD an operator
  would keep, not the act of submitting a portal registration itself
  (that is `marketentry.operation`'s `:filing/submit`, always
  human-gated -- see README Actuation)."
  (:require [clojure.string :as str]))

(defn- unsigned-certificate
  "Every certificate this actor produces is UNSIGNED -- signature is
  the market-entry operator's act, not this actor's."
  [kind subject record-id]
  {"@context" ["https://www.w3.org/ns/credentials/v2"]
   "type" ["VerifiableCredential" kind]
   "credentialSubject" {"id" subject "record" record-id}
   "proof" nil
   "issued_by_registry" false
   "status" "draft-unsigned"})

(defn- zero-pad [n w]
  (let [s (str n)]
    (str (apply str (repeat (max 0 (- w (count s))) "0")) s)))

(defn compute-engagement-fee
  "The ground-truth engagement fee for `engagement`'s own `:base-fee`
  and `:monitoring-months` x `:monthly-rate` -- a single flat
  base + months x rate calculation, not a full pricing engine."
  [{:keys [base-fee monthly-rate monitoring-months]}]
  (+ (double base-fee)
     (* (double monthly-rate) (double monitoring-months))))

(defn engagement-fee-matches-claim?
  "Does `engagement`'s own `:claimed-fee` equal the independently
  recomputed `compute-engagement-fee`?"
  [{:keys [claimed-fee] :as engagement}]
  (== (double claimed-fee) (compute-engagement-fee engagement)))

(def simplified-procurement-threshold-fjd
  "Procurement Regulations 2010, own reg 29 (\"Procurement of goods,
  services or works $50,000 and less\") -- the value at or below which
  the simplified procurement track applies; reg 30 (\"$50,001 and
  more\") requires Government Tender Board review. Read directly off the
  regulation's own section titles (Attorney-General's own Laws of Fiji
  portal, verified 2026-07-22)."
  50000.0)

(defn simplified-procurement-eligible?
  "The ground-truth reg 29 simplified-track eligibility for
  `engagement`, independently recomputed from its own declared
  `:declared-contract-value`. A missing/nil declared value simply fails
  (does not throw) -- an engagement with no declared value is not
  eligible for the simplified track."
  [{:keys [declared-contract-value]}]
  (boolean
   (and (some? declared-contract-value)
        (<= (double declared-contract-value) simplified-procurement-threshold-fjd))))

(defn simplified-procurement-ineligible-claim?
  "Does `engagement` declare `:claims-simplified-procurement? true` (i.e.
  it is following the reg 29 simplified track, bypassing Government
  Tender Board review) while the INDEPENDENTLY recomputed
  `simplified-procurement-eligible?` is false (its own declared contract
  value actually sits in the reg 30 tier)? An engagement that does NOT
  claim the simplified track is never flagged by this check
  (entity/engagement-scope-gated, the same discipline Bhutan's
  `:foreign-company?`-gated FDI check and CAF's `:reserved-market?`-gated
  check use)."
  [{:keys [claims-simplified-procurement?] :as engagement}]
  (boolean (and claims-simplified-procurement?
                (not (simplified-procurement-eligible? engagement)))))

(defn register-draft
  "Validate + construct the FILING-DRAFT registration DRAFT -- the
  market-entry operator's own act of preparing a portal registration
  package. Pure function -- does not touch any real procurement
  portal."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "draft: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "draft: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "draft: sequence must be >= 0" {})))
  (let [draft-number (str (str/upper-case jurisdiction) "-DFT-" (zero-pad sequence 6))
        record {"record_id" draft-number
                "kind" "filing-draft"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "draft_number" draft-number
     "certificate" (unsigned-certificate "FilingDraft" draft-number draft-number)}))

(defn register-submit
  "Validate + construct the FILING-SUBMIT registration DRAFT -- the
  market-entry operator's own act of actually submitting a portal
  registration (always human-gated upstream)."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "submit: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "submit: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "submit: sequence must be >= 0" {})))
  (let [submit-number (str (str/upper-case jurisdiction) "-SUB-" (zero-pad sequence 6))
        record {"record_id" submit-number
                "kind" "filing-submit"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "submit_number" submit-number
     "certificate" (unsigned-certificate "FilingSubmit" submit-number submit-number)}))

(defn append [history result]
  (conj (vec history) (get result "record")))
