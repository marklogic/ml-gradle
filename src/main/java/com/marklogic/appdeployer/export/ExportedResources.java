package com.marklogic.appdeployer.export;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExportedResources {

	private List<File> files;
	private List<String> messages;

	public ExportedResources(List<File> files, String... messages) {
		this.files = files;
		this.messages = new ArrayList<>();
		if (messages != null) {
			for (String s : messages) {
				this.messages.add(s);
			}
		}
	}

	public void add(ExportedResources resources) {
		List<File> otherFiles = resources.getFiles();
		if (otherFiles != null) {
			for (File file : resources.getFiles()) {
				if (!this.files.contains(file)) {
					this.files.add(file);
				}
			}
		}

		for (String message : resources.getMessages()) {
			if (!this.messages.contains(message)) {
				this.messages.addAll(resources.getMessages());
			}
		}
	}

	public List<File> getFiles() {
		return files;
	}

	public void setFiles(List<File> files) {
		this.files = files;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
}
