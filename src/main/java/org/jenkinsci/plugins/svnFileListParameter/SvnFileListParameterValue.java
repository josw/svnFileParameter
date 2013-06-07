package org.jenkinsci.plugins.svnFileListParameter;

import java.util.Map.Entry;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.export.Exported;

import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.ParameterValue;
import hudson.util.VariableResolver;

public class SvnFileListParameterValue extends ParameterValue {

	private String svnFiles;
	private String svnUrl;

	private boolean isRev;
	private String revision;

	@DataBoundConstructor
	public SvnFileListParameterValue(String name, boolean isRev,
			String revision, String svnFiles) {
		super(name);
		System.out.println("STEP ============= 2");
		this.svnUrl = svnUrl;
		this.svnFiles = svnFiles;
		this.isRev = isRev;
		this.revision = revision;

	}

	@Override
	public void buildEnvVars(AbstractBuild<?, ?> build, EnvVars env) {

		System.out.println("STEP ============= 3");

		if (getSvnFiles() != null)
			env.put(getName(), getSvnFiles());
		
		env.put("isRev", isRev()?"true":"false");
		env.put("revision", getRevision());

		for (Entry<String, String> entry : env.entrySet()) {

			System.out.println(entry.getKey() + ":" + entry.getValue());

		}

	}

	public boolean isRev() {
		return isRev;
	}

	public void setRev(boolean isRev) {
		this.isRev = isRev;
	}

	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	public String getSvnFiles() {

		System.out.println("STEP ============= 5");

		return svnFiles;
	}

	public void setSvnFiles(String svnFiles) {

		System.out.println("STEP ============= 4");

		this.svnFiles = svnFiles;
	}

	@Override
	public VariableResolver<String> createVariableResolver(
			AbstractBuild<?, ?> build) {

		System.out.println("STEP ============= 1");

		return new VariableResolver<String>() {
			public String resolve(String name) {
				return SvnFileListParameterValue.this.name.equals(name) ? getSvnFiles()
						: null;
			}
		};
	}

	@Override
	public String toString() {
		return "(SvnFileListParameterValue) " + getName()
				+ ": Repository URL='" + svnUrl + "' Files='" + svnFiles + "'";
	}

}
