package com.castorama.integration.backoffice.imp;

import java.util.ArrayList;
import java.util.List;

public class ProjectProperties {
	private String projectName;
	private List<ChunkProperties> chunks;
	private int state;
	private boolean onlyInventory;
	
	public ProjectProperties() {
		chunks = new ArrayList<ChunkProperties>();
		state = -1;
	}
	
	public String getProjectName() {
		return projectName;
	}
	
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	public List<ChunkProperties> getChunks() {
		return chunks;
	}
	
	public void addChunk(ChunkProperties chunk) {
		this.chunks.add(chunk);
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public boolean isOnlyInventory() {
		return onlyInventory;
	}

	public void setOnlyInventory(boolean onlyInventory) {
		this.onlyInventory = onlyInventory;
	}
}
