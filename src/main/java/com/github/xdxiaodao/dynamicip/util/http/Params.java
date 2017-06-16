package com.github.xdxiaodao.dynamicip.util.http;

import java.io.Serializable;

public class Params implements Serializable {
	private static final long serialVersionUID = 2566949764102297356L;
	private String paramName;
	private Object parmaValue;

	public Params() {
	}

	public Params(String paramName, Object parmaValue) {
		this.paramName = paramName;
		this.parmaValue = parmaValue;
	}

	public String getParamName() {
		return this.paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public Object getParmaValue() {
		return this.parmaValue;
	}

	public void setParmaValue(Object parmaValue) {
		this.parmaValue = parmaValue;
	}
}