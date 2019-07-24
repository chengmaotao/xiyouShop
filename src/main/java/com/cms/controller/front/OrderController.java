package com.cms.controller.front;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.jfinal.kit.PropKit;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import com.cms.CommonAttribute;
import com.cms.entity.Cart;
import com.cms.entity.CartItem;
import com.cms.entity.Member;
import com.cms.entity.Order;
import com.cms.entity.OrderItem;
import com.cms.entity.Receiver;
import com.cms.routes.RouteMapping;
import org.apache.commons.lang.time.DateUtils;


/**
 * Controller - 订单
 * 
 * 
 * 
 */
@RouteMapping(url = "/order")
public class OrderController extends BaseController{

	
	/**
	 * 添加
	 */
	public void add(){
		Member currentMember = getCurrentMember();
		Receiver defaultReceiver=new Receiver().dao().findDefault(currentMember.getId());
		if(defaultReceiver==null){
			redirect("/receiver/add");
		}else{
			Cart currentCart = getCurrentCart();
			setAttr("currentCart",currentCart);
			setAttr("defaultReceiver",defaultReceiver);

			String delivery = PropKit.get("delivery");
			String deliveryFee = PropKit.get("deliveryFee");

			setAttr("delivery",delivery);
			setAttr("deliveryFee",deliveryFee);

			BigDecimal totalPrice = currentCart.getTotalPrice();
			setAttr("hasDeliveryFee",false);
			if(totalPrice.compareTo(new BigDecimal(delivery)) < 0){
				totalPrice = totalPrice.add(new BigDecimal(deliveryFee));
				setAttr("hasDeliveryFee",true);
			}

			setAttr("realTotalPrice",totalPrice);
			//currentCart.totalPrice

			render("/templates/"+getTheme()+"/"+getDevice()+"/orderAdd.html");
		}
	}
	
	/**
	 * 保存
	 */
	public void save(){
	    Order order = getModel(Order.class,"",true);
		Member currentMember = getCurrentMember();
		Cart currentCart = getCurrentCart();

		List<CartItem>  cartItems = currentCart.getCartItems();

		if(cartItems == null || cartItems.isEmpty()){
			int pageSize = 20 ;
			setAttr("page",new Order().dao().findPage(1,pageSize,currentMember.getId(),null));

			render("/templates/"+getTheme()+"/"+getDevice()+"/memberOrderList.html");
			return;
		}

		String delivery = PropKit.get("delivery");
		String deliveryFee = PropKit.get("deliveryFee");

		BigDecimal totalPrice = currentCart.getTotalPrice();

		if(totalPrice.compareTo(new BigDecimal(delivery)) < 0){
			totalPrice = totalPrice.add(new BigDecimal(deliveryFee));
		}
		Date now = new Date();
		order.setCreateDate(now);
		order.setModifyDate(now);

		Integer orderExpireTimes = PropKit.getInt("order.expire.time", 30);

		order.setExpire(DateUtils.addMinutes(now, orderExpireTimes));
		order.setAmount(currentCart.getTotalPrice());
		order.setTotalPrice(totalPrice);
		order.setQuantity(currentCart.getQuantity());
		order.setSn(DateFormatUtils.format(now, "yyyyMMddHHmmssSSS")+RandomStringUtils.randomNumeric(5));
		order.setStatus(CommonAttribute.ORDER_STATUS_PENDING_PAYMENT);
		order.setMemberId(currentMember.getId());
		order.save();


		for(CartItem cartItem:cartItems){
			OrderItem orderItem = new OrderItem();
			orderItem.setCreateDate(new Date());
			orderItem.setModifyDate(new Date());
			orderItem.setName(cartItem.getProduct().getName());
			orderItem.setPrice(cartItem.getPrice());
			orderItem.setQuantity(cartItem.getQuantity());
			orderItem.setSn(cartItem.getProduct().getSn());
			orderItem.setImage(cartItem.getProduct().getProductImages().getMinimum());
			orderItem.setOrderId(order.getId());
			orderItem.setProductId(cartItem.getProductId());
			orderItem.save();
		}
		for(CartItem cartItem:cartItems){
			new CartItem().dao().deleteById(cartItem.getId());
		}
		redirect("/payment?orderId="+order.getId());
	}

}
