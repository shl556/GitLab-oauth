<#import "masterTemplate.ftl" as layout />

<@layout.masterTemplate title="Sign In">
    <h2>Sign In</h2>
    <#if message??>
    	<div class="success">
    		${message}
    	</div>
    </#if>
    <#if error??>
    	<div class="error">
    		<strong>Error:</strong> ${error}
    	</div>
    </#if>
    <form action="/login" method="post">
	   <dl>
	     <dt>Username:
	     <dd><input type="text" name="username" size="30" maxlength="50" value="${username!}">
	     <dt>Password:
	     <dd><input type="password" name="password" size="30">
	     <dt><input type="radio" name="autoLogin" value="true"> 七天免登陆
	   </dl>
	   <div class="actions"><input type="submit" value="Sign In"></div>
	 </form>
</@layout.masterTemplate>