package com.rays.pro4.Model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

//import org.apache.log4j.Logger;

import com.rays.pro4.Bean.CartBean;
import com.rays.pro4.Bean.UserBean;
import com.rays.pro4.Exception.ApplicationException;
import com.rays.pro4.Exception.DatabaseException;
import com.rays.pro4.Exception.DuplicateRecordException;
import com.rays.pro4.Exception.RecordNotFoundException;
import com.rays.pro4.Util.DataUtility;
import com.rays.pro4.Util.EmailBuilder;
import com.rays.pro4.Util.EmailMessage;
import com.rays.pro4.Util.EmailUtility;
import com.rays.pro4.Util.JDBCDataSource;

public class CartModel {
	public Integer nextPK() throws Exception {

		int pk = 0;

		Connection conn = JDBCDataSource.getConnection();

		PreparedStatement pstmt = conn.prepareStatement("select max(id) from st_cart");

		ResultSet rs = pstmt.executeQuery();

		while (rs.next()) {
			pk = rs.getInt(1);
		}

		rs.close();

		return pk + 1;
	}

	public long add(CartBean bean) throws Exception {

		int pk = 0;

		Connection conn = JDBCDataSource.getConnection();

		pk = nextPK();

		conn.setAutoCommit(false);

		PreparedStatement pstmt = conn.prepareStatement("insert into st_cart values(?,?,?,?,?)");

		pstmt.setInt(1, pk);
		pstmt.setString(2, bean.getCustomerName());
		pstmt.setString(3, bean.getProduct());
		pstmt.setDate(4, new java.sql.Date(bean.getTransactionDate().getTime()));
		pstmt.setString(5, bean.getQuantity());

		int i = pstmt.executeUpdate();
		System.out.println("Cart Add Successfully " + i);
		conn.commit();
		pstmt.close();

		return pk;
	}

	public void delete(CartBean bean) throws Exception {

		Connection conn = JDBCDataSource.getConnection();

		conn.setAutoCommit(false);

		PreparedStatement pstmt = conn.prepareStatement("delete from st_cart where id = ?");

		pstmt.setLong(1, bean.getId());

		int i = pstmt.executeUpdate();
		System.out.println("Cart delete successfully " + i);
		conn.commit();

		pstmt.close();
	}

	public void update(CartBean bean) throws Exception {

		Connection conn = JDBCDataSource.getConnection();

		conn.setAutoCommit(false); // Begin transaction

		PreparedStatement pstmt = conn.prepareStatement(
				"update st_cart set customerName = ?, product = ?, transactionDate = ?, quantity = ? where id = ?");

		pstmt.setString(1, bean.getCustomerName());
		pstmt.setString(2, bean.getProduct());
		pstmt.setDate(3, new java.sql.Date(bean.getTransactionDate().getTime()));
		pstmt.setString(4, bean.getQuantity());
		pstmt.setLong(5, bean.getId());

		int i = pstmt.executeUpdate();

		System.out.println("Cart update successfully " + i);

		conn.commit();
		pstmt.close();

	}

	public CartBean findByPK(long pk) throws Exception {

		String sql = "select * from st_cart where id = ?";
		CartBean bean = null;

		Connection conn = JDBCDataSource.getConnection();
		PreparedStatement pstmt = conn.prepareStatement(sql);

		pstmt.setLong(1, pk);

		ResultSet rs = pstmt.executeQuery();

		while (rs.next()) {

			bean = new CartBean();
			bean.setId(rs.getLong(1));
			bean.setCustomerName(rs.getString(2));
			bean.setProduct(rs.getString(3));
			bean.setTransactionDate(rs.getDate(4));
			bean.setQuantity(rs.getString(5));

		}

		rs.close();

		return bean;
	}

	public List search(CartBean bean, int pageNo, int pageSize) throws Exception {

		StringBuffer sql = new StringBuffer("select * from st_cart where 1=1");
		if (bean != null) {

			if (bean.getCustomerName() != null && bean.getCustomerName().length() > 0) {
				sql.append(" AND customerName like '" + bean.getCustomerName() + "%'");
			}

			if (bean.getProduct() != null && bean.getProduct().length() > 0) {
				sql.append(" AND product like '" + bean.getProduct() + "%'");
			}

			if (bean.getQuantity() != null && bean.getQuantity().length() > 0) {
				sql.append(" AND quantity like '" + bean.getQuantity() + "%'");
			}

			if (bean.getTransactionDate() != null && bean.getTransactionDate().getTime() > 0) {
				Date d = new java.sql.Date(bean.getTransactionDate().getTime());
				sql.append(" AND transactionDate like '" + d + "%'");
			}
			if (bean.getId() > 0) {
				sql.append(" AND id = " + bean.getId());
			}

		}

		if (pageSize > 0) {

			pageNo = (pageNo - 1) * pageSize;

			sql.append(" Limit " + pageNo + ", " + pageSize);

		}

		System.out.println("sql query search >>= " + sql.toString());
		List list = new ArrayList();

		Connection conn = JDBCDataSource.getConnection();
		PreparedStatement pstmt = conn.prepareStatement(sql.toString());

		ResultSet rs = pstmt.executeQuery();

		while (rs.next()) {

			bean = new CartBean();
			bean.setId(rs.getLong(1));
			bean.setCustomerName(rs.getString(2));
			bean.setProduct(rs.getString(3));
			bean.setTransactionDate(rs.getDate(4));
			bean.setQuantity(rs.getString(5));

			list.add(bean);

		}
		rs.close();

		return list;

	}

	public List list() throws Exception {

		ArrayList list = new ArrayList();

		StringBuffer sql = new StringBuffer("select * from st_cart");

		Connection conn = JDBCDataSource.getConnection();

		PreparedStatement pstmt = conn.prepareStatement(sql.toString());
		ResultSet rs = pstmt.executeQuery();

		while (rs.next()) {

			CartBean bean = new CartBean();

			bean.setId(rs.getLong(1));
			bean.setCustomerName(rs.getString(2));
			bean.setProduct(rs.getString(3));
			bean.setTransactionDate(rs.getDate(4));
			bean.setQuantity(rs.getString(5));

			list.add(bean);

		}

		rs.close();

		return list;
	}

}