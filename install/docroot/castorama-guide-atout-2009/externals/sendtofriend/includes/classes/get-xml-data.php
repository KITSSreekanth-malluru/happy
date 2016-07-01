<?php

	class xmldata {
		
		public 
		
			$subject,
			$subjectTemp,
			$linktokta,
			$firstname,
			$lastname,
			$firstnameTemp,
			$lastnameTemp,
			$from,
			$to = array(),
			$message,
			$messageTemp,
			$pagemark = array(),
			$annotations = array(),
			$offset,
			$finalurl;



		private
		
			$xmlstring;

		
		function __construct() {
			
			
			if (isset($_POST['xmloutput'])) {
				
				$this->xmlstring = stripslashes($_POST['xmloutput']);
				
			}	else {
				
					die("Error receiving the xml data!");
				
			}
		
			
			$this->getDataFromXml();
			
		}
		
		
		private function getDataFromXml() {
			
			global $OUTPUT_HEADERS;
			
			$dom = new DOMDocument("1.0","utf-8");
			$dom->preserveWhiteSpace = false;
			if (!$dom ->loadXML($this->xmlstring)) die("Error loading the xml data!");
			
			//THE FINAL URL TO THE CATALOGUE, URL THAT WILL BE SEND IN THE BODY OF THE MAIL
			
			$this->linktokta = $dom->getElementsByTagName("urlrequest")->item(0)->nodeValue;

			$tempPageMark = $dom->getElementsByTagName("pagemarks");
			
			if ($tempPageMark->length>0) {
				$this->pagemark = explode(",",$dom->getElementsByTagName("pagemarks")->item(0)->nodeValue);
			}

			$url = parse_url($this->linktokta);
			$this->linktokta=preg_replace(array('/appli.htm/','/appli.php/','/'.SWF_ENGINE.'/'),array('','',''),$url['scheme']."://".$url['host'].$url['path']);
			$query = array();
			if (isset($url['query'])) $query=explode("&",$url['query']);
			$queryvars = array();
			$vars = array();
			foreach($query as $value) {
				$dataandvalue = explode("=",$value);
				if (isset($dataandvalue[1])&& isset($dataandvalue[1])) $queryvars[$dataandvalue[0]] = $dataandvalue[1];
			}
			if (count($this->pagemark)>0) {
				$pagemark = implode("-",$this->pagemark);
				$queryvars['pagemark'] = $pagemark;
			}
			$queryvars = array_filter($queryvars,array($this,"cleanQueryvars"));
			foreach($queryvars as $key =>$value) {
				switch($key) {
					case "G_SAVE_PAGE" :
						$key="page";
						break;
				}
				$vars[] = $key."=".$value;
			} 
			$this->finalurl = $this->linktokta.OPENINGPAGE;
			if (count($queryvars)!=0) $this->finalurl .= "?".implode("&",$vars);

			if ($dom->getElementsByTagName("subject")->length!=0) $this->subjectTemp = $dom->getElementsByTagName("subject")->item(0)->nodeValue;
			$this->firstnameTemp = $dom->getElementsByTagName("firstname")->item(0)->nodeValue;
			$this->lastnameTemp = $dom->getElementsByTagName("lastname")->item(0)->nodeValue;
			$this->from = $dom->getElementsByTagName("from")->item(0)->nodeValue;

			foreach ($dom->getElementsByTagName("to") as $expeditor) {

				$emailsto = preg_split("/(,|;)/",$expeditor->nodeValue);
				foreach ($emailsto as $emailto) {
					if (preg_match("/^[A-Za-z0-9_\\.-]+@[A-Za-z0-9_\\.-]+[.][A-Za-z0-9_\\.-]+$/",$emailto)) $this->to[] = $emailto;					
				}
				
				
			};

			$this->messageTemp = $dom->getElementsByTagName("message")->item(0)->nodeValue;

			// getting the annotations

			$tempAnnotations = $dom->getElementsByTagName("annotations");
			
			if ($tempAnnotations->length>0)	$this->offset = $dom->getElementsByTagName("offset")->item(0)->nodeValue;

			
			foreach ($tempAnnotations as $value) {
				
				$tempPages = $value->getElementsByTagName("page");
				
				foreach ($tempPages as $value) {
					
					$tempPageNb = $value->getAttribute("id");
					$tempsAnnotation = $value->getElementsByTagName("annotation");
					
					if (isset($var)) {
					foreach ($var as $k=>$v) {
						if (preg_match("/^page=/",$v)) unset($var[$k]);							
					}
					}
					$vars['']="page=".$tempPageNb;

					$index = 0;
					
					foreach ($tempsAnnotation as $value) {
						
						$tempValue = iconv("utf-8",$OUTPUT_HEADERS['CHARSET'],$value->nodeValue);
						$this->annotations[$tempPageNb][$index]['text'] = $tempValue;
							
						$pageAnnotationLink = $this->linktokta.OPENINGPAGE;
						
						$this->annotations[$tempPageNb][$index]['link'] = $pageAnnotationLink."?".implode("&",$vars);
						
						$index++;
						
					}
										
					
				}
				
			}
			
			
			$this->firstname = iconv("utf-8",$OUTPUT_HEADERS['CHARSET'],$this->firstnameTemp);
			$this->lastname = iconv("utf-8",$OUTPUT_HEADERS['CHARSET'],$this->lastnameTemp);
			$this->message = iconv("utf-8",$OUTPUT_HEADERS['CHARSET'],$this->messageTemp);
			$this->subject = iconv("utf-8",$OUTPUT_HEADERS['CHARSET'],$this->subjectTemp);
			

		}
		
		private function cleanQueryvars($var) {
			if (!empty($var)) return true;
				else return false;
			
		}
		
		public function addMailSubject($string) {
			
			$this->mailsubject = $string;
			
		}
		
		public function report() {
			
			foreach($this as $key => $value) {
				
				echo $key." => ".$value."<br />";
				
			}
			
		}
		
		
	}

?>