package com.shl.mongodb;

public class PageInfo {
	/**
	 * 总记录数
	 */
	private int total;
	/**
	 * 总页数
	 */
	private int totalPages;
	/**
	 * 每页记录数
	 */
	private int pageNum;

	/**
	 * 当前页码
	 */
	private int currentPageNum;
	/**
	 * 目标页起始记录数
	 */
	private int start;
	/**
	 * 目标也截止记录数
	 */
	private int end;

	public PageInfo() {

	}

	public PageInfo(int total, int pageNum) {
		this.pageNum = pageNum;
		this.total = total;
		this.totalPages = total % pageNum == 0 ? total / pageNum : total / pageNum + 1;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getCurrentPageNum() {
		return currentPageNum;
	}

	public void setCurrentPageNum(int currentPageNum) {
		this.currentPageNum = currentPageNum;
		start = (currentPageNum - 1) * pageNum + 1;
		end = currentPageNum * pageNum;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

}
