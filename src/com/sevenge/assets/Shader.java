
package com.sevenge.assets;
/** Shader Asset implementation **/
public class Shader extends Asset {

	public int glID;
	public int type;

	public Shader (int id, int t) {
		glID = id;
		type = t;
	}
}
