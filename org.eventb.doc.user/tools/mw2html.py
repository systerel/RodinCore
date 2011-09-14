#! /usr/bin/env python

"""
mw2html - Mediawiki to static HTML

I use this to create a personal website from a local mediawiki
installation.  No search functionality.  Hacks the Monobook skin and
the produced HTML.

Connelly Barnes 2005.  Public domain.
"""

__version__ = '0.1.0.0'

import re
import sys
import getopt
import random
import urllib
import urllib2
import textwrap
import urlparse
import os, os.path
import errno
import sha
import logging

try:
  set
except:
  from sets import Set as set

try:
  import htmldata
except:
  print 'Requires Python htmldata module:'
  print '  http://oregonstate.edu/~barnesc/htmldata/'
  sys.exit()

try:
  import tidy
  TIDY = True
except:
  print 'Use of HTML tidy library and python wrapper is recommended'
  TIDY = False

MOVE_HREF          = 'movehref'
MADE_BY_COMMENT    = '<!-- Content generated by Mediawiki and mw2html -->'
INDEX_HTML = 'index.html'
url_filename_cache = {}
wrote_file_set     = set()

MONOBOOK_SKIN      = 'monobook'    # Constant identifier for Monobook.

class Config:
  """
  Instances contain all options passed at the command line.
  """
  default_title_extract_re = r'([^<]*) - Event-B'
  def __init__(self, rooturl, outdir,
               flatten=True, lower=True, index=None, clean=True,
               sidebar=None, hack_skin=True, fetch_images=True,
               made_by=True, overwrite=False, footer=None,
               skin=MONOBOOK_SKIN, move_href=True, image_dir = True,
               one_page=False,
               remove_png=True, remove_history=True,url_regex=None,
               verbose=logging.WARNING,
               title_extract_re = None
               ):
    self.rooturl         = rooturl
    self.outdir          = os.path.abspath(outdir)
    self.flatten         = flatten
    self.lower           = lower
    self.index           = index
    self.clean           = clean
    self.sidebar         = sidebar
    self.hack_skin       = hack_skin
    self.made_by         = made_by
    self.overwrite       = overwrite
    self.footer          = footer
    self.skin            = skin
    self.move_href       = move_href
    self.url_regex       = url_regex
    if title_extract_re:
        self.title_extract_re = title_extract_re
    else:
        self.title_extract_re = self.default_title_extract_re

    if self.sidebar is not None:
        self.sidebar       = os.path.abspath(self.sidebar)
    if self.footer is not None:
        self.footer        = os.path.abspath(self.footer)
    self.remove_png      = remove_png
    self.remove_history  = remove_history
    self.verbose         = verbose
    self.fetch_images    = fetch_images
    self.image_dir       = image_dir
    self.one_page        = one_page


def post_filename_transform(filename, config):
  """
  User-customizable filename transform.

  Here filename is the full filename in the output directory.
  Returns modified full filename.
  """
  return filename


