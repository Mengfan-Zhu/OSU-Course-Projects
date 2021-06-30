<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="header.jsp"/>
<c:if test="${resubmit == true}">
	<div class = "alert alert-danger message" role="alert">
		<p>Please resubmit item quantities!</p>
	</div>
</c:if>
<form:form modelAttribute="order" method="post" action="purchase/submitItems" onsubmit="return validateForm()">
 	<table class = "inputs">
    	<tr>
    		<td>Customer Name: </td>
    		<td><form:input path="customerName"/></td>
    	</tr>
    	<tr>
    		<td>Email Address: </td>
    		<td><form:input path="emailAddress" /></td>
    	</tr>    	
    </table>
    <table class = "inputs">
   	<tr>
   		<th>Name</th>
   		<th>Price</th>
   		<th>Quantity</th>
   	</tr>
	<c:forEach items="${inventory.items}" var="item" varStatus="loop">
		<tr>
			<td><c:out value="${item.name}"></c:out></td>
			<td><c:out value="$${item.price}" ></c:out></td>
			<td><form:input path="lineItems[${loop.index}].quantity"/></td>
			<td class = "hidden"><form:hidden path="lineItems[${loop.index}].itemName" value="${item.name}"/></td>
			<td class = "hidden"><form:hidden path="lineItems[${loop.index}].price" value="${item.price}"/></td>
			<td class = "hidden"><form:hidden path="lineItems[${loop.index}].itemNumber" value="${item.itemNumber}"/></td>
		</tr>
	</c:forEach>
    </table>
	<input type="submit" value="Purchase">
</form:form>
<script>
function validateForm()
{
	var list = document.getElementsByTagName("input");
	var customerName=document.forms["order"]["customerName"].value;
	var emailAddress=document.forms["order"]["emailAddress"].value;
	var nonzero = false;
	var message = "";
  	var invalid = false;
	if (!/^[A-Za-z][A-Za-z ]{4,34}$/.test(customerName))
  	{
  		message += "Please input valid name\n";
	    invalid = true;
  	}
	if (!/^[A-Za-z0-9][A-Za-z0-9_\-\.]*\@[A-Za-z0-9_\-\.]+\.[A-Za-z]{2,4}$/.test(emailAddress))
  	{
  		message += "Please input valid email address\n";
	    invalid = true;
  	}
	for(var i=2;i<list.length;i++)
    {
           if(list[i].type=="text")  
           {
           	var quantity = list[i].value;
               if(quantity.length!=0 && !/^((0)|([1-9][0-9]{0,2}))$/.test(quantity)){
             		message += "Please input valid quantity\n";
               }else{
	               	if(/^[1-9][0-9]{0,2}$/.test(quantity)){
	               		nonzero = true;
	               	}
               }
           }
    } 
	if(!nonzero){
		message += "You don't order any book!\n";
	} 
	if(invalid){
  		alert(message);
	    return false;
  	}
}
</script>
<jsp:include page="footer.jsp"/>