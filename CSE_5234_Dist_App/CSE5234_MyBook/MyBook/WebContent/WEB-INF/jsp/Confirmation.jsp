<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="header.jsp"/>
<div class = 'confirm'>
<h3>Thanks for completing your order!</h3>
<h3>Your confirm code is: ${sessionScope.confirmNum}</h3>
</div>
<h3>Customer Information</h3>
	<table>
    	<tr>
    		<th>Customer Name: </th>
    		<td><c:out value="${sessionScope.order.customerName}" /></td>
    	</tr>
    	<tr>
    		<th>Email Address: </th>
    		<td><c:out value="${sessionScope.order.emailAddress}" /></td>
    	</tr>    	
    </table>
	<h3>Order List</h3>
	<table>
   	<tr>
   		<th>Name</th>
   		<th>Price</th>
   		<th>Quantity</th>
   	</tr>
			<c:forEach items="${sessionScope.order.lineItems}" var="item" varStatus="loop">
				<c:if test="${item.quantity>0}">
				<tr>
				<td><c:out value="${item.itemName}"></c:out></td>
				<td><c:out value="${item.price}"></c:out></td>
				<td><c:out value="${item.quantity}"></c:out></td>
				</tr>
				</c:if>				
			</c:forEach>
	</table>
	<h3>Payment Information</h3>
	<table>
		<tr>
    		<th>Credit Card Number: </th>
    		<td><c:out value="${sessionScope.order.paymentInfo.creditCardNumber}" /></td>
    	</tr>
    	<tr>
    		<th>Expiration Date: </th>
    		<td><c:out value="${sessionScope.order.paymentInfo.expirationDate}" /></td>
    	</tr>
    	<tr>
    		<th>CVV code: </th>
    		<td><c:out value="${sessionScope.order.paymentInfo.cvvCode}" /></td>
    	</tr>
    	<tr>
    		<th>Card Holder Name: </th>
    		<td><c:out value="${sessionScope.order.paymentInfo.cardHolderName}" /></td>
    	</tr>
	</table>
	<h3>Shipping Details</h3>
	<table>
			<tr>
				<th><c:out value="Name: "></c:out></th>
				<td><c:out value="${sessionScope.order.shippingInfo.name}"></c:out></td>
			</tr>
			<tr>
				<th><c:out value="Address Line1: "></c:out></th>
				<td><c:out value="${sessionScope.order.shippingInfo.addressLine1}"></c:out></td>
			</tr>
			<tr>
				<th><c:out value="Address Line2: "></c:out></th>
				<td><c:out value="${sessionScope.order.shippingInfo.addressLine2}"></c:out></td>
			</tr>
			<tr>
				<th><c:out value="City: "></c:out></th> 
				<td><c:out value="${sessionScope.order.shippingInfo.city}"></c:out></td>
			</tr>
			<tr>
				<th><c:out value="State: "></c:out></th>
			 	<td><c:out value="${sessionScope.order.shippingInfo.state}"></c:out></td>
			</tr>
			<tr>
				<th><c:out value="Zip: "></c:out></th>
			 	<td><c:out value = "${sessionScope.order.shippingInfo.zip}"></c:out></td>
			</tr>
	</table>
<jsp:include page="footer.jsp"/>