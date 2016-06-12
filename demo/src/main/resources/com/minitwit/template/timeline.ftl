<#import "masterTemplate.ftl" as layout />

<@layout.masterTemplate title="Timeline">
    <h2>${pageTitle}</h2>
    	
    <#if user??>
         <#-- 用户已登录，且用户在查看其它用户profileUser所发布的信息时显示-->
    	<#if profileUser?? && user.id != profileUser.id>
    		<div class="followstatus">
    		<#-- 判断当前用户是否是profileUser的关注者-->
    		<#if followed>
    		    <#--点击超链接关注或者不关注指定用户-->
    			<a class="unfollow" href="/auth/t/${profileUser.username}/unfollow">Unfollow user</a>
    		<#else>
    			<a class="follow" href="/auth/t/${profileUser.username}/follow">Follow user</a>.
    		</#if>
    		</div>
        <#-- 用户已登录，查看与自己关联的所有用户最近发布的消息时显示-->
    	<#elseif pageTitle != 'Public Timeline'>
    		<div class="twitbox">
        		<h3>What's on your mind ${user.username}?</h3>
        		<#-- 发布消息的消息框-->
        		<form action="/auth/message" method="post">
          		<p><input type="text" name="text" size="60" maxlength="160"><!--
          		--><input type="submit" value="Share">
        		</form>
      		</div>
    	</#if>
    </#if>
    	<#--消息的条件查询框-->
      <div class="twitbox">
      		        <form action="getMessageByCondition" method="post">
      		        关键字：<input type="text" name="keyWord" style="width: 200px;"/> &nbsp;&nbsp;
      		       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 日期：<select name="pubDate" style="width: 200px;">
      		         <option value="1">最近一周</option>
      		         <option value="2">最近两周</option>
      		         <option value="3">最近一月</option>
      		         <option value="4">最近两月</option>
      		        </select>  <input type="submit" value="search">
      		        </form>
      		</div>
   <#-- 显示消息框-->
    <ul class="messages">
    <#if messages??>
    <#list messages as message>
		<li>
		<#--点击超链接查找指定用户所发布的消息-->
		<strong><a href="/t/${message.username}">${message.username}</a></strong>
		${message.text}
		<small>&mdash; ${message.pubDateStr}</small>
 		
        <#if user?? && message.userId==user.id >
		<small><a href="/deleteMessage/${message.id}">delete</a></small>
		<small><a href="/updateMessage/${message.id}">update</a></small>
        </#if>	  
       
<#else>
		<li><em>There're no messages so far.</em>
	</#list>
	<#else>
		<li><em>There're no messages so far.</em>
	</#if>
	</ul>
</@layout.masterTemplate>