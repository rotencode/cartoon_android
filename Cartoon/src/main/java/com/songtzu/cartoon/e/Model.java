package com.songtzu.cartoon.e;

public class Model {
	private String name;// 名称
	private int flag;//
	private int size;// 尺寸
	private String path;// sdcard路径
	private String lRes;// 大图名称
	
	private int resId;
	
	private String rstPath;
	private boolean saved;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getlRes() {
		return lRes;
	}

	public void setlRes(String lRes) {
		this.lRes = lRes;
	}

	public int getResId() {
		return resId;
	}

	public void setResId(int resId) {
		this.resId = resId;
	}

	public String getRstPath() {
		return rstPath;
	}

	public void setRstPath(String rstPath) {
		this.rstPath = rstPath;
	}

	public boolean isSaved() {
		return saved;
	}

	public void setSaved(boolean saved) {
		this.saved = saved;
	}
}
