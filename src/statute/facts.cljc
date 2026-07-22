(ns statute.facts
  "General-law compliance catalog for the Republic of Fiji (FJI) --
  extends this repo's existing `marketentry.facts` (public-procurement
  market-entry only, narrow scope) with a second, orthogonal catalog of
  statutes a company operating in this jurisdiction must generally track
  for compliance. Mirrors cloud-itonami-iso3166-jpn/-deu/-bgr/-aze/-alb/
  -arm/-atg/-ben/-btn/-bwa/-caf/-est's `statute.facts` (ADR-2607141700,
  cloud-itonami-compliance-fact-federation).

  Every entry cites an OFFICIAL government-hosted URL that this
  iteration actually fetched and read this session -- never fabricated.

  - Companies/commercial-entity law: the Companies Act 2015 (\"CA\"),
    confirmed directly from the Registrar of Companies Office's own page
    (`justice.gov.fj/registrar-of-companies-office/`, fetched directly
    this session, live, HTTP 200): \"The office is responsible for the
    efficient and effective administration of the Companies Act 2015.\"
    The SAME page's own \"Legislations\" list also names the Companies
    Regulation 2015 (\"CR\"), the Credit unions Act 1954 and the Money
    lenders Act 1938 as instruments the same Registrar administers --
    this catalog cites the Companies Act 2015 as the primary company-law
    instrument (the same discipline every sibling catalog uses of citing
    the ONE governing act for a topic, not every adjacent instrument the
    same office happens to also administer). This iteration did NOT
    independently fetch the Companies Act 2015's own primary statutory
    text, only the Registrar's own summary/citation of its title and
    year -- an honest limit on how deep this iteration went, not a claim
    the Act's substantive provisions were read.
  - Employment law: the **Employment Relations Act 2007**, confirmed
    directly and independently from TWO pages of Fiji's own Judiciary
    website (`judiciary.gov.fj`, fetched directly this session, live,
    HTTP 200 on both):
    - `judiciary.gov.fj/tribunals/employment-relations-tribunal/`, own
      text quoted directly: \"The Employment Relations Tribunal is newly
      established under Section 202, Part 20 of the Employment Relations
      Act\" and, further down the SAME page under its own \"Jurisdiction
      of Employment Tribunal\" heading: \"As per Section 211 of the
      Employment Relations Act 2007;\" -- this is the ONE place this
      iteration found the exact enactment YEAR stated outright next to
      the Act's name, on an official Fiji Government page, not inferred
      or carried over from training-data memory.
    - `judiciary.gov.fj/courts/high-court/employment-relations/`, own
      text quoted directly: \"The Employment Relations Court is newly
      established under Section 219 Part 20 (Institutions) of the
      Employment Relations Act as a Division of the High Court.\"
    Before finding these two pages, this iteration first checked the
    Ministry of Employment, Productivity & Industrial Relations' own
    site directly (`employment.gov.fj`, unreachable live this session --
    DNS timeout; read instead via a `web.archive.org` snapshot,
    `20221210080426`, of its homepage) and found ONLY a bare
    abbreviation, no year: \"Promotion of principles and rights at work
    standards, and compliance functions of ERA and its subsidiary
    Regulations.\" That abbreviation-only reference was NOT, by itself,
    treated as sufficient to cite an exact Act year (the Ministry's inner
    pages, including its own \"labourlaws.php\", were never captured by
    Wayback Machine -- checked directly via the CDX API, zero snapshots
    for that path) -- the Judiciary's own two pages above are what
    actually ground the citation used here. This catalog does NOT
    include a second, separate entry for the \"Workmen's Compensation Act
    1964\", which the Employment Relations Tribunal's own jurisdiction
    list also names in passing (\"to hear and determine any matter under
    the Workmen's Compensation Act 1964\") -- that Act's own citable
    shape (full title, current status) was not independently
    investigated this iteration; it is named here only as an honest note
    of a second real instrument this iteration is aware of but has NOT
    catalogued.

  A law not in this table has NO spec-basis, full stop; extend
  `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of statute entries. `:statute/url` + `:statute/law-number`
  are the citation the governor requires before any compliance-fact
  proposal referencing this law can commit. FJI's catalog has 2 entries
  -- company law and employment law, both independently confirmed this
  iteration from official Fijian Government sources actually fetched and
  read this session."
  {"FJI"
   [{:statute/id "fji.companies-act-2015"
     :statute/title "Companies Act 2015"
     :statute/jurisdiction "FJI"
     :statute/kind :law
     :statute/law-number "Companies Act 2015 (\"CA\"), administered by the Registrar of Companies Office, Ministry of Justice, per the Registrar's own official page (justice.gov.fj/registrar-of-companies-office/, fetched directly this session): \"The office is responsible for the efficient and effective administration of the Companies Act 2015.\" This iteration did not independently fetch the Act's own primary statutory text, only the Registrar's own citation of its title/year."
     :statute/url "https://www.justice.gov.fj/registrar-of-companies-office/"
     :statute/url-provenance :official-justice-gov-fj
     :statute/enacted-date "2015-01-01"
     :statute/retrieved-at "2026-07-22"
     :statute/topic #{:corporate-governance :incorporation}}
    {:statute/id "fji.employment-relations-act-2007"
     :statute/title "Employment Relations Act 2007"
     :statute/jurisdiction "FJI"
     :statute/kind :law
     :statute/law-number "Employment Relations Act 2007, per Fiji's own Judiciary website (judiciary.gov.fj/tribunals/employment-relations-tribunal/, fetched directly this session): \"As per Section 211 of the Employment Relations Act 2007\" (Tribunal jurisdiction); Section 202, Part 20 establishes the Employment Relations Tribunal; Section 219, Part 20 establishes the Employment Relations Court (judiciary.gov.fj/courts/high-court/employment-relations/, also fetched directly this session) as a Division of the High Court."
     :statute/url "https://judiciary.gov.fj/tribunals/employment-relations-tribunal/"
     :statute/url-provenance :official-judiciary-gov-fj
     :statute/enacted-date "2007-01-01"
     :statute/retrieved-at "2026-07-22"
     :statute/topic #{:labor}}]})

(defn spec-basis
  "The jurisdiction's statute vector, or nil -- nil means NO spec-basis
  for that jurisdiction yet."
  [iso3]
  (get catalog iso3))

(defn coverage
  "Honest coverage report, same shape/discipline as `marketentry.facts/coverage`:
  never report a missing jurisdiction as covered."
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-fji statute.facts Wave 0 (ADR-2607141700): "
                 (count (get catalog "FJI")) " FJI statute(s) seeded with an "
                 "official citation. Extend `statute.facts/catalog`, never "
                 "fabricate a law-id or URL.")})))

(defn by-topic
  "Statutes for `iso3` tagged with `topic` (e.g. :labor, :data-protection)."
  [iso3 topic]
  (filterv #(contains? (:statute/topic %) topic) (spec-basis iso3)))
