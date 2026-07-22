# cloud-itonami-iso3166-fji

**FJI**: Republic of Fiji.

- Fiji Procurement Office (Ministry of Economy) public-procurement
  compliance -- Procurement Regulations 2010 (reg 29/reg 30 value
  threshold; Government Tender Board)
- Registrar of Companies (Ministry of Justice) business registration +
  FRCS (Fiji Revenue and Customs Service) TIN registration

AGPL-3.0-or-later.

## Market-entry / statute catalogs

Governed public-sector market-entry compliance actor, same architecture
as every other `cloud-itonami-iso3166-*` sibling:

- `src/marketentry/{facts,governor,phase,sim,operation,registry,store,
  marketentryllm}.cljc` -- the actor. `facts.cljc` cites the Fiji
  Procurement Office (a division of the Ministry of Economy) and the
  Procurement Regulations 2010 (regs 4-20 establish the Office and the
  Government Tender Board; reg 29/reg 30 fix a FJD 50,000/50,001
  procurement-method value threshold); Registrar of Companies (Ministry
  of Justice, `justice.gov.fj`) business/company registration under the
  Companies Act 2015; FRCS (Fiji Revenue and Customs Service) TIN
  registration via TPOS. `governor.cljc`'s flagship check independently
  recomputes the Regulations' own reg 29/reg 30 value threshold -- a
  flat-value-threshold test (the Procurement Regulations 2010 genuinely
  ARE a plain two-tier dollar split; this catalog does not force a
  different shape onto it) -- see the namespace docstrings for the full
  research trail and honestly-narrowed scope, including facts this
  iteration could NOT verify (e.g. a local-representative/agent
  provision in the Companies Act 2015's own text).
- `src/statute/facts.cljc` -- general-law catalog: the Companies Act
  2015 (company law) and the Employment Relations Act 2007 (labour law,
  confirmed directly from Fiji's own Judiciary website).

Every citation is curl/WebFetch-verified against an official source
(`frcs.org.fj`, `justice.gov.fj`, `judiciary.gov.fj`); where the LIVE
official site could not be reached this session (`economy.gov.fj`,
`fpo.gov.fj` -- DNS failure / timeout; `laws.gov.fj`'s live pages are a
client-side-rendered SPA this iteration could not query directly), the
SAME official page was instead read from a `web.archive.org` snapshot --
see `marketentry.facts`'s docstring for exactly which facts are
live-verified vs. archived-snapshot-verified vs. an honestly-flagged
gap.

## Culture catalog

Alongside the market-entry / statute catalogs, this repo carries a
**country-level regional-culture catalog** (ADR-2607171400 addendum 2,
`cloud-itonami-municipality-culture-catalog` Wave 1, in
`com-junkawasaki/root`) — national dishes, protected products, beverages,
crafts, festivals and heritage sites for Fiji:

- `src/culture/facts.cljc` — the catalog, source of truth (keyed by
  uppercase ISO3, mirroring `statute.facts`).
- `schema/culture.edn` — DataScript schema.
- `data/culture-tx.edn` — derived DataScript tx-data (regenerated from
  the catalog, never hand-edited).

City-level counterparts live in the `cloud-itonami-municipality-*` repos.
Same provenance discipline as the compliance catalogs: every entry cites a
source URL that was actually fetched and read on `:culture/retrieved-at`;
summaries state only what the cited source confirms. An item not in
`culture.facts/catalog` has no spec-basis — never fabricate one.
