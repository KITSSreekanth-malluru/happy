<dsp:page>
    <dsp:getvalueof var="pbx_codefamille" param="lb" />
    <script type="text/javascript">
    <!--
        function sofincoSubmit() {
            window.opener.document.pbx_sofinco.PBX_CODEFAMILLE.value ="${pbx_codefamille}";
            window.opener.document.pbx_sofinco.submit();
            window.close();
        }
    //-->
    </script>

<body onload="sofincoSubmit();">
</body>
</dsp:page>