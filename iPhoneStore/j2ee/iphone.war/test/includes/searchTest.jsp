<dsp:page>
  <cast:pageContainer>
    <jsp:attribute name="bodyContent">
    <script type="text/javascript" src="${pageContext.request.contextPath}/test/js/jquery.js"></script>
      <form action="/iPhoneStore/search.jsp" name="search" id="search">
        <table>
          <thead>
            <tr>
              <td>Parameter name</td>
              <td>Parameter value</td>
              <td>Parameter name</td>
              <td>Parameter value</td>
              <td>Search request</td>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>Search keyword</td>
              <td>
                <input type="text" name="searchKeyword" tabindex="1" id="searchKeyword" value="douche"/>
              </td>
              <td>Facet Id</td>
              <td>
                <input type="text" name="facetId" tabindex="6" id="facetId" />
              </td>
              <td rowspan="4">
                <textArea rows="6" cols="40" id="request" id="outta" name="request" tabindex="">
                Please click on submit button.
                </textArea>
              </td>
            </tr>
            <tr>
              <td>Products per page</td>
              <td>
                <input type="text" name="n" value="20" id="n" tabindex="3"/>
              </td>
              <td>Facet value Id</td>
              <td>
                <input type="text" name="facetValueId" tabindex="7" id="facetValueId" />
              </td>
            </tr>
            <tr>
              <td>Page number</td>
              <td>
                <input type="text" name="pageNum" value="0" id="pageNum" tabindex="4"/>
              </td>
            </tr>
            <tr>
              <td>Language</td>
              <td>
                <input type="text" name="lang" value="fr" id="lang" tabindex="5"/>
              </td>
            </tr>
            <tr>
              <td colspan="2">
                <input type="button" value="submit query in json format" onclick="sbmt()" tabindex="2"/>
              </td>
            </tr>
          </tbody>
        </table>
      </form>
      <textArea style="width:100%; height:75%" colls="120" rows="100" id="response" value="ffg" id="outta" name="response" >
      Please click on submit button.
      </textArea>
      <script type="text/javascript"><!--
      <!--
      function sbmt(){
          var keyword=$("#searchKeyword")[0].value;
          var n=$("#n")[0].value;
          var pn=$("#pageNum")[0].value;
          var lang=$("#lang")[0].value;
          var facetId=$("#facetId")[0].value;
          var facetValueId=$("#facetValueId")[0].value;
          var data = { 
                  searchKeyword :keyword,
                  n: n,
                  pageNum: pn,
                  lang: lang,
                  appliedFacet:[{ facetId:facetId, valueId:facetValueId }]
                  };
          var data1 = '{ "searchKeyword":"' + keyword + '", "n":' + n+ ', "pageNum":'+ pn + ', "lang":"' + lang +'"';
          if (facetId != ""){
              data1 += ', "appliedFacet":[{"facetId":'+facetId;
              if (facetValueId != ""){
                  data1 += ',"valueId":'+facetValueId ;
              }
              data1+='}]';
          }
          data1+='}';
          var tereq = $("#request")[0];
          var req = dumpObject(data);
          tereq.value = "{\n"+req.dump+"\n}";
          var o={
                  type: "POST",
                  dataType: "json",
                  contentType: "multipart/form-data",
                  url: "${pageContext.request.contextPath}/search.jsp",
                  data: data1,
                  error: function(XMLHttpRequest, textStatus, errorThrown){
                   debugger;
                  },
                  success: function(json){
                    var ta = $("#response")[0];
                    var obj = jQuery.parseJSON(json);
                    var dob =dumpObject(json, 0);
                    ta.value = "{\n"+dob.dump+"\n}";
                  }
                }
                $.ajax(o);


          
      }
      function dumpObject(obj, level)
      {
        var od = new Object;
        var result = "";
        var len = 0;
        var tb = "";
        for (var i = 0 ; i< level; i++){
            tb+='\t';
        }
        for (var property in obj)
        {
          var value = obj[property];
          if (typeof value == 'string')
            value = "\"" + value + "\"";
          else if (typeof value == 'object')
          {
            if (value instanceof Array)
            {
                var arob = "";
                for (var elemt in value)
                {
                    arob = arob + tb+"{ \n" + dumpObject(value[elemt], level+1).dump + tb+" }, \n";
                }
                arob = arob.replace(/, \n$/, "\n");
                value = "[ " + arob + " ]";
            }
            else
            {
              var ood = dumpObject(value, level+1);
              value = "{ " + ood.dump + " }";
            }
          }
          result += tb+"\"" + property + "\" : " + value + ", \n";
          len++;
        }
        od.dump = result.replace(/, \n$/, "\n");
        od.len = len;

        return od;
      }
      //-->
      </script>
      
    </jsp:attribute>
  </cast:pageContainer>
</dsp:page>