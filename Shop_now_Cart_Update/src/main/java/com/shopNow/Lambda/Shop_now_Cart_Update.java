package com.shopNow.Lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Shop_now_Cart_Update implements RequestHandler<JSONObject, JSONObject> {

	@SuppressWarnings({ "unchecked", "unused" })
	public JSONObject handleRequest(JSONObject input, Context context) {

		LambdaLogger logger = context.getLogger();
		logger.log("Invoked JDBCSample.getCurrentTime");

		JSONArray cart_Add_array = new JSONArray();
		JSONObject jsonObject_cartAdd_result = new JSONObject();

		Object userid1 = input.get("userid");
		Object product_id1 = input.get("product_id");
		Object quantity1 = input.get("quantity");
		Object device_id1 = input.get("device_id");
		String device_id = device_id1.toString();
		String vendor_id = null;
		long userid;
		int quantity;
		long product_id;

		String Str_msg;
		JSONObject jo_cartInsert = new JSONObject();

		if (userid1 == null || userid1 == "") {
			userid =  0;
		} else {
			
			userid = Long.parseLong(userid1.toString());
		}

		if (product_id1 == null || product_id1 == "") {

			product_id = (long) 0;
		} else {
			product_id = Long.parseLong(product_id1.toString());

		}
		if (quantity1 == null || quantity1 == "") {
			quantity = 0;

		} else {
			quantity = Integer.parseInt(quantity1.toString());
		}
		if ((device_id1 == null || device_id1 == "") && userid == 0) {
			// device_id=null;
			
			Str_msg = "Please Enter either UserId or Device_id";
			jo_cartInsert.put("status", "0");
			jo_cartInsert.put("message", Str_msg);
			return jo_cartInsert;
		}

		// Get time from DB server
		try {
			
			Connection conn = DriverManager.getConnection(url, username, password);

			

			// Changing-------------------------------------------------------

			if (userid == 0) {

				String sql1 = "SELECT * FROM wsimcpsn_shopnow.cart_items where ProductId='" + product_id
						+ "'AND device_id='" + device_id + "' and UserId='" + userid + "'";

				

				Statement stmt1 = conn.createStatement();
				ResultSet resultSet1 = stmt1.executeQuery(sql1);

				if (resultSet1.next() == false) {

					Str_msg = "Product is not Added in cart please call Cart/ADD web service";
					jo_cartInsert.put("status", "0");
					jo_cartInsert.put("message", Str_msg);

					return jo_cartInsert;

				} else if (quantity == 0) {

					String sql2 = "Delete from wsimcpsn_shopnow.cart_items where device_id='" + device_id
							+ "' and ProductId='" + product_id + "'";
				
					Statement stmt2 = conn.createStatement();

					int i = stmt2.executeUpdate(sql2);

					if (i > 0) {

						Str_msg = "Removed product from  cart ";
						jo_cartInsert.put("status", "1");
						jo_cartInsert.put("message", Str_msg);

						return jo_cartInsert;
					} else {

						Str_msg = " Not Removed product from  cart ";
						jo_cartInsert.put("status", "0");
						jo_cartInsert.put("message", Str_msg);

						return jo_cartInsert;

					}

				} else {

					String sql3 = "UPDATE wsimcpsn_shopnow.cart_items SET Quantity=" + quantity + " WHERE ProductId='"
							+ product_id + "' AND device_id='" + device_id + "'";

					Statement stmt3 = conn.createStatement();
					int i = stmt3.executeUpdate(sql3);

					if (i > 0) {

						Str_msg = "CartItem updated successfully";
						jo_cartInsert.put("status", "1");
						jo_cartInsert.put("message", Str_msg);
					} else {

						Str_msg = "CartItem not updated successfully";
						jo_cartInsert.put("status", "0");
						jo_cartInsert.put("message", Str_msg);

					}
				}
				return jo_cartInsert;

			}

			else {

				String sql4 = "SELECT * FROM wsimcpsn_shopnow.cart_items where UserId='0'  and ProductId='" + product_id
						+ "' and device_id='" + device_id + "'";


				String sql5 = "SELECT * FROM wsimcpsn_shopnow.cart_items where ProductId='" + product_id
						+ "'AND UserId='" + userid + "'";
		

				Statement stmt4 = conn.createStatement();
				ResultSet resultSet4 = stmt4.executeQuery(sql4);

				Statement stmt5 = conn.createStatement();
				ResultSet resultSet5 = stmt5.executeQuery(sql5);

				if (resultSet4.next()) {

					String sql6 = "UPDATE wsimcpsn_shopnow.cart_items SET Userid='" + userid + "'  WHERE ProductId='"
							+ product_id + "' AND device_id='" + device_id + "'";


					Statement stmt6 = conn.createStatement();
					int i = stmt6.executeUpdate(sql6);

					if (i > 0) {

						Str_msg = "CartItem update successfully";
						jo_cartInsert.put("status", "1");
						jo_cartInsert.put("message", Str_msg);
						return jo_cartInsert;
					} else {

						Str_msg = "CartItem not update successfully";
						jo_cartInsert.put("status", "0");
						jo_cartInsert.put("message", Str_msg);
						return jo_cartInsert;

					}
				}

				else if (resultSet5.next() == false) {

					Str_msg = "Product is not Added in cart please call Cart/ADD web service";
					jo_cartInsert.put("status", "0");
					jo_cartInsert.put("message", Str_msg);

					return jo_cartInsert;

				} else if (quantity == 0) {

					String sql7 = "Delete from wsimcpsn_shopnow.cart_items where  UserId='" + userid
							+ "' and ProductId='" + product_id + "'";

					Statement stmt7 = conn.createStatement();

					int i = stmt7.executeUpdate(sql7);

					if (i > 0) {

						Str_msg = "Removed product from  cart ";
						jo_cartInsert.put("status", "1");
						jo_cartInsert.put("message", Str_msg);

						return jo_cartInsert;
					} else {

						Str_msg = " Not Removed product from  cart ";
						jo_cartInsert.put("status", "0");
						jo_cartInsert.put("message", Str_msg);

						return jo_cartInsert;

					}

				}

				else {
					
					String sql8 = "UPDATE wsimcpsn_shopnow.cart_items SET Quantity=" + quantity + " WHERE ProductId='"
							+ product_id + "' AND UserId='" + userid + "'";

	

					Statement stmt8 = conn.createStatement();
					int i = stmt8.executeUpdate(sql8);

					if (i > 0) {
						Str_msg = "CartItem updated successfully";
						jo_cartInsert.put("status", "1");
						jo_cartInsert.put("message", Str_msg);
						return jo_cartInsert;

					} else {

						Str_msg = "CartItem not updated successfully";
						jo_cartInsert.put("status", "0");
						jo_cartInsert.put("message", Str_msg);
						return jo_cartInsert;

					}

				}

			}

		} catch (Exception e) {
          logger.log("Exception "+e);
		
		}

		
		return jo_cartInsert;
	}

}

