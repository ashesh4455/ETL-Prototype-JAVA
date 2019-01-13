package com.quertle.demo.utils;

public class Paginate {
	private Integer pageSize;
	private Integer pageLimit;

	public Paginate(Integer pageLimit, Integer pageSize) {
		this.pageLimit = pageLimit;
		this.pageSize = pageSize;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public Paginate setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	public Integer getPageLimit() {
		return pageLimit;
	}

	public Paginate setPageLimit(Integer pageLimit) {
		this.pageLimit = pageLimit;
		return this;
	}

}