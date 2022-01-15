package de.bergtiger.claim.bdo;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;

public class TigerList<T> extends ArrayList<T> {

	/**
	 * 
	 */
	@Serial
	private static final long serialVersionUID = 1L;

	private boolean order = true;
	private int pageSize = 15;
	private int page = 0;

	public TigerList() {
		super();
	}
	
	public TigerList(Collection<? extends T> list) {
		super(list);
	}

	// Page
	/**
	 * Get current Page (Starts with 0)
	 * 
	 * @return page
	 */
	public int getPage() {
		return page;
	}

	/**
	 * Set Page
	 * 
	 * @param page to set
	 */
	public void setPage(int page) {
		if (page <= 0)
			page = 0;
		if (page >= getPageMax())
			page = getPageMax() - 1;
		this.page = page;
	}

	// PageSize
	/**
	 * Get Page size (Elements on each Page)
	 * 
	 * @return amount of elements on a page
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * Set Page size (Elements on each Page)
	 * 
	 * @param pageSize to set
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	// Order
	public boolean getOrder() {
		return order;
	}

	public void setOrder(boolean order) {
		this.order = order;
	}

	/**
	 * Get Max Page
	 * 
	 * @return max pages
	 */
	public int getPageMax() {
		return (size() / pageSize) + ((size() % pageSize != 0) ? 1 : 0);
	}

}
