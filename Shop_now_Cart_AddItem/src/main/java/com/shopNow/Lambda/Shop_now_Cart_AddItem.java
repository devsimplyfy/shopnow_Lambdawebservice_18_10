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
//import org.springframework.jdbc.support.rowset.SqlRowSet;

public class Shop_now_Cart_AddItem implements RequestHandler<JSONObject, JSONObject> {

	@SuppressWarnings({ "unchecked", "unused" })
	public JSONObject handleRequest(JSONObject input, Context context) {

		LambdaLogger logger = context.getLogger();

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
			userid = 0;
		} else {
			
			userid = Long.parseLong(userid1.toString());
		}

		if (product_id1 == null || product_id1 == "") {

			product_id = 0;
		} else {
			product_id = Long.parseLong(product_id1.toString());

		}
		if (quantity1 == null || quantity1 == "") {
			quantity = 0;
			Str_msg = "Cart_Item not Inserted because quantity is not valid";
			jsonObject_cartAdd_result.put("status", "0");
			jsonObject_cartAdd_result.put("message", Str_msg);
			return jsonObject_cartAdd_result;

		} else {
			quantity = Integer.parseInt(quantity1.toString());
		}
		if ((device_id1 == null || device_id1 == "") && userid == 0) {
			
			Str_msg = "Please Enter either UserId or Device_id";
			jo_cartInsert.put("status", "0");
			jo_cartInsert.put("message", Str_msg);
			return jo_cartInsert;
		}
		

		

		

			// Get time from DB server
			try {
				
				Connection conn = DriverManager.getConnection(url, username, password);

				Statement stmt1_productid = conn.createStatement();
				ResultSet resultSet_productId = stmt1_productid.executeQuery(
						"SELECT * FROM wsimcpsn_shopnow.products where id='" + product_id + "'AND stock='true'");

				if (resultSet_productId.next() == false) {

					Str_msg = "Either product id is not valid or the product is currently not available";
					jo_cartInsert.put("status", "0");
					jo_cartInsert.put("message", Str_msg);

					return jo_cartInsert;

				}
				else {
				
					vendor_id=resultSet_productId.getString("vendor_id");
				
				}
				if (userid == 0) {

					String sql2 = "SELECT UserId FROM wsimcpsn_shopnow.cart_items where productId='" + product_id
							+ "' and device_id='" + device_id + "'";

					
					Statement stmt2 = conn.createStatement();
					ResultSet Cart_product_id = stmt2.executeQuery(sql2);

					if (Cart_product_id.next()) {
						long uid = Cart_product_id.getLong("UserId");
						if (uid > 0) {
							String sql3 = "INSERT  INTO wsimcpsn_shopnow.cart_items (device_id,UserId,ProductId,VendorId,Quantity) VALUES('"
									+ device_id + "','" + userid + "','" + product_id + "','" + vendor_id + "'," + quantity
									+ ") ON DUPLICATE KEY UPDATE  Quantity =" + quantity ;
							
							
							Statement stmt3 = conn.createStatement();
							int i = stmt3.executeUpdate(sql3);

							if (i > 0) {
								Str_msg = "CartItem inserted successfully";
								jo_cartInsert.put("status", "1");
								jo_cartInsert.put("message", Str_msg);

							}

							else {
								Str_msg = "Cart_Item not Inserted";
								jo_cartInsert.put("status", "0");
								jo_cartInsert.put("message", Str_msg);

							}

							return jo_cartInsert;

						}

						Str_msg = "product is present in cart please call Cart/update web service";
						jo_cartInsert.put("status", "0");
						jo_cartInsert.put("message", Str_msg);

						return jo_cartInsert;

					} else {

						String sql4 = "INSERT  INTO wsimcpsn_shopnow.cart_items (device_id,UserId,ProductId,VendorId,Quantity) VALUES('"
								+ device_id + "','" + userid + "','" + product_id + "','" + vendor_id + "'," + quantity
								+ ") ON DUPLICATE KEY UPDATE  Quantity =" + quantity ;
						
						
						Statement stmt4 = conn.createStatement();
						int i = stmt4.executeUpdate(sql4);

						if (i > 0) {
							Str_msg = "CartItem inserted successfully";
							jo_cartInsert.put("status", "1");
							jo_cartInsert.put("message", Str_msg);

						}

						else {
							Str_msg = "Cart_Item not Inserted";
							jo_cartInsert.put("status", "0");
							jo_cartInsert.put("message", Str_msg);

						}

						return jo_cartInsert;

					}

				} else {

					String sql5 = "SELECT id FROM wsimcpsn_shopnow.customers where id='" + userid + "'";
					
					Statement stmt5 = conn.createStatement();
					ResultSet srs_customer_id = stmt5.executeQuery(sql5);

					String sql6 = "SELECT * FROM wsimcpsn_shopnow.cart_items where UserId='" + userid
							+ "' and ProductId=" + product_id;
					
					
					Statement stmt6 = conn.createStatement();
					ResultSet user_item_cart = stmt6.executeQuery(sql6);

					String sql7 = "SELECT * FROM wsimcpsn_shopnow.cart_items where UserId=0  and ProductId='"
							+ product_id + "' and device_id='" + device_id + "'";
					
					
					
					Statement stmt7 = conn.createStatement();
					ResultSet user_item_cart1 = stmt7.executeQuery(sql7);

					if (srs_customer_id.next() == false) {

						Str_msg = "user is not valid";
						jo_cartInsert.put("status", "0");
						jo_cartInsert.put("message", Str_msg);

						return jo_cartInsert;

					}

					// ------------------------------------------

					else if (user_item_cart.next()) {

						Str_msg = "product is present in cart please call Cart/update web service";
						jo_cartInsert.put("status", "0");
						jo_cartInsert.put("message", Str_msg);

						return jo_cartInsert;

					}

					// --------------------------------------------
					else if (user_item_cart1.next()) {

						String sql8 = "UPDATE wsimcpsn_shopnow.cart_items SET UserId='" + userid + "' WHERE ProductId='"
								+ product_id + "' AND device_id='" + device_id + "' and UserId = 0";

						
						Statement stmt8 = conn.createStatement();
						boolean i = stmt8.execute(sql8);

						// jdbcTemplate.execute(sql1);

						Str_msg = "CartItem update successfully";
						jo_cartInsert.put("status", "1");
						jo_cartInsert.put("message", Str_msg);
						
						return jo_cartInsert;

					}

					// ---------------------------------------------------

					else {
						

						String sql9 = "INSERT  INTO wsimcpsn_shopnow.cart_items (device_id,UserId,ProductId,VendorId,Quantity) VALUES('"
								+ device_id + "','" + userid + "','" + product_id + "','" + vendor_id + "','" + quantity
								+ "') ON DUPLICATE KEY UPDATE  Quantity ='" + quantity + "'" ;
					

						Statement stmt9 = conn.createStatement();
						int i = stmt9.executeUpdate(sql9);

						if (i >0) {
							Str_msg = "CartItem inserted successfully";
							jo_cartInsert.put("status", "1");
							jo_cartInsert.put("message", Str_msg);

						}

						else {
							Str_msg = "Cart_Item not Inserted";
							jo_cartInsert.put("status", "0");
							jo_cartInsert.put("message", Str_msg);

						}

					}

					return jo_cartInsert;

				}

			} catch (Exception e) {

				logger.log("Exception "+ e);

			}
		 
		return jo_cartInsert;
	}

}