def monobook_fix_html_sidebar(doc, config):
  """
  Sets sidebar for Mediawiki 1.4beta6 Monobook HTML output.
  """
  if config.made_by:
    doc = doc.replace('<html xmlns=', MADE_BY_COMMENT + '\n<html xmlns=')

  SIDEBAR_ID = 'SIDEBAR' + sha.new(str(random.random())).hexdigest()

  # Remove sidebar HTML
  doc = re.sub(
    r'(<!-- end content -->)[\s\S]+?' +
    r'(<!-- end of the left \(by default at least\) column -->)',
    r'\1<div class="visualClear"></div></div></div></div>' + SIDEBAR_ID + r'\2', doc)

  pre_sidebar = """
    <div id="column-one">
      <div id="p-cactions" class="portlet"></div>
      <div class="portlet" id="p-personal"></div>
      <div class="portlet" id="p-logo"></div>
      <div class="portlet" id="p-nav">
  """

  post_sidebar = """
      </div>
      <div id="p-search" class="portlet"></div>
      <div class="portlet" id="p-tb"></div>
    </div>
    <!-- end left column -->
  """

  sidebar_content = ''
  if config.sidebar != None:
    f = open(config.sidebar, 'rU')
    sidebar_content = f.read()
    f.close()

  sidebar_content = pre_sidebar + sidebar_content + post_sidebar

  doc = doc.replace(SIDEBAR_ID, sidebar_content)

  doc = re.sub(
    r'<div id="f-poweredbyico">[\s\S]+?(<ul id="f-list">)',
    r'\1', doc)

  # Remove edit links
  doc = re.sub(r'<div class="editsection"[\s\S]+?</div>', r'', doc)

  # Remove page has been accessed X times list item.
  doc = re.sub(r'<li id="f-viewcount">[\s\S]+?</li>', r'', doc)

  # Remove disclaimers list item.
  doc = re.sub(r'<li id="f-disclaimer">[\s\S]+?</li>', r'', doc)

  # Replace remaining text with footer, if available.
  if config.footer is not None:
    s1 = '<div id="footer">'
    s2 = '</div>'
    i1 = doc.index(s1)
    i2 = doc.index(s2, i1)
    f = open(config.footer, 'rU')
    footer_text = f.read()
    f.close()
    doc = doc[:i1+len(s1)] + footer_text + doc[i2:]

  return doc


def fix_move_href_tags(doc, config):
  """
  Return copy of doc with all MOVE_HREF tags removed.
  """
  while '<' + MOVE_HREF in doc:
    i1 = doc.index('<' + MOVE_HREF)
    i2 = doc.index('</' + MOVE_HREF, i1+1)
    i3 = doc.index('>', i2+1)
    (start, end) = (i1, i3+1)
    tags = htmldata.tagextract(doc[start:end])
    assert tags[0][0] == MOVE_HREF
    assert tags[-1][0] == '/' + MOVE_HREF
    href = tags[0][1].get('href', '')
    new_tags = []
    for tag in tags[1:-1]:
      if len(tag) == 2:
        if 'href' in tag[1]:
          if href == '':
            continue
          tag[1]['href'] = href
      new_tags += [tag]
    doc = doc[:start] + htmldata.tagjoin(new_tags) + doc[end:]
  return doc


def html_remove_image_history(doc, config):
  """
  Remove image history and links to information.
  """
  doc = re.sub(r'<h2>Image history</h2>[\s\S]+?</ul>', r'', doc)
  doc = re.sub(r'<h2>Image links</h2>[\s\S]+?</ul>', r'', doc)
  return doc

