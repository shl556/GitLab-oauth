<#import "masterTemplate.ftl" as layout />

<@layout.masterTemplate title="Timeline">
    <h2>${pageTitle}</h2>
    
    <#if user??>
    		<div class="twitbox">
        		<h3>Dear ${user.username}, You can update your message here where you publish it at ${message.pubDateStr}! </h3>
        		<#-- 修改消息的消息框-->
        		<form action="/updateMessage" method="post">
          		<p><input type="text" name="text" size="60" maxlength="160" value="${message.text}">
          		<input type="hidden" name="id" value="${message.id}"/>
          		<input type="submit" value="Update">
        		</form>
      		</div>
    </#if>
  
</@layout.masterTemplate>