package org.jenkinsci.plugins.svnFileListParameter;

/*
 * repository 인증은 SubversionSCM 을 그대로 사용한다. 
 */
 

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.sf.json.JSONObject;


import org.jvnet.localizer.ResourceBundleHolder;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;

import hudson.Extension;
import hudson.FilePath;
import hudson.model.ParameterValue;
import hudson.model.AbstractProject;
import hudson.model.Hudson;
import hudson.model.ParameterDefinition;
import hudson.model.ParameterDefinition.ParameterDescriptor;
import hudson.model.ParametersDefinitionProperty;
import hudson.scm.SubversionSCM;

public class SvnFileListParameterDefinition extends ParameterDefinition
		implements Comparable<SvnFileListParameterDefinition> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -437403546746893357L;
	private final String svnUrl;
	private final UUID uuid;

	@DataBoundConstructor
	public SvnFileListParameterDefinition(String name, String svnUrl, String uuid) {
		super(name, ResourceBundleHolder.get(
				SvnFileListParameterDefinition.class).format("SvnUrlDescription"));

		if (svnUrl == null) {
			throw new IllegalArgumentException("Must not pass null as path.");
		}
		
		this.svnUrl = svnUrl;

		if (uuid == null || uuid.length() == 0) {
			this.uuid = UUID.randomUUID();
		} else {
			this.uuid = UUID.fromString(uuid);
		}
	}
	
	
	@Extension
	public static class DescriptorImpl extends ParameterDescriptor
	{
		@Override
		public String getDisplayName()
		{
			return ResourceBundleHolder.get(
					SvnFileListParameterDefinition.class).format("DisplayName");
		}
	}
	
	
	public List<Map<String, String>> getSvnUpdated() throws IOException, InterruptedException {
		
		
	    AbstractProject context = null;
	    List<AbstractProject> jobs = Hudson.getInstance().getItems(AbstractProject.class);

	    // which project is this parameter bound to? (I should take time to move
	    // this code to Hudson core one day)
	    for(AbstractProject project : jobs) {
	      @SuppressWarnings("unchecked")
	    ParametersDefinitionProperty property = (ParametersDefinitionProperty) project.getProperty(ParametersDefinitionProperty.class);
	      if(property != null) {
	        List<ParameterDefinition> parameterDefinitions = property.getParameterDefinitions();
	        if(parameterDefinitions != null) {
	          for(ParameterDefinition pd : parameterDefinitions) {
	            if(pd instanceof SvnFileListParameterDefinition && ((SvnFileListParameterDefinition) pd).compareTo(this) == 0) {
	              context = project;
	              break;
	            }
	          }
	        }
	      }
	    }
		
	    
	    
    	System.out.println (context.getProperties());
    	
    	SubversionSCM scm = new SubversionSCM(svnUrl);
    	
    	FilePath workpath = context.getSomeWorkspace();
    	
		SVNClientManager aaaa = scm.createSvnClientManager(context);
		System.out.println ("========== START ============");
		
		
		StatusHandler statusHandler = new StatusHandler(workpath.toURI().toString().replaceAll("file:", ""), true);
		
		try {
		 
			aaaa.getStatusClient().doStatus(new File(workpath.toURI()), SVNRevision.HEAD, SVNDepth.INFINITY, true, true, false, false, statusHandler, null);
		
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		System.out.println ("========== END ===========");
		//System.out.println (res);
	    
	   return statusHandler.getResult();
		
		//return null;
	}
	
	
	public String getSvnUrl() {
		return svnUrl;
	}

	@Override
	public int compareTo(SvnFileListParameterDefinition pd) {
		if (pd.uuid.equals(uuid)) {
			return 0;
		}
		return -1;
	}

	@Override
	public ParameterValue createValue(StaplerRequest req, JSONObject jo) {
		
		String svnFiles = jo.getString("svnFiles");
		if (svnFiles != null && !svnFiles.isEmpty()) {
			jo.put("svnFiles", svnFiles.replace(",", " "));
		}
		
		SvnFileListParameterValue svnFileListParameterValue = req.bindJSON(SvnFileListParameterValue.class, jo);
		      
		return svnFileListParameterValue;
		
//		SvnFileListParameterValue value = req.bindJSON(SvnFileListParameterValue.class, jo);
//		value.setSvnFiles("file~~~~");
//		
//		return value;
	}

	@Override
	public ParameterValue createValue(StaplerRequest req) {
		return null;
	}

}