def gen_xml_toc(doc, config, one_page=False, doc_filename=None):
    """
    Generate XML toc for eclipse documentation plugin

    :one_page: says if the TOC shall be build by the page headers. If false
               the TOC is built from the *topic* class elements.
    :doc_filename: is the file name in which *doc* will be saved.
    """

    toc ="""<?xml version="1.0" encoding="UTF-8"?>
<?NLS TYPE="org.eclipse.help.toc"?>
"""
    out_dir_base = os.path.basename(config.outdir)
    if not out_dir_base:
        out_dir_base = os.path.basename(config.outdir[:-1])

    # Force one_page for page having no ''topic'' elements
    if not 'class="topic"' in doc:
        one_page = True

    logging.debug("one_page : %s, doc_filename: %s\n" % (one_page,
        doc_filename))
    # Some set up depending on one_page
    if one_page:
        topic=re.compile(r'<a name="([^"]*)"></a><h([2-9])>\s*<span class="mw-headline">([^<]*)</span></h[2-9]>',re.UNICODE)
        where_base = "%s/%s#" % (out_dir_base, os.path.basename(doc_filename))
        title_extract_re = config.default_title_extract_re
    else:
        topic=re.compile(r'<div class="topic" style="display:inline;"><a href="([^"]*)"(0*)[^>]*>([^<]*)</a></div>',re.UNICODE)
        where_base = "%s/" % out_dir_base
        title_extract_re = config.title_extract_re



    title = re.search(r'<title>%s</title>' % title_extract_re,
            doc).group(1)
    filename = os.path.splitext(doc_filename)[0] + '.xml'
    logging.info("Writing %s\n"%filename)


    logging.info("Generate %s file" % filename)


    toc += '<toc label="'+title+'">\n'

    # we expect a first heading level for h2
    first_level = None

    for m in topic.finditer(doc):
        logging.debug("Found topic <h%s> %s : %s" % (m.group(2), m.group(3),m.group(1)))
        try:
            l = int(m.group(2))
        except:
            l = 2
        if first_level is None:
            first_level = l
            level = l
        else:
            for l in range(level, l-1, -1):
                toc+='%s</topic>\n' % (' '*l)
        toc += '%s<topic label="%s" href="%s%s">\n' % (' ' * l,
                m.group(3).strip(), where_base,  m.group(1))
        if not one_page:
            toc += '%s<link toc="%s.xml" />' % (' ' * (l+1),
                    os.path.splitext(m.group(1))[0] )
        level = l

    if first_level:
        for l in range(level, first_level-1, -1):
            toc+='%s</topic>\n' % (' '*l)

    toc += "</toc>\n"

    f = open(filename, 'w')
    f.write(toc)
    f.close()

def post_html_transform(doc, url, config):
  """
  User-customizable HTML transform.

  Given an HTML document (with URLs already rewritten), returns
  modified HTML document.
  """
  if config.hack_skin:
    if config.skin == MONOBOOK_SKIN:
      doc = monobook_fix_html_sidebar(doc, config)
      doc = monobook_hack_skin_html(doc, config)
    else:
      raise ValueError('unknown skin')
  if config.move_href:
    doc = fix_move_href_tags(doc, config)
  if config.remove_history:
    doc = html_remove_image_history(doc, config)

  #remove some HTML
  L = htmldata.tagextract(doc)
  R=[]
  prev=None
  skip=False
  for item in L:
    #logging.debug("item %s"% repr(item))
    # remove edit links
#    if isinstance(item, tuple) and item[0] == 'span' \
#        and 'class' in item[1] and item[1]['class'] == 'editsection':
#      R.append(item)
#      skip=True
#    elif skip == True and not(isinstance(item, tuple) and item[0] == '/span'):
#      logging.debug("Remove item %s " % repr(item))
#    elif skip == True and isinstance(item, tuple) and item[0] == '/span':
#      R.append('&nbsp;') #necessary to prevent eclipse ruiningthe html when
#                         #inserting anchors
#      R.append(item)
#      skip=False
    # remove empty div
    if isinstance(item, tuple) and item[0] == 'div':
      if prev != None:
        R.append(prev)
      prev=item
    elif prev != None:
      if isinstance(item, tuple) and item[0] == '/div':
        logging.debug("Remove item %s %s" % (repr(prev),repr(item)))
        R.append(('<!-- %s %s -->' % (repr(prev),repr(item))))
        prev=None
      else:
        R.append(prev)
        R.append(item)
        prev=None
    else:
      R.append(item)

  doc=htmldata.tagjoin(R)
  return doc


def monobook_hack_skin_html(doc, config):
  """
  Hacks Monobook HTML output: use CSS ids for hacked skin.

  See monobook_hack_skin_css.
  """
  doc = doc.replace('<div id="globalWrapper">', '<div id="globalWrapperHacked">')
  doc = doc.replace('<div id="footer">', '<div id="footerHacked">')
  doc = doc.replace('</body>', '<br/></body>')
  return doc


