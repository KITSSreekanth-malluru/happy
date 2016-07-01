<dsp:getvalueof var="title" param="topic.title"/>
<dsp:getvalueof var="desc" param="topic.description"/>
<dsp:getvalueof var="biurl" param="topic.bigImage.url"/>
<div class="themesPictureMenuContainer">
  <div class="topicText">
    <div class="topicTextInn">
      <div class="topicTitle"><span>${title}</span></div>
      <div class="topicDescription"><span>${desc}</span></div>
    </div>
  </div>
  <div class="topicBigImage">
    <img src="${biurl}"/>
  </div>
</div>