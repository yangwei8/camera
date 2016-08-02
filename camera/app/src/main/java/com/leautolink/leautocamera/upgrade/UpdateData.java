package com.leautolink.leautocamera.upgrade;

import java.io.File;
import java.io.Serializable;

public class UpdateData implements Serializable {
	private int command;
	private String version;
	private String note;
	private String url;
	
	public int getCommand() {
		return command;
	}
	public void setCommand(int command) {
		this.command = command;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
//		this.url = url.toLowerCase();
		this.url = url;
	}
	public String getApkFileName(){
		int pos = url.lastIndexOf(File.separatorChar);
		return url.substring(pos+1);
	}
	@Override
	public String toString() {
		return "UpdateMessage [info=" + command + ", version=" + version
				+ ", note=" + note + ", url=" + url + "]";
	}
}