def monobook_hack_skin_css(doc, url, config):
  """
  Hacks Mediawiki 1.4beta6 Monobook main CSS file for better looks.

  Removes flower background.  Defines *Hacked CSS ids, so we can add
  an orange bar at the top, and clear the orange bar right above the
  footer.
  """
  logging.debug("monobook_hack_skin_css :  %s" % url)
  if not 'monobook/main.css' in url:
    return doc


  doc = "/* Monobook skin automatically modified by mw2html. */" + doc
  doc = doc.replace('url("headbg.jpg")', '')

  doc += """
    /* Begin hacks by mw2html */

    #globalWrapperHacked {
      font-size:127%;
      width: 100%;
      background-color: White;
      border-top: 1px solid #fabd23;
      border-bottom: 1px solid #fabd23;
      margin: 0.6em 0em 1em 0em;
      padding: 0em 0em 1.2em 0em;
    }

    #footerHacked {
      background-color: White;
      margin: 0.6em 0em 0em 0em;
      padding: 0.4em 0em 0em 0em;
      text-align: center;
      font-size: 90%;
    }

    #footerHacked li {
      display: inline;
      margin: 0 1.3em;
    }
  #column-one {
      padding-top: 3.0em; 
    }
#content {
      margin: 0em 0 0 0em;
      border-width: 0 1px 1px 0;
  }
#column-one {
    display: none;
  }
#catlinks {
    display: none;
  }
.editsection {
    display: none;
  }
    """

  #c1 = '#column-one { padding-top: 160px; }'
  #c2 = '#column-one { padding-top: 3.0em; }'
  #assert c1 in doc
#
#  doc = doc.replace(c1, '/* edit by mw2html */\n' + c2 +
#                        '\n/* end edit by mw2html */\n')

  # Remove external link icons.
  if config.remove_png:
    doc = re.sub(r'#bodyContent a\[href \^="http://"\][\s\S]+?\}', r'', doc)

#  import cssutils
#  from cssutils import css, stylesheets
#
#  sheet = cssutils.parseString(doc)
#  style = css.CSSStyleDeclaration()
#  style['padding-top'] = '3.0em'
#  stylerule = css.CSSStyleRule(selectorText=u'#column-one', style=style)
#  sheet.add(stylerule)
#  doc = sheet.cssText

  return doc


def post_css_transform(doc, url, config):
  """
  User-customizable CSS transform.

  Given a CSS document (with URLs already rewritten), returns
  modified CSS document.
  """
  if config.hack_skin:
    if config.skin == MONOBOOK_SKIN:
      doc = monobook_hack_skin_css(doc, url, config)
    else:
      raise ValueError('unknown skin')
  return doc


def url_to_filename(url, config):
  """
  Translate a full url to a full filename (in local OS format) under outdir.
  """
  url = split_section(url)[0]
  if url in url_filename_cache:
    return url_filename_cache[url]

  part = url
  if part.lower().startswith('http://'):
    part = part[len('http://'):]
  L = part.strip('/').split('/')
  L = [urllib.quote_plus(x) for x in L]
  if len(L) <= 1 or not '.' in L[-1]:
    # url ends with a directory name.  Store it under index.html.
    L += [INDEX_HTML]

  # Local filename relative to outdir
  # (More transformations are made to this below...).
  subfile = os.sep.join(L)

  # Fix up extension based on mime type.
  fix_ext = True
  try:
    f = urllib2.urlopen(url)
  except urllib2.URLError, e:
    fix_ext = False

  if fix_ext:
    mimetype = f.info().type.lower().split(' ')[0]
    # Maps mimetype to file extension
    MIME_MAP = {
     'image/jpeg': 'jpg', 'image/png': 'png', 'image/gif': 'gif',
     'image/tiff': 'tiff', 'text/plain': 'txt', 'text/html': 'html',
     'text/rtf': 'rtf', 'text/css': 'css', 'text/sgml': 'sgml',
     'text/xml': 'xml', 'application/zip': 'zip'
    }
    if mimetype in MIME_MAP:
      (root, ext) = os.path.splitext(subfile)
      ext = '.' + MIME_MAP[mimetype]
      subfile = root + ext

  return(path_to_filename(url, subfile, config))



