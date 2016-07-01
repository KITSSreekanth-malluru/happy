<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<ul id="lancez_vous_menu" class="lancez_vous_menu">
  <li class="menu_point_t1 mainMenuPoint">
    <a id="themesMenuButton" href="javascript:void(0);" onclick="hideLVMenuButton()">
      <img class="menuBgImg" src="${pageContext.request.contextPath}/../images/troc/lancez-vous/themesMenuButton.png"/>
    </a>

    <dsp:getvalueof var="maxColumnSize" value="0"/>
    <dsp:getvalueof var="columnsNumber" value="0"/>

    <dsp:droplet name="/com/castorama/droplet/CastTopicsMenuDroplet">
      <dsp:param name="containerId" value="tc100001"/>
      <dsp:oparam name="output">
        <dsp:droplet name="/atg/dynamo/droplet/ForEach">
          <dsp:param name="array" param="map"/>
          <dsp:oparam name="output">
            <dsp:getvalueof var="columnsNumber" param="size"/>
            <dsp:setvalue param="menuColumn" paramvalue="element"/>
              <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                <dsp:param name="array" param="menuColumn"/>
                <dsp:oparam name="output">
                  <dsp:getvalueof var="columnSize" param="size"/>
                  <c:if test="${columnSize > maxColumnSize}">
                      <dsp:getvalueof var="maxColumnSize" value="${columnSize}"/>
                  </c:if>
                </dsp:oparam>
              </dsp:droplet>
          </dsp:oparam>
        </dsp:droplet>
        <script type="text/javascript">
            $(document).ready(function(){
                var $topicMenu = $("#topicMenu").css({
                    "display"    : "block",
                    "visibility" : "hidden"
                }); max_height = 0;
                $("div.divContent", $topicMenu).each(function(){
                    max_height = Math.max(max_height, $(this).height());
                }).height( max_height );
                $topicMenu.css({
                    "display"    : "none",
                    "visibility" : "visible"
                })
                var middle_height = max_height - 325;
                if(middle_height < 0) {
                    middle_height = 0;
                }
                $("#middleImage").height(middle_height);
            });
        </script>
        <div id="topicMenu" class="upperMenuPopup" style="width: ${columnsNumber * 174 + 120}px;" >
            <div class="menuContainer">
            <div class="m8 troc_bg">
              
              <dsp:getvalueof var="menuMap" param="map"/>
              <dsp:droplet name="/atg/dynamo/droplet/ForEach" var="mapEntry" >
                <dsp:param name="array" param="map"/>
                <dsp:setvalue param="menuColumn" paramvalue="element"/>
                <dsp:oparam name="output">
                  <dsp:getvalueof var="counter" param="count"/>
                  
                  <dsp:setvalue param="chapter" value="${mapEntry.key}"/>
                  <dsp:getvalueof var="hImage" param="chapter.headerImage.url"/>
                  <dsp:getvalueof var="color" param="chapter.color"/>
                  
                  <div class="menu_col">
                    <div class="divHeader" style="background-color: ${color};">
                      <img src="${pageContext.request.contextPath}/../..${hImage}" />
                    </div>
                    <div class="divContent" style="min-height:325px; //min-height:340px; height: auto;" >
                      <style type="text/css">#lv_m8.m8 OL LI.liHover${counter}:hover{color: #ffffff; background-color: ${color};}</style>
                      <div id="lv_m8" class="m8">
                        <div class="subCol">
                          <ol>
                            <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                              <dsp:param name="array" param="menuColumn"/>
                              <dsp:setvalue param="menuRow" paramvalue="element"/>
                              <dsp:oparam name="output">
                                <dsp:getvalueof var="pointCounter" param="count"/>
                                <dsp:getvalueof var="pointTitle" param="menuRow.title"/>
                                <dsp:getvalueof var="topicId" param="menuRow.repositoryId"/>
                                <dsp:droplet name="/com/castorama/droplet/CastTopicLinkDroplet">
                                  <dsp:param name="topicId" value="${topicId}"/>
                                  <dsp:oparam name="output">
                                    <dsp:getvalueof var="url" param="url"/>
                                    <li class="liHover${counter}"><a href="${contextPath}${url}"><div class="dLink">${pointTitle}</div></a></li>
                                  </dsp:oparam>
                                </dsp:droplet>
                              </dsp:oparam>
                            </dsp:droplet>
                          </ol>
                        </div>
                      </div>
                    </div>
                  </div>
                </dsp:oparam>
              </dsp:droplet>
              
              <a class="imageLink" href="javascript:void(0);" onclick="hideLVMenu()">
                <img class="menuBgImg" src="${pageContext.request.contextPath}/../images/troc/lancez-vous/themesMenuButtonUp.png"/>
                <img id="middleImage" class="menuBgImgMid" src="${pageContext.request.contextPath}/../images/troc/lancez-vous/themesMenuButtonMid.png" style="height: auto"/>
                <img class="menuBgImg" src="${pageContext.request.contextPath}/../images/troc/lancez-vous/themesMenuButtonDownClose.png"/>
              </a>
          </div>
        </div>
      </div>
      </dsp:oparam>
    </dsp:droplet>
  </li>
</ul>