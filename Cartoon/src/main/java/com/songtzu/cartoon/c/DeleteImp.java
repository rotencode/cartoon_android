package com.songtzu.cartoon.c;

public interface DeleteImp {
	/**
	 * show or hide view
	 */
	public void delete();
	/**
	 * define a default callback
	 * @author Carrot
	 *
	 */
	public static class NULL implements DeleteImp{

		@Override
		public void delete() {
			// TODO Auto-generated method stub
			
		}
	}
}