def path_to_filename(url, subfile, config):
  if config.lower:
    subfile = subfile.lower()

  ans = os.path.join(config.outdir, subfile)

  if config.flatten:
    ans = flatten_filename(config, ans)

  if config.clean:
    ans = clean_filename(url, config, ans)

  if config.index != None:
    ans = move_to_index_if_needed(config, ans)

  ans = find_unused_filename(ans, file_exists_in_written_set)

  ans = post_filename_transform(ans, config)

  # Cache and return answer.
  wrote_file_set.add(os.path.normcase(os.path.normpath(ans)))
  url_filename_cache[url] = ans
  return ans


def file_exists_in_written_set(filename):
  return os.path.normcase(os.path.normpath(filename)) in wrote_file_set


def find_unused_filename(filename, exists=os.path.exists):
  """
  Return 'file' if 'file' doesn't exist, otherwise 'file1', 'file2', etc.

  Existance is determined by the callable exists(), which takes
  a filename and returns a boolean.
  """
  if not exists(filename):
    return filename
  (head, tail) = os.path.split(filename)
  i = 1
  while True:
    numbered = (os.path.splitext(tail)[0] + str(i) +
                os.path.splitext(tail)[1])
    fullname = os.path.join(head, numbered)
    if not exists(fullname):
      return fullname
    i += 1


def clean_filename(url, config, ans):
  # Split outdir and our file/dir under outdir
  # (Note: ans may not be a valid filename)
  (par, ans) = (ans[:len(config.outdir)], ans[len(config.outdir):])
  if ans.startswith(os.sep):
    ans = ans[1:]

  # Replace % escape codes with underscores, dashes with underscores.
  while '%%' in ans:
    ans = ans[:ans.index('%%')] + '_' + ans[ans.index('%%')+2:]
  while '%25' in ans:
    ans = ans[:ans.index('%25')] + '_' + ans[ans.index('%25')+5:]
  while '%' in ans:
    ans = ans[:ans.index('%')] + '_' + ans[ans.index('%')+3:]
  ans = ans.replace('-', '_')
  ans = ans.replace(' ', '_')
  while '__' in ans:
    ans = ans.replace('__', '_')
  while '_.' in ans:
    ans = ans.replace('_.', '.')

  if config.image_dir and ('/images/' in url or '/math/' in url):
    par = os.path.join(par, 'images')

  # Rename math thumbnails
  if '/math/' in url:
    tail = os.path.split(ans)[1]
    if os.path.splitext(tail)[1] == '.png':
      tail = os.path.splitext(tail)[0]
      if set(tail) <= set('0123456789abcdef') and len(tail) == 32:
        ans = 'math_' + sha.new(tail).hexdigest()[:4] + '.png'
  return os.path.join(par, ans)


def move_to_index_if_needed(config, ans):
  if ans.endswith(config.index):
    ans = ans[:len(ans)-len(config.index)] + INDEX_HTML
  return ans


def flatten_filename( config, filename):
  def get_fullname(relname):
    return os.path.join(config.outdir, relname)

  orig_ext = os.path.splitext(filename)[1]
  (head, tail) = os.path.split(filename)
  if tail == INDEX_HTML:
    (head, tail) = os.path.split(head)
  ans = tail
  if os.path.splitext(ans)[1] != orig_ext:
    ans = os.path.splitext(ans)[0] + orig_ext
  return os.path.join(config.outdir, ans)


def split_section(url):
  """
  Splits into (head, tail), where head contains no '#' and is max length.
  """
  if '#' in url:
    i = url.index('#')
    return (url[:i], url[i:])
  else:
    return (url, '')


def rewrite_external_url(url, config):
  """
  Rewrite any URL that could not be stored locally.

  To not rewrite any external URLs, simply return url.
  """
  # Do not nullify other URL inside the fetched domain
  return url


