<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="header.jsp"/>
<form:form modelAttribute="shipping" method="post" action="submitShipping" onsubmit="return validateForm()">
    <table class = "inputs">
    	<tr>
    		<td>Name: </td>
    		<td><form:input path="name" /></td>
    	</tr>
    	<tr>
    		<td>Address Line 1: </td>
    		<td><form:input path="addressLine1" /></td>
    	</tr>
    	<tr>
    		<td>Address Line 2: </td>
    		<td><form:input path="addressLine2" /></td>
    	</tr>
    	<tr>
    		<td>City: </td>
    		<td><form:input path="city" /></td>
    	</tr>
    	<tr>
    		<td>State: </td>
    		<td><form:input path="state" /></td>
    	</tr>
    	<tr>
    		<td>Zip: </td>
    		<td><form:input path="zip" /></td>
    	</tr>
    </table>
	<input type="submit" value="Confirm">
</form:form>
<script>
function validateForm()
{
	var name=document.forms["shipping"]["name"].value;
	var addressLine1=document.forms["shipping"]["addressLine1"].value;
  	var city=document.forms["shipping"]["city"].value;
  	var state=document.forms["shipping"]["state"].value;
  	var zip=document.forms["shipping"]["zip"].value;
  	var message = "";
  	var invalid = false;
  	if (!/^[A-Za-z][A-Za-z ]{4,34}$/.test(name))
  	{
  		message += "Please input valid name\n";
	    invalid = true;
  	}
  	if (addressLine1.length < 10)
  	{
  		message += "Please input valid address\n";
	    invalid = true;
  	}
  	if (!/^[A-Za-z]+$/.test(city))
  	{
  		message += "Please input valid city name\n";
	    invalid = true;
  	}
  	if (!/^[A-Za-z]+$/.test(state))
  	{
  		message += "Please input valid state name\n";
	    invalid = true;
  	}
  	if (!/^\d{5}$/.test(zip))
  	{
  		message += "Please input valid zip code\n";
	    invalid = true;
  	}
  	if(invalid){
  		alert(message);
	    return false;
  	}
}
</script>
<jsp:include page="footer.jsp"/>