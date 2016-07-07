/**
 * 
 */

//mongodb会对每个文档逐一调用map.js中的function，emit方法返回的是一个MultiValueMap类型的对象，key值
//为这里的this.username,后面的1表示取值，即数组型value中的一个值
function ( ) {
   emit(this.username,this.price);
}