def url_to_relative(url, cururl, config):
  """
  Translate a full url to a filename (in URL format) relative to cururl.
  """
  cururl = split_section(cururl)[0]
  (url, section) = split_section(url)

  L1 = url_to_filename(url,    config).replace(os.sep, '/').split('/')
  L2 = url_to_filename(cururl, config).replace(os.sep, '/').split('/')

  while L1 != [] and L2 != [] and L1[0] == L2[0]:
    L1 = L1[1:]
    L2 = L2[1:]

  return urllib.quote('../' * (len(L2) - 1) + '/'.join(L1)) + section


def parse_css(doc, url, config):
  """
  Returns (modified_doc, new_urls), where new_urls are absolute URLs for
  all links found in the CSS.
  """
  new_urls = []  

  L = htmldata.urlextract(doc, url, 'text/css')
  for item in L:
    # Store url locally.
    u = item.url
    new_urls += [u]
    item.url = url_to_relative(u, url, config)

  newdoc = htmldata.urljoin(doc, L)
  newdoc = post_css_transform(newdoc, url, config)

  return (newdoc, new_urls)


def get_domain(u):
  """
  Get domain of URL.
  """
  ans = urlparse.urlparse(u)[1]
  if ':' in ans:
    ans = ans[:ans.index(':')]
  return ans


def should_follow(rooturl, url, config):
  """
  Returns boolean for whether url should be spidered.

  Given that 'url' was linked to from site 'rooturl', return whether
  'url' should be spidered as well.
  """
  #logging.debug("regex is : %s" % config.url_regex)
  # False if different domains.
  if get_domain(rooturl) != get_domain(url):
    return False

  # False if multiple query fields.
  if url.count('&') >= 1:
    return False

  if 'MediaWiki:' in url or 'Special:' in url:
    return False

  # We accept images whatever the user regex is
  if '/images/' in url :
    if not config.fetch_images:
      return False
    return True;

  # We accept css and js whatever the user regex is
  if '/skins/' in url:
    return True

  if config.url_regex != None:
    r = re.compile(r'%s' % config.url_regex, re.UNICODE|re.M)
    if not r.search(url):
      logging.debug("rejecting url  %s" % url)
      return False

  return True




def parse_html(doc, url, config):
  """
  Returns (modified_doc, new_urls), where new_urls are absolute URLs for
  all links we want to spider in the HTML.
  """
  BEGIN_COMMENT_REPLACE = '<BEGINCOMMENT-' + str(random.random()) + '>'
  END_COMMENT_REPLACE   = '<ENDCOMMENT-' + str(random.random()) + '>'

  new_urls = []  
  if TIDY:
    options = dict(output_xhtml=1, wrap=0)
    doc = str(tidy.parseString(doc, **options))

  # Temporarily "get rid" of comments so htmldata will find the URLs
  # in the funky "<!--[if" HTML hackery for IE.
  doc = doc.replace('<!--', BEGIN_COMMENT_REPLACE)
  doc = doc.replace('-->', END_COMMENT_REPLACE)

  L = htmldata.urlextract(doc, url, 'text/html')
  for item in L:
    u = item.url
    if should_follow(url, u, config):
      # Store url locally.
      new_urls += [u]
      item.url = url_to_relative(u, url, config)
    else:
      item.url = rewrite_external_url(item.url, config)

  newdoc = htmldata.urljoin(doc, L)
  newdoc = newdoc.replace(BEGIN_COMMENT_REPLACE, '<!--')
  newdoc = newdoc.replace(END_COMMENT_REPLACE, '-->')
  newdoc = newdoc.replace('<br>', '<br/>')
  newdoc = post_html_transform(newdoc, url, config)
  return (newdoc, new_urls)
  

