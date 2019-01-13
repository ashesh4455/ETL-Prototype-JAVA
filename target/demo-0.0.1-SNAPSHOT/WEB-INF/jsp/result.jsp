<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
    
<head>
<title>${title}</title>     
</head>
    
<body>
	        
	<h3>Search Result for : ${search.content}</h3>
	             
	<table>
		<c:forEach var="s" items="${search.fierceNews}">
			<tr>
				               
				<td><b>Title</b>: ${s.title}</td>                
				<td><b>Content</b>: ${s.urlLink}</td>              
			</tr> 
		</c:forEach>
		                
		<tr>
			                    
			<a href="/search">Submit another query</a>                 
		</tr>
		            
	</table>
	    
</body>
</html>