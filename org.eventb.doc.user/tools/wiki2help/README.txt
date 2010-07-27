To build the HTML pages from the MediaWiki source, 
run the build_eclipse_help Ant script from 
another ant script that sets the following properties:

  dest.dir - where generated docs will be placed
  wiki.url - the wiki url (ex:"http://wiki.event-b.org/index.php/Decomposition_Plug-in_User_Guide")

As an example, one may adapt his own script from the following one, extracted from
the decomposition plug-in (ch.ethz.eventb.decomposition.documentation/eclipsehelp/build_eclipse_help.xml)

<project name="decomposition-help" default="decomposition-help.generate">
	
     <target name="decomposition-help.generate">
        <ant dir="../../org.eventb.doc.user/tools/wiki2help/"
        	 antfile="build_eclipse_help.xml" target="generate"
        	 inheritAll="false">
           <property name="dest.dir" value="${ant.file.decomposition-help}/../contents"/>
           <property name="wiki.url" value="http://wiki.event-b.org/index.php/Decomposition_Plug-in_User_Guide"/>
     	</ant>
     	
     	<replaceregexp match="href=&quot;contents" replace="href=&quot;eclipsehelp/contents" flags="g">
     		<fileset dir="${ant.file.decomposition-help}/../contents" includes="decomposition_plug_in_user_guide.xml"/>
     	</replaceregexp>
    </target>

</project>