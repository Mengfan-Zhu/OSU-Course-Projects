package edu.osu.cse5234.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.osu.cse5234.business.view.Inventory;
import edu.osu.cse5234.business.view.InventoryService;
import edu.osu.cse5234.business.view.Item;
import edu.osu.cse5234.bussiness.OrderProcessingServiceBean;
import edu.osu.cse5234.model.*;
import edu.osu.cse5234.util.ServiceLocator;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/purchase")
public class PurchaseController {
	@RequestMapping(method = RequestMethod.GET)
	public String viewOrderEntryForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// get inventory
		InventoryService inventoryService = ServiceLocator.getInventoryService();
		Inventory inventory = inventoryService.getAvailableInventory();
		request.setAttribute("inventory", inventory);
		// check whether to prompt resubmit
		if(request.getSession().getAttribute("resubmit") == null) {
			request.getSession().setAttribute("resubmit", false);
		}
		request.setAttribute("inventory", inventory);		
		// set order object
		Order order = new Order();
		List<LineItem> lineItems = new ArrayList<LineItem>();
		List<Item> items = inventory.getItems();
		for (int i = 0; i < items.size(); i++) {
			lineItems.add(new LineItem());
		}
		order.setLineItems(lineItems);
		request.setAttribute("order", order);
		return "OrderEntryForm";
	}

	@RequestMapping(path = "/submitItems", method = RequestMethod.POST)
	public String submitItems(@ModelAttribute("order") Order order, HttpServletRequest request) {
		request.getSession().setAttribute("order", order);
		OrderProcessingServiceBean orderProcessingServiceBean = ServiceLocator.getOrderProcessingService();
		boolean isValid = orderProcessingServiceBean.validateItemAvailability(order);
		if(isValid) {
			return "redirect:/purchase/paymentEntry";
		}else {
			request.getSession().setAttribute("resubmit", true);
			return "redirect:/purchase";
		}
		
	}
	
	@RequestMapping(path = "/paymentEntry", method = RequestMethod.GET)
	public String viewPaymentEntryForm(HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute("payment", new PaymentInfo());	
		return "PaymentEntryForm";
	}
	
	@RequestMapping(path = "/submitPayment", method = RequestMethod.POST)
	public String submitPayment(@ModelAttribute("payment") PaymentInfo paymentInfo, HttpServletRequest request) {
		Order order = (Order) request.getSession().getAttribute("order");
		order.setPaymentInfo(paymentInfo);
		return "redirect:/purchase/shippingEntry";
	}
	
	@RequestMapping(path = "/shippingEntry", method = RequestMethod.GET)
	public String viewShippingEntryForm(HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute("shipping", new ShippingInfo());	
		return "ShippingEntryForm";
	}
	
	@RequestMapping(path = "/submitShipping", method = RequestMethod.POST)
	public String submitShipping(@ModelAttribute("shipping") ShippingInfo shippingInfo, HttpServletRequest request) {
		Order order = (Order) request.getSession().getAttribute("order");
		order.setShippingInfo(shippingInfo);
		return "redirect:/purchase/viewOrder";
	}
	
	@RequestMapping(path = "/viewOrder", method = RequestMethod.GET)
	public String viewOrder(HttpServletRequest request, HttpServletResponse response) {	
		return "ViewOrder";
	}
	
	@RequestMapping(path = "/confirmOrder", method = RequestMethod.POST)
	public String confirmOrder(PaymentInfo paymentInfo, HttpServletRequest request) {
		OrderProcessingServiceBean orderProcessingServiceBean = ServiceLocator.getOrderProcessingService();
		Order order = (Order) request.getSession().getAttribute("order");
		String confirmNum = orderProcessingServiceBean.processOrder(order);
		if(confirmNum == "Error") {
			request.getSession().setAttribute("resubmit", true);
			return "redirect:/purchase";
		}
		request.getSession().setAttribute("confirmNum", confirmNum);		
		return "redirect:/purchase/viewConfirmation";
	}
	
	@RequestMapping(path = "/viewConfirmation", method = RequestMethod.GET)
	public String viewConfirmation(HttpServletRequest request, HttpServletResponse response) {		
		return "Confirmation";
	}
}