def run(config, out=sys.stdout):
  """
  Code interface.
  """
  if urlparse.urlparse(config.rooturl)[1].lower().endswith('wikipedia.org'):
    logging.critical('Please do not use robots with the Wikipedia site.\n')
    logging.critical('Instead, install the Wikipedia database locally and use mw2html on\n')
    logging.critical('your local installation.  See the Mediawiki site for more information.\n')
    sys.exit(1)

  # Number of files saved
  n = 0

  if not config.overwrite and os.path.exists(config.outdir):
    logging.critical('Error: Directory exists: ' + str(config.outdir) )
    sys.exit(1)

  complete = set()
  pending  = set([config.rooturl])

  while len(pending) > 0:
    url      = pending.pop()
    if url in complete:
      continue
    complete.add(url)
    try:
      f        = urllib2.urlopen(url)
    except urllib2.URLError, e:
      try:
        logging.info(str(e.code) + ': ' + url + '\n\n')
      except:
        logging.warning('Error opening: ' + url + '\n\n')
      continue
    doc      = f.read()
    mimetype = f.info().type.lower().split(' ')[0]
    f.close()

    new_urls = []

    if mimetype == 'text/html':
      (doc, new_urls) = parse_html(doc, url, config)
    elif mimetype == 'text/css':
      (doc, new_urls) = parse_css(doc, url, config)

     #new_urls = []
    # Enqueue URLs that we haven't yet spidered.
    for u in new_urls:
      if u not in complete:
        # Strip off any #section link.
        if '#' in u:
          u = u[:u.index('#')]
        pending.add(u)

    mode = ['wb', 'w'][mimetype.startswith('text')]

    # Save modified content to disk.
    filename = url_to_filename(url, config)

    # Make parent directory if it doesn't exist.
    try:
      os.makedirs(os.path.split(filename)[0])
    except OSError, e:
      if e.errno != errno.EEXIST:
        raise

    # Not really needed since we checked that the directory
    # outdir didn't exist at the top of run(), but let's double check.
    if os.path.exists(filename) and not config.overwrite:
      logging.critical('File already exists: ' + str(filename))
      sys.exit(1)

    f = open(filename, mode)
    f.write(doc)
    f.close()

    if url is config.rooturl and not config.one_page:
        one_page = False
    else: 
        one_page = True

    
    # Generate XML TOC only for textual pages.
    if not "/skins/" in url and not '/images/' in url:
        gen_xml_toc(doc, config, one_page, filename)

    out.write(url + '\n => ' + filename + '\n\n')
    n += 1

  logging.info(str(n) + ' file(s) saved\n')


def usage():
  """
  Print command line options.
  """
  usage_str = """
  mw2html url outdir [options]

  Converts an entire Mediawiki site into static HTML.
  Tested only with Mediawiki 1.4beta6 Monobook output.
  WARNING: This is a recursive robot that ignores robots.txt.  Use with care.

    url                  - URL of mediawiki page to convert to static HTML.
    outdir               - Output directory.

    -f, --force          - Overwrite existing files in outdir.
    --no-flatten         - Do not flatten directory structure.
    --no-lower           - Retain original case for output filenames and dirs.
    --no-clean           - Do not clean up filenames (clean replaces
                           non-alphanumeric chars with _, renames math thumbs).
    --no-hack-skin       - Do not modify skin CSS and HTML for looks.
    --no-made-by         - Suppress "generated by" comment in HTML source.
    --no-move-href       - Disable <movehref> tag. [1]
    --no-remove-png      - Retain external link PNG icons.
    --no-remove-history  - Retain image history and links to information.
    --no-images          - Do not fetch images.
    --no-image-dir       - Store image in the same directory tha html.
    --one-page           - Only treat one page
    -l, --left=a.html    - Paste HTML fragment file into left sidebar.
    -t, --top=a.html     - Paste HTML fragment file into top horiz bar.
    -b, --bottom=a.html  - Paste HTML fragment file into footer horiz bar.
    -i, --index=filename - Move given filename in outdir to index.html.
    -r, --title-regex=re - Only follow url matching the regex.
    -R, --title-extract-re
                         - regex used to extract TOC titles from <title> elmt.
                           Default to '([^<]*) - Event-B'
    -v, --verbose        - Show more informations.
    -d, --debug          - Show debugging informations.
    -q, --quiet          - Only show critical errors.

  Example Usage:
    mw2html http://127.0.0.1/mywiki/ out -f -i main_page.html -l sidebar.html

    Freezes wiki into 'out' directory, moves main_page.html => index.html,
    assumes sidebar.html is defined in the current directory.

  [1]. The <movehref> tag.
       Wiki syntax: <html><movehref href="a"></html>...<html></movehref></html>.
       When enabled, this tag will cause all href= attributes inside of it to be
       set to the given location.  This is useful for linking images.
       In MediaWiki, for the <html> tag to work, one needs to enable $wgRawHtml
       and $wgWhitelistEdit in LocalSettings.php.  A <movehref> tag with no href
       field will remove all links inside it.

  """

  print textwrap.dedent(usage_str.strip('\n'))
  sys.exit(1)

