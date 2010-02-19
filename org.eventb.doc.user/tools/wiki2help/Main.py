#!/usr/bin/env python
import sys
import getopt
import textwrap
import re
import Wiki
from ImageDownloader import ImageDownloader

def main(argv):   
    wiki_domain = 'http://wiki.event-b.org'
    to_dir = '.'
    try:  
        opts, args = getopt.getopt(argv, "ho:d:", ["help", "output=", "domain="])
    except getopt.GetoptError:          
        usage()                         
        sys.exit(2) 
        
    for opt, arg in opts:                
        if opt in ("-h", "--help"):
            usage()                     
            sys.exit()                  
        elif opt in ("-d", "--domain"):                
            wiki_domain = arg
        elif opt in ("-o", "--output"):
            to_dir = arg
        
    from_page = "".join(args)            
    from_wiki = Wiki.Wiki(wiki_domain)
    export_mediawiki_page(from_wiki, from_page, to_dir)
    print "Finished."  
    
def usage():
  """
  Print command line options.
  """
  usage_str = """
  Main wiki_page [options]

  Converts a Mediawiki page to Eclipse Help format.

    wiki_page            - The short name of the Wiki page to be taken as input.
                           (i.e. relative to http://wiki.event-b.org/index.php)
  
    -h, --help           - Print usage summary.
    -o, --output         - Use specified output directory.
                           (by default, it is set to '.')
    -d, --domain         - Use specified Wiki domain.
                           (by default, it is set to 'http://wiki.event-b.org')

  Example Usage:
    Main Decomposition_Plug-in_User's_Guide

  """
  
  print textwrap.dedent(usage_str.strip('\n'))
  sys.exit(1) 
 
def export_mediawiki_page(wiki_from, page_from, dir_to):
    xml = wiki_from.get_page_export([page_from])
    title = wiki_from.get_title(xml)
    images = wiki_from.get_mediawiki_image_list(xml)
    image_names = [img[0] for img in images]
    image_urls = [img[1] for img in images]
    down = ImageDownloader(wiki_from.domain, dir_to + '/images/')
    down.saveImages(image_urls, image_names)
    xml = manage_math_markups(wiki_from, page_from, dir_to, xml)
    wiki_from.write_mediawiki_file(page_from, dir_to, xml)
    
def manage_math_markups(wiki_from, page_from, dir_to, xml):
    html_images = get_html_page(wiki_from, page_from, dir_to)
    math_markups = [img[0] for img in html_images]
    images = [img[1][img[1].rfind("/")+1:] for img in html_images]
    s = xml
    for i in range(0, len(math_markups)):
        s = re.sub("&lt;math&gt;" + math_markups[i].replace("\\","\\\\") + "&lt;/math&gt;", "[[Image:" + images[i] + "]]", s)
    s = re.sub("&lt;math&gt;(.*?)_(.*?)&lt;/math&gt;","&lt;math&gt;\\1<sub>\\2</sub>&lt;/math&gt;",s)
    s = re.sub("&lt;math&gt;(.*?)&lt;/math&gt;","<i>\\1</i>",s)
    s = re.sub("&lt;","<",s)
    s = re.sub("&gt;",">",s)
    s = re.sub("&quot;","\"",s)
    s = re.sub("&amp;","&",s)
    return s
    
def get_html_page(wiki_from, page_from, dir_to):
    html = wiki_from.get_page(page_from)
    images = wiki_from.get_html_image_list(html)
    image_names = ['/images' + img[1][img[1].rfind("/"):] for img in images]
    image_urls = [img[1] for img in images]
    down = ImageDownloader(wiki_from.domain, dir_to)
    down.saveImages(image_urls, image_names)
    return images

if __name__ == "__main__":
    main(sys.argv[1:])