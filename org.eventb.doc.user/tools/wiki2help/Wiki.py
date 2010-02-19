#!/usr/bin/env python
import urllib2
import MultipartPostHandler
import cStringIO
#From http://fabien.seisen.org/python/urllib2_multipart.html
from urllib import urlencode
import cookielib
import re
import os
 
class Wiki:
    def __init__(self, domain, path='/index.php'):
        self.domain = domain
        self.path = self.domain + path
        self.cookie_processor = urllib2.HTTPCookieProcessor()
        self.opener = urllib2.build_opener(self.cookie_processor, MultipartPostHandler.MultipartPostHandler())
 
    def get_title(self, xml):
        matches = re.findall("<title>(.*)</title>", xml)
        return matches[0]
    
    def get_mediawiki_image_list(self, xml):
        matches = re.findall("\[\[Image:(.*?)[\]\|]", xml)
        images = [(match, '/images/' + match.capitalize()) for match in matches]
        return images
    
    def get_html_image_list(self, html):
        matches = re.findall("<img class=\"tex\" alt=\"(.*?)\" src=\"(.*?)\"", html)
        images = [(match[0], match[1]) for match in matches]
        return images
 
    def get_page_export(self, pages):
        text = "\n".join(pages)
        data = {
            'curonly': 'on',
            'pages': text,
            'templates': 'on',
            #'wpDownload': '',
            'submit': 'Export'
        }
        url = self.path + "/Special:Export"
        result = self.opener.open((url), urlencode(data))
        return result.read()
    
    def get_page(self, url):
        result = self.opener.open(self.path + "/" + url)
        return result.read()
    
    def write_mediawiki_file(self, title, dir_to, xml):
        matches = re.findall("<text xml:space=\"preserve\">(.*?)</text>", xml, re.DOTALL)
        xml_file = open(dir_to + '/' + title + '.mediawiki', "w")
        xml_file.write(matches[0])
        xml_file.close()