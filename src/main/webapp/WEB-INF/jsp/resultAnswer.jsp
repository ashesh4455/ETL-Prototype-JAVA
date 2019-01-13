<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
    
<head>
<title>${title}</title>     
</head>
    
<body>
	        
	<h3>Search Result for : ${questionSearch.content}</h3>
	             
	<table>
		<c:forEach var="s" items="${questionSearch.question}">
			<tr>
				 <c:forEach var="a" items="${s.answers}" varStatus="loop">              
				<td><b>Answer <c:out value="${loop.index}" /></b>: ${a}</td>     
				</c:forEach>               
			</tr> 
		</c:forEach>
		                
		<tr>
			                    
			<a href="/search">Submit another query</a>                 
		</tr>
		            
	</table>
	    
</body>
</html>