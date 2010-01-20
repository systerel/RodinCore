#!/usr/bin/env python
import urllib
import os
class ImageDownloader:
    def __init__(self, urlBase, savePath):
        self.urlBase = urlBase
        self.path = savePath
        if not os.path.exists(savePath):
            os.mkdir(savePath)
        self.images = {}    
    def _saveImage(self, url, name):
        self.images[url] = False
        ImageDownload(name, self.urlBase + url, self.path + name)
    def saveImages(self, urls, names):
        for i in range(0, len(urls) ):
            self._saveImage(urls[i], names[i])
 
class ImageDownload:
    def __init__(self,name, url, savePath):
        self.url = url
        self.name = name
        self.percent = 0
        urllib.urlretrieve(url, savePath, lambda x, y,z: (self.downloadUpdate(x,y,z)))
        
    def downloadUpdate(self, x,y,z):
        percent = int(x*y/float(z)*100)
        percent = min(100, percent)
        if percent > self.percent:
            self.percent = percent
            print str(percent) + "% " + self.name