def loggerInit(opt):
  logging.basicConfig(level=opt.verbose,format="%(levelname)-8s %(message)s")

def main():
  """
  Command line interface.
  """
  try:
      (opts, args) = getopt.gnu_getopt(sys.argv[1:], 'fl:t:b:i:r:R:vdq',
                   ['force', 'no-flatten', 'no-lower', 'no-clean',
                    'no-hack-skin', 'no-made-by', 'left=',
                    'top=', 'bottom=', 'index=', 'no-move-href',
                    'no-remove-png', 'no-images', 'no-remove-history',
                    'title-regex=', 'no-image-dir', 'title-extract-re=',
                    'one-page',
                    'verbose', 'debug', 'quiet'])
  except getopt.GetoptError:
    usage()

  # Parse non-option arguments
  try:
    (rooturl, outdir) = args
  except ValueError:
    usage()
  config = Config(rooturl=rooturl, outdir=outdir)

  # Parse option arguments
  for (opt, arg) in opts:
    if opt in ['-f', '--force']:
      config.overwrite      = True
    if opt in ['--no-flatten']:
      config.flatten        = False
    if opt in ['--no-lower']:
      config.lower          = False
    if opt in ['--no-clean']:
      config.clean          = False
    if opt in ['--no-hack-skin']:
      config.hack_skin      = False
    if opt in ['--no-made-by']:
      config.made_by        = False
    if opt in ['--no-images']:
      config.fetch_images   = False
    if opt in ['--no-image-dir']:
      config.image_dir   = False
    if opt in ['--one-page']:
      config.one_page   = True
    if opt in ['--no-move-href']:
      config.move_href      = False
    if opt in ['--no-remove-png']:
      config.remove_png     = False
    if opt in ['--no-remove-history']:
      config.remove_history = False
    if opt in ['-l', '--left']:
      config.sidebar        = os.path.abspath(arg)
    if opt in ['-t', '--top']:
      raise NotImplementedError
      config.header         = os.path.abspath(arg)
    if opt in ['-b', '--bottom']:
      config.footer         = os.path.abspath(arg)
    if opt in ['-i', '--index']:
      config.index          = arg
    if opt in ['-r', '--title-regex']:
      config.url_regex      = arg
    if opt in ['-R', '--title-extract-re']:
      config.title_extract_re   = arg
    if opt in ['-v', '--verbose']:
      config.verbose          = logging.INFO
    if opt in ['-d', '--debug']:
      config.verbose          = logging.DEBUG
    if opt in ['-q', '--quiet']:
      config.verbose          = logging.CRITICAL

  if config.one_page:
      config.url_regex = config.rooturl
  loggerInit(config)
  # Run program
  run(config)


if __name__ == '__main__':
  main()