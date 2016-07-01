<dsp:page>
	<dsp:importbean bean="/com/castorama/commerce/clientspace/CastNewsletterFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsNull" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
  
	
	<cast:pageContainer>
		<jsp:attribute name="bodyContent">
		
		<div class="breadcrumbs bluePage">
    		<div class="homeBreadIco">
		      	<a href="/store/home.jsp">
			        <img src="/store/images/icoHomeGray.gif" alt="" title=""/>
			    </a>
			</div>
    		<div class="splitter">></div>
			<div><a href="/store/user/clientSpaceHome.jsp">Banner</a></div>
			<div class="splitter">></div>
       		<div class="active">Examples</div>
		</div>
		
		
		
		<div class="bonnesPromoArea">
			<div class="bonnesPromo">
				<div class="bonnesImg">
					<a href="#" class="illustration"><img src="${pageContext.request.contextPath}/images/products/bonnes.gif" /></a>
					<a href="#"><img src="${pageContext.request.contextPath}/images/logos/MAC.gif" /></a>
				</div>
				<div class="prodDecription">
					Cabine de douche circulaire Cyclade hydra
					<div class="discount">-10%</div>
					<div class="priceContent">
                    	<div class="newprice">711,11 &euro;</div>
                    	<div class="oldprice">799,00 &euro;</div>
                    	<div>J'economise <span class="red">87,89 &euro;</span>
                    </div>
                  </div>
                  <input type="submit" class="buttonCartBig" value="Ajouter au panier"/>
				</div>
			</div>
			
			<div class="bonnesPromo">
				<div class="bonnesImg">
					<a href="#" class="illustration"><img src="${pageContext.request.contextPath}/images/products/bonnes.gif" /></a>
					<a href="#"><img src="${pageContext.request.contextPath}/images/logos/MAC.gif" /></a>
				</div>
				<div class="prodDecription">
					Cabine de douche circulaire Cyclade hydra
					<div class="discount">-10%</div>
					<div class="priceContent">
                    	<div class="newprice">711,11 &euro;</div>
                    	<div class="oldprice">799,00 &euro;</div>
                    	<div>J'economise <span class="red">87,89 &euro;</span>
                    </div>
                  </div>
                  <input type="submit" class="buttonCartBig" value="Ajouter au panier"/>
				</div>
			</div>
		</div>
		
		
		
		
		<div class="advertiseBlocks">
			<div class="banner-990x236">
				<img src="${pageContext.request.contextPath}/images/banners/places/990x236.png" />
			</div>
		</div>
		
		<br />
		<br />
		
		<div class="advertiseBlocks">
			<div class="banner-990x118">
				<img src="${pageContext.request.contextPath}/images/banners/places/990x118.png" />
			</div>
		</div>
		
		
		<br />
		<br />
		
		<div class="advertiseBlocks">
			<div class="banner-590x236">
				<img src="${pageContext.request.contextPath}/images/banners/places/590x236.png" />
			</div>
			<div class="banner-390x236">
				<img src="${pageContext.request.contextPath}/images/banners/places/390x236.png" />
			</div>
		</div>
		
		<br />
		<br />
		
		
		<div class="advertiseBlocks">
			<div class="banner-590x118">
				<img src="${pageContext.request.contextPath}/images/banners/places/590x118.png" />
			</div>
			<div class="banner-390x118">
				<img src="${pageContext.request.contextPath}/images/banners/places/390x118.png" />
			</div>
		</div>
		
		<br />
		<br />
		
		<div class="advertiseBlocks">
			<div class="banner-590x236">
				<img src="${pageContext.request.contextPath}/images/banners/places/590x236.png" />
			</div>
			<div class="banner-390x113">
				<img src="${pageContext.request.contextPath}/images/banners/places/390x113.png" />
			</div>
			<div class="banner-390x113 banner-bottom">
				<img src="${pageContext.request.contextPath}/images/banners/places/390x113.png" />
			</div>
		</div>
		
		<br />
		<br />
		
		<div class="advertiseBlocks">
			<div class="banner-590x236">
				<img src="${pageContext.request.contextPath}/images/banners/places/590x236.png" />
			</div>
			<div class="banner-190x113">
				<img src="${pageContext.request.contextPath}/images/banners/places/190x113.png" />
			</div>
			<div class="banner-190x113">
				<img src="${pageContext.request.contextPath}/images/banners/places/190x113.png" />
			</div>
			<div class="banner-190x113 banner-bottom">
				<img src="${pageContext.request.contextPath}/images/banners/places/190x113.png" />
			</div>
			<div class="banner-190x113 banner-bottom">
				<img src="${pageContext.request.contextPath}/images/banners/places/190x113.png" />
			</div>
		</div>
		
		<br />
		<br />
		
		
		<div class="advertiseBlocks">
			<div class="banner-590x236">
				<img src="${pageContext.request.contextPath}/images/banners/places/590x236.png" />
			</div>
			<div class="banner-190x236">
				<img src="${pageContext.request.contextPath}/images/banners/places/190x236.png" />
			</div>
			<div class="banner-190x236">
				<img src="${pageContext.request.contextPath}/images/banners/places/190x236.png" />
			</div>
		</div>
		
		<br />
		<br />
		
		
			
		</jsp:attribute>
	</cast:pageContainer>
</dsp:page>