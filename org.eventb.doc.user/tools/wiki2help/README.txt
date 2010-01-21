To build the Eclipse Help from the MediaWiki source, 
run the build_eclipse_help Ant script from 
another ant script that sets the following properties:

  dest.dir - where generated docs will be placed
  wiki.domain - the wiki domain (ex:"http://wiki.event-b.org")
  wiki.page - the wiki page (such as url = wiki.domain/wiki.page)
  help.title - the title of the generated html document

As an example, one may adapt his own script from the following one, extracted from
the decomposition plug-in (ch.ethz.eventb.decomposition/doc/tool/build_eclipse_help.xml)


<project name="decomposition-help" default="decomposition-help.generate">
    
    <dirname property="tool.dir" file="${ant.file.decomposition-help}"/>

     <target name="decomposition-help.generate">
        <ant dir="../../../org.eventb.doc.user/tools/wiki2help/"
             antfile="build_eclipse_help.xml" target="generate-help"
             inheritAll="false">
           <property name="dest.dir" value="${tool.dir}/.."/>
           <property name="wiki.domain" value="http://wiki.event-b.org"/>
           <property name="wiki.page" value="Decomposition_Plug-in_User_Guide"/>
           <property name="help.title" value="Decomposition Plug-in User's Guide"/>
        </ant>
    </target>

</project>