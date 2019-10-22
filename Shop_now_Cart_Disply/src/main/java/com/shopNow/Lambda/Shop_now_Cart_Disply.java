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

public class Shop_now_Cart_Disply implements RequestHandler<JSONObject, JSONObject> {

	@SuppressWarnings({ "unchecked", "unused" })
	public JSONObject handleRequest(JSONObject input, Context context) {
		
		LambdaLogger logger = context.getLogger();
		logger.log("Invoked JDBCSample.getCurrentTime");

		JSONArray cart_Add_array = new JSONArray();
		JSONObject jsonObject_cartDisplay_result = new JSONObject();

		
		Object userid1 = input.get("userid");
		Object device_id1 = input.get("device_id");
		Object search1 = input.get("search");
		String device_id = device_id1.toString();
		String vendor_id = null;
		long userid ;
		long product_id ;
		
			
		String search = search1.toString();
		String Str_msg=null;
		String sql;
		JSONObject jo_cartInsert = new JSONObject();
		

		if (userid1 == null || userid1 == "") {
			userid = 0;
		} else {
			userid = Long.parseLong(userid1.toString());
		}
		
		if(search==null) {
			
			search="";
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
				
				
			
      
				if(userid!=0) {

					
					 sql = "SELECT wsimcpsn_shopnow.products.*,table1.attribute_value,cart_items.UserId,cart_items.Quantity,SUM(cart_items.Quantity *wsimcpsn_shopnow.products.sale_price) AS total,product_offers.offer_name FROM wsimcpsn_shopnow.products LEFT JOIN \n"
							+ " (SELECT pa.product_id,GROUP_CONCAT(att_group_name,'\":\"',av.att_value) AS attribute_value FROM wsimcpsn_shopnow.product_attributes pa INNER JOIN attributes_value av ON av.id=pa.att_group_val_id INNER JOIN attributes a ON a.id=pa.att_group_id GROUP BY pa.product_id) AS table1 ON wsimcpsn_shopnow.products.id=table1.product_id \n"
							+ " LEFT JOIN wsimcpsn_shopnow.cart_items ON cart_items.ProductId=wsimcpsn_shopnow.products.id  LEFT JOIN wsimcpsn_shopnow.product_offers ON product_offers.product_id=wsimcpsn_shopnow.products.id WHERE cart_items.UserId='"
							+ userid + "' AND wsimcpsn_shopnow.products.name LIKE '%" + search + "%' GROUP BY wsimcpsn_shopnow.products.id";
					
					

					}
					else {
						 sql = "SELECT wsimcpsn_shopnow.products.*,table1.attribute_value,cart_items.UserId,cart_items.Quantity,SUM(cart_items.Quantity *wsimcpsn_shopnow.products.sale_price) AS total,product_offers.offer_name  FROM wsimcpsn_shopnow.products LEFT JOIN \n"
									+ " (SELECT pa.product_id,GROUP_CONCAT(att_group_name,'\":\"',av.att_value) AS attribute_value FROM product_attributes pa INNER JOIN attributes_value av ON av.id=pa.att_group_val_id INNER JOIN attributes a ON a.id=pa.att_group_id GROUP BY pa.product_id) AS table1 ON wsimcpsn_shopnow.products.id=table1.product_id \n"
									+ " LEFT JOIN cart_items ON cart_items.ProductId=wsimcpsn_shopnow.products.id  LEFT JOIN product_offers ON product_offers.product_id=wsimcpsn_shopnow.products.id WHERE cart_items.device_id='"
									+ device_id + "' and wsimcpsn_shopnow.cart_items.UserId='0' AND  wsimcpsn_shopnow.products.name LIKE '%" + search + "%' GROUP BY wsimcpsn_shopnow.products.id";
						 
						

						
					}
				
				
				
				
				Statement stmt1_productid = conn.createStatement();
				ResultSet rs = stmt1_productid.executeQuery(sql);
			
				
				if(rs.next()==false) {
					
					Str_msg = "cart_items are not present";
					jsonObject_cartDisplay_result.put("status", "0");
					jsonObject_cartDisplay_result.put("message", Str_msg);
					return jsonObject_cartDisplay_result;
					
					
				}
				
	
				JSONObject jo_CartItem_Result = new JSONObject();
				JSONArray json_array_CartItem = new JSONArray();

				float total = 0;
			
				
				do {

					JSONObject jo_cartItem = new JSONObject();
					JSONArray json_array_CartItem1 = new JSONArray();
					jo_cartItem.put("Product_id", rs.getString("id"));
					jo_cartItem.put("vendor_product_id", rs.getString("vendor_product_id"));
					jo_cartItem.put("quantity", rs.getString("Quantity"));
					jo_cartItem.put("Sale_price", rs.getFloat("sale_price"));
					jo_cartItem.put("Regular_price", rs.getFloat("regular_price"));
					jo_cartItem.put("name", rs.getString("name"));

					if (rs.getString("offer_name") == null) {

						jo_cartItem.put("offer", "");
					} else {

						jo_cartItem.put("offer", rs.getString("offer_name"));
					}

					jo_cartItem.put("description", rs.getString("description"));
					jo_cartItem.put("total", rs.getInt("total"));
					jo_cartItem.put("image", rs.getString("image"));

			

					if (rs.getString("attribute_value") == null) {

					} else {
						json_array_CartItem1.add(rs.getString("attribute_value"));
						jo_cartItem.put("product_attribute", json_array_CartItem1);

					}

					jo_cartItem.put("product_attribute", json_array_CartItem1);
					total = total + rs.getFloat("total");
					json_array_CartItem.add(jo_cartItem);

				} while (rs.next());

				jo_CartItem_Result.put("Products", json_array_CartItem);
				jo_CartItem_Result.put("Grand Total", total);
				if (total > 1000) {
					jo_CartItem_Result.put("Delivery", "FREE");

				}

				jo_CartItem_Result.put("tax", "0");
				jo_CartItem_Result.put("Currency", "INR");
				jsonObject_cartDisplay_result.put("Cart Items", jo_CartItem_Result);

				
				}
			 catch (Exception e) {
				e.printStackTrace();
				logger.log("Caught exception: " + e.getMessage());
			}
			
		return jsonObject_cartDisplay_result;

	}
}
