
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="header.jsp"/>
<form:form modelAttribute="payment" method="post" action="submitPayment" onsubmit="return validateForm()">
<fieldset>
    <table class = "inputs">
    	<tr>
    		<td>Credit Card Number: </td>
    		<td><form:input path="creditCardNumber"/></td>
    	</tr>
    	<tr>
    		<td>Expiration Date: </td>
    		<td><form:input path="expirationDate" /></td>
    	</tr>
    	<tr>
    		<td>CVV code: </td>
    		<td><form:input path="cvvCode" /></td>
    	</tr>
    	<tr>
    		<td>Card Holder Name: </td>
    		<td><form:input path="cardHolderName" /></td>
    	</tr>
    </table>
<input type="submit" value="Pay">
</fieldset>
</form:form>
<script>
function validateForm()
{
	var creditCardNumber=document.forms["payment"]["creditCardNumber"].value;
	var expirationDate=document.forms["payment"]["expirationDate"].value;
	var cvvCode=document.forms["payment"]["cvvCode"].value;
  	var cardHolderName=document.forms["payment"]["cardHolderName"].value;
  	var message = "";
  	var invalid = false;
  	if (!/^\d{16}$/.test(creditCardNumber))
  	{
  		message += "Please input valid credit card number\n";
	    invalid = true;
  	}
  	if (!/^((0[1-9])|(1[0-2]))\/\d{4}$/.test(expirationDate))
  	{
  		message += "Please input valid expiration date\n";
	    invalid = true;
  	}
  	if (!/^\d{3}$/.test(cvvCode))
  	{
  		message += "Please input valid cvv code\n";
	    invalid = true;
  	}
  	if (!/^[A-Za-z][A-Za-z ]{4,34}$/.test(cardHolderName))
  	{
  		message += "Please input valid card holder name\n";
	    invalid = true;
  	}
  	if(invalid){
  		alert(message);
	    return false;
  	}
}
</script>
<jsp:include page="footer.jsp"/>