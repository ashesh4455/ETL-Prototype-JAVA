
<html>
    <head>
		<title>${title}</title>
    </head>
    <body>
        <h3>Enter Search Query</h3>
        <form method="POST" action="/question/search"  modelAttribute="questionSearch">
             <table>
                <tr>
                    <td><label path="content">Query: </label></td>
                    <td><input path="content" name="content"/></td>
                </tr>                
                <tr>
                    <td><input type="submit" value="Submit"/></td>
                </tr>
            </table>
        </form>
    </body>
</html>