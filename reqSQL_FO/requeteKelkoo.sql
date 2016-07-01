SELECT 
                "SkuId", "Url de la page", "Stock", "Libelle", "Description", "Garantie", 
                "SoumisaD3E", "Eco-taxe en euros", "Image", "PageListe name", 
                "CatNiveau 1 name", "CatNiveau 2 name", "CatNiveau 3 name", "CatNiveau 4 name", "CatNiveau 5 name", 
                "PageListe id", "CatNiveau 1 id", "CatNiveau 2 id", "CatNiveau 3 id", "CatNiveau 4 id", "CatNiveau 5 id", 
                "Marque", "List Price", "On Sale", "Sale Price", "Frais de livraison", 
                CASE 
                                WHEN "Delai de livraison en jr" > 35 THEN '6 - 8 semaines' 
                                WHEN "Delai de livraison en jr" > 28 THEN '5 semaines' 
                                WHEN "Delai de livraison en jr" > 21 THEN '4 semaines' 
                                WHEN "Delai de livraison en jr" > 14 THEN '3 semaines' 
                                WHEN "Delai de livraison en jr" > 7  THEN '2 semaines' 
                                WHEN "Delai de livraison en jr" > 0  THEN '1 semaine' 
                                ELSE '6 - 8 semaines' 
                END AS "Delai de Livraison", 
                "Type Article", "RetraitMomentaneMotifsCodifies" 
FROM 
( 
SELECT 
                ds.sku_id AS "SkuId", 
                --cs.url_page AS "Url de la page", 
        '/store/' || replace(replace(ds.display_name, ' ', '-'), '/', '-')  || '-' || ds.sku_id || '.html' AS "Url de la page", 
                di.stock_level AS "Stock", 
                ds.display_name AS "Libelle", 
                cs.LibelleClientLong AS "Description", 
                cs.garantie AS "Garantie", 
                cs.SoumisaD3E AS "SoumisaD3E", 
                cs.ecoTaxeEnEuro AS "Eco-taxe en euros", 
                'http://www.castorama.fr/produits/' || LTRIM (cs.sku_id, 'Casto') || '_g.jpg' AS "Image", 
                pageListe.display_name AS "PageListe name", 
                catNiveau1.display_name AS "CatNiveau 1 name", 
                catNiveau2.display_name AS "CatNiveau 2 name", 
                catNiveau3.display_name AS "CatNiveau 3 name", 
                catNiveau4.display_name AS "CatNiveau 4 name", 
                catNiveau5.display_name AS "CatNiveau 5 name", 
                pageListe.category_id AS "PageListe id", 
                catNiveau1.category_id AS "CatNiveau 1 id", 
                catNiveau2.category_id AS "CatNiveau 2 id", 
                catNiveau3.category_id AS "CatNiveau 3 id", 
                catNiveau4.category_id AS "CatNiveau 4 id", 
                catNiveau5.category_id AS "CatNiveau 5 id", 
                cs.MarqueCommercial as "Marque", 
                dp.list_price AS "List Price", 
                ds.on_sale AS "On Sale", 
                dp2.list_price AS "Sale Price", 
                DECODE (cs.exoneration_pfe, 
                                1, 'Port gratuit', 
                                DECODE (cs.horsnormes, 
                                                1, (cs.poidsuv / 1000) * cpp.prix_au_kg + cpp.forfait_ttc + cpp.forfait_hn_ttc, 
                                                (cs.poidsuv / 1000) * cpp.prix_au_kg + cpp.forfait_ttc 
                                ) 
                ) AS "Frais de livraison", 
                DECODE (cs.typeexpedition, 1, 
                                CASE WHEN di.stock_level = 0 THEN DECODE (cs.transporteur, 'COLISSIMO', 5, 7) + cs.delaiapprofournisseur 
                                ELSE DECODE (cs.transporteur, 'COLISSIMO', 3, 5) END, 
                                2, cs.delaiapprofournisseur + 2, 
                                -3 
                ) AS "Delai de livraison en jr", 
                CASE 
                                WHEN cs.typearticle = 1 THEN 'Vendu sur Internet et en magasin' 
                                WHEN cs.typearticle = 2 THEN 'Exclusif Internet' 
                                WHEN cs.typearticle = 3 THEN 'Vendu uniquement en magasin' 
                                ELSE 'Type Article inconnu' 
                END AS "Type Article", 
                cs.retraitMomentaneMotifsCodifies AS "RetraitMomentaneMotifsCodifies" 

FROM 
                CAST_CORE.casto_pfe_poids cpp, 
                dcs_sku ds 
INNER JOIN casto_sku cs ON cs.sku_id = ds.sku_id 
INNER JOIN dcs_prd_chldsku lienPrdSku ON lienPrdSku.sku_id = ds.sku_id 
INNER JOIN dcs_cat_chldprd lienPrdPL ON lienPrdPL.child_prd_id = lienPrdSku.product_id AND lienPrdPL.category_id like 'cat%' 
LEFT OUTER JOIN dcs_price dp ON dp.sku_id = cs.sku_id AND dp.price_list = 'listPrices'
LEFT OUTER JOIN dcs_price dp2 ON dp2.sku_id = cs.sku_id AND dp2.price_list = 'salePrices' 
LEFT OUTER JOIN CAST_CORE.dcs_inventory di ON cs.sku_id = di.catalog_ref_id 
LEFT OUTER JOIN dcs_cat_chldcat lienCatCatNiveau1 ON lienCatCatNiveau1.child_cat_id = lienPrdPL.category_id AND lienCatCatNiveau1.category_id like 'cat%' 
LEFT OUTER JOIN dcs_cat_chldcat lienCatCatNiveau2 ON lienCatCatNiveau2.child_cat_id = lienCatCatNiveau1.category_id AND lienCatCatNiveau2.category_id like 'cat%' 
LEFT OUTER JOIN dcs_cat_chldcat lienCatCatNiveau3 ON lienCatCatNiveau3.child_cat_id = lienCatCatNiveau2.category_id AND lienCatCatNiveau3.category_id like 'cat%' 
LEFT OUTER JOIN dcs_cat_chldcat lienCatCatNiveau4 ON lienCatCatNiveau4.child_cat_id = lienCatCatNiveau3.category_id AND lienCatCatNiveau4.category_id like 'cat%' 
LEFT OUTER JOIN dcs_cat_chldcat lienCatCatNiveau5 ON lienCatCatNiveau5.child_cat_id = lienCatCatNiveau4.category_id AND lienCatCatNiveau5.category_id like 'cat%' 
LEFT OUTER JOIN dcs_category pageListe ON pageListe.category_id = lienPrdPL.category_id 
LEFT OUTER JOIN dcs_category catNiveau1 ON catNiveau1.category_id = lienCatCatNiveau1.category_id 
LEFT OUTER JOIN dcs_category catNiveau2 ON catNiveau2.category_id = lienCatCatNiveau2.category_id 
LEFT OUTER JOIN dcs_category catNiveau3 ON catNiveau3.category_id = lienCatCatNiveau3.category_id 
LEFT OUTER JOIN dcs_category catNiveau4 ON catNiveau4.category_id = lienCatCatNiveau4.category_id 
LEFT OUTER JOIN dcs_category catNiveau5 ON catNiveau5.category_id = lienCatCatNiveau5.category_id 
WHERE 
(cs.poidsuv / 1000) >= cpp.poids_inf 
AND (cs.poidsuv / 1000) < cpp.poids_sup 
AND cpp.grille_pfe_id = 1 
ORDER BY            ds.sku_id 
);
