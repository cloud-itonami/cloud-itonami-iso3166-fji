(ns culture.facts
  "Country-level regional-culture catalog for Fiji (FJI) -- national
  dishes, protected products, beverages, crafts, festivals and heritage
  sites, per ADR-2607171400 addendum 2 (cloud-itonami-municipality-
  culture-catalog Wave 1, in com-junkawasaki/root). Sibling namespace to
  `marketentry.facts` / `statute.facts` (ADR-2607141700); city-level
  counterparts live in the cloud-itonami-municipality-* repos.

  Catalog is keyed by UPPERCASE ISO3 (mirrors `statute.facts`); entries
  carry no :culture/municipality (that attribute is city-level only).

  Every entry cites a source URL that was actually fetched and read on
  :culture/retrieved-at -- never fabricated. Summaries state only what the
  cited source confirms. An item not in this table has NO spec-basis, full
  stop; extend `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of culture entries."
  {"FJI"
   [{:culture/id "fji.dish.kokoda"
     :culture/name "Kokoda"
     :culture/country "FJI"
     :culture/kind :dish
     :culture/summary "Fijian version of the Oceanian dish 'ota 'ika: raw fish marinated in citrus juice and coconut milk."
     :culture/url "https://en.wikipedia.org/wiki/%27Ota_%27ika"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "fji.dish.lovo"
     :culture/name "Lovo (earth oven)"
     :culture/country "FJI"
     :culture/kind :dish
     :culture/summary "Indigenous Fijian earth-oven cooking in which staple foods are prepared for ceremonial or celebratory occasions."
     :culture/url "https://en.wikipedia.org/wiki/Earth_oven"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "fji.dish.rourou"
     :culture/name "Rourou"
     :culture/country "FJI"
     :culture/kind :dish
     :culture/summary "Fijian stew of taro leaves cooked in coconut milk with aromatics, often served with cassava or roti; a Fijian preparation of the Polynesian taro-leaf dish lu'au."
     :culture/url "https://en.wikipedia.org/wiki/L%C5%AB%CA%BBau_(food)"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "fji.beverage.yaqona"
     :culture/name "Yaqona (kava)"
     :culture/country "FJI"
     :culture/kind :beverage
     :culture/summary "In Fiji kava is called yaqona; a formal yaqona ceremony often accompanies important social, political or religious functions, with ritual presentation of bundled roots as a sevusevu gift."
     :culture/url "https://en.wikipedia.org/wiki/Kava"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "fji.craft.masi"
     :culture/name "Masi (tapa cloth)"
     :culture/country "FJI"
     :culture/kind :craft
     :culture/summary "Fijian barkcloth made by women from paper mulberry and decorated by stencilling, smoking and rubbing; its colours traditionally indicated the wearer's social rank."
     :culture/url "https://en.wikipedia.org/wiki/Tapa_cloth"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "fji.festival.fiji-day"
     :culture/name "Fiji Day"
     :culture/country "FJI"
     :culture/kind :festival
     :culture/summary "Culmination of the annual Fiji Week celebrations on 10 October, commemorating independence from British colonial rule in 1970 with religious ceremonies and cultural performances."
     :culture/url "https://en.wikipedia.org/wiki/Fiji_Day"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "fji.heritage.levuka"
     :culture/name "Levuka"
     :culture/country "FJI"
     :culture/kind :heritage
     :culture/summary "Historic port town on Ovalau island, designated Fiji's first UNESCO World Heritage Site in 2013 for its exceptional testimony to late colonial Pacific port towns."
     :culture/url "https://en.wikipedia.org/wiki/Levuka"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}]})

(defn spec-basis [iso3] (get catalog iso3))

(defn coverage
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-fji culture catalog "
                 "(ADR-2607171400 addendum 2, Wave 1): " (count (get catalog "FJI"))
                 " FJI entries, each with a fetched-and-read citation. "
                 "Extend `culture.facts/catalog`, never fabricate an id/url.")})))

(defn by-kind [iso3 kind]
  (filterv #(= (:culture/kind %) kind) (spec-basis iso3)))
