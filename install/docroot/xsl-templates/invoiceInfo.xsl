<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:lxslt="http://xml.apache.org/xslt"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    xmlns:xlink="http://www.w3.org/1999/xlink">

    <xsl:template name="print_invoice">
                <fo:table xsl:use-attribute-sets="table-tight">
                    <fo:table-column/>
                    <fo:table-body>
                        <fo:table-row>
                            <fo:table-cell padding-left="12px">
                                <fo:block padding-top="20px"/>
                                <fo:block xsl:use-attribute-sets="black-normal-7" text-align="left">Conservez cette facture, elle tient lieu de garantie.</fo:block>
                                <fo:block xsl:use-attribute-sets="black-normal-7" text-align="left">Vous pouvez à tout moment consulter l'état de la commande sur notre site: www.castorama.fr</fo:block>
                                <fo:block xsl:use-attribute-sets="black-normal-7" text-align="left">Nous vous remercions de votre commande et vous en souhaitons bonne réception :</fo:block>
                                <fo:block padding-top="10px"/>
                                <fo:block xsl:use-attribute-sets="black-normal-7" text-align="left">TVA acquittée sur les encaissements</fo:block>
                                <fo:block padding-top="10px"/>
                                <fo:block xsl:use-attribute-sets="black-normal-7" text-align="left">Modalités et conditions de règlement</fo:block>
                                <fo:block xsl:use-attribute-sets="black-normal-7" text-align="left">Paiement comptant.</fo:block>
                                <fo:block xsl:use-attribute-sets="black-normal-7" text-align="left">Il ne sera pas accordé d’escompte en cas de règlement anticipé.  (Loi 92-1442)</fo:block>
                                <fo:block xsl:use-attribute-sets="black-normal-7" text-align="left">Conformément à l’article L441-3 et suivants du Code de commerce, tout retard de paiement des sommes dues à leur échéance entraînera de plein droit l’application d’une pénalité de 5.5%  du montant TTC
                                    dû.</fo:block>
                                <fo:block xsl:use-attribute-sets="black-normal-7" text-align="left">Indemnité forfaitaire pour frais de recouvrement en cas de retard de paiement : 40euros.</fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                    </fo:table-body>
                </fo:table>
    </xsl:template>

</xsl:stylesheet>
