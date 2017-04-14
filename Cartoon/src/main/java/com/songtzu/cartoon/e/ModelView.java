package com.songtzu.cartoon.e;

import com.songtzu.cartoon.v.MagicImageView;
import com.songtzu.cartoon.v.Tab;

public class ModelView {
	public MagicImageView imageView;
	public Tab tab;
	public Model model;
	private boolean inited;
	
	public ModelView(MagicImageView _iv, Tab _tab, Model _m) {
		imageView = _iv;
		tab = _tab;
		model = _m;
	}

	public boolean isInited() {
		return inited;
	}

	public void setInited(boolean inited) {
		this.inited = inited;
	}
}
